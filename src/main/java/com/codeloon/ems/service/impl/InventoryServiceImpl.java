package com.codeloon.ems.service.impl;

import com.codeloon.ems.dto.InventoryDto;
import com.codeloon.ems.dto.SystemBeanDto;
import com.codeloon.ems.entity.Event;
import com.codeloon.ems.entity.Inventory;
import com.codeloon.ems.entity.InventoryItem;
import com.codeloon.ems.entity.User;
import com.codeloon.ems.model.DataTableBean;
import com.codeloon.ems.model.EventBean;
import com.codeloon.ems.model.InventoryItemBean;
import com.codeloon.ems.repository.InventoryItemRepository;
import com.codeloon.ems.repository.InventoryRepository;
import com.codeloon.ems.repository.UserRepository;
import com.codeloon.ems.service.InventoryService;
import com.codeloon.ems.util.ResponseBean;
import com.codeloon.ems.util.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;
    private final InventoryItemRepository inventoryItemRepository;

    @Autowired
    private SystemBeanDto systemBeanDto;

    @Override
    public DataTableBean getAllInventory() {
        DataTableBean dataTableBean = new DataTableBean();
        List<Object> inventoryDtoList = new ArrayList<>();
        int page = 0;
        int size = 10;
        String code = ResponseCode.RSP_ERROR;
        Pageable pageable = PageRequest.of(page, size);
        try {
            Page<Inventory> inventoryList = inventoryRepository.findAll(pageable);
            inventoryList.forEach(inventory -> {
                InventoryDto inventoryDto = new InventoryDto();
                BeanUtils.copyProperties(inventory, inventoryDto);
                inventoryDtoList.add(inventoryDto);
            });
            dataTableBean.setPagecount(inventoryList.getTotalPages());
            dataTableBean.setCount(inventoryList.getTotalElements());
        } catch (Exception ex) {
            log.error("Error occurred while retrieving all inventory details", ex);
        } finally {
            dataTableBean.setMsg("Success");
            dataTableBean.setCode(ResponseCode.RSP_SUCCESS);
            dataTableBean.setList(inventoryDtoList);

        }
        return dataTableBean;
    }

    @Override
    public DataTableBean getInventoryByName(String itemName, int page, int size) {
        DataTableBean dataTableBean = new DataTableBean();
        String msg = "";
        String code = ResponseCode.RSP_ERROR;

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("itemName").ascending());
//            Page<Object[]> inventoryList = inventoryRepository.searchInventoryByName(itemName, pageable);
            Page<Inventory> inventoryList = inventoryRepository.findByItemNameContaining(itemName, pageable);

            if(!inventoryList.isEmpty()){
                List<Object> inventoryDataList = this.mapSearchData(inventoryList);

                dataTableBean.setList(inventoryDataList);
                dataTableBean.setCount(inventoryList.getTotalElements());
                dataTableBean.setPagecount(inventoryList.getTotalPages());

                msg = "Inventory searched successfully.";
                code = ResponseCode.RSP_SUCCESS;
            }else {
                dataTableBean.setCount(0);
                dataTableBean.setPagecount(0);

                log.warn("Inventory item with iemName '{}' not found", itemName);
                msg = "Inventory not found: " + itemName;
            }

        } catch (Exception ex) {
            log.error("Error occurred while searching inventory : {}", ex.getMessage(), ex);
            msg = "Error occurred while searching inventory.";
        }

        dataTableBean.setMsg(msg);
        dataTableBean.setCode(code);
        return dataTableBean;
    }


    @Override
    public ResponseBean createInventory(InventoryDto inventory) {
        ResponseBean responseBean = new ResponseBean();
        String msg = "";
        String code = ResponseCode.RSP_ERROR;

        try {
            Long startingBcode = 0L;
            Long endingBcode = 0L;

            Optional<User> getSystemUser = userRepository.findByUsername(systemBeanDto.getSysUser());
            Optional<InventoryItem> getInventoryItem = inventoryItemRepository.findById(inventory.getItemId());
            if (getInventoryItem.isPresent()){
                InventoryItem inventoryItem = getInventoryItem.get();

                Inventory inventory1 = new Inventory();

                inventory1 = this.convertToEntity(inventory);

                inventory1.setCreatedAt(LocalDateTime.now());
                inventory1.setCreatedUser(getSystemUser.get());
                inventory1.setBalanceQuantity(inventory.getOrderQuantity());
                inventory1.setTotalAmount(Double.valueOf(inventory.getOrderQuantity() * inventory.getPurchasePrice()));

                //Inventory getLastInventory = inventoryRepository.findTopByOrderByIdDesc();

                Long lastBarcode = inventoryRepository.findTopByOrderByIdDesc().getEndBarcode() != 0 ?
                        inventoryRepository.findTopByOrderByIdDesc().getEndBarcode() : 1000000000L;

                startingBcode = lastBarcode + 1;
                endingBcode = startingBcode + inventory.getOrderQuantity() - 1;
                inventory1.setEndBarcode(endingBcode);
                inventory1.setStartBarcode(startingBcode);

                inventoryRepository.saveAndFlush(inventory1);
                inventoryItem.setAvgPrice((inventoryItem.getAvgPrice() + inventory1.getSalesPrice())/2);
                inventoryItem.setQuantity(inventoryItem.getQuantity() + inventory1.getOrderQuantity());
                inventoryItemRepository.saveAndFlush(inventoryItem);

                code = ResponseCode.RSP_SUCCESS;
                msg = "Inventory created successfully.";
                log.info("Inventory created  successfully. Inventory ID : {}, Inv Name : {}", inventory1.getId(),
                        inventory1.getItemName());
            }else {
                code = ResponseCode.RSP_ERROR;
                msg = "Invalid item id";
                log.info("Inventory created  failed due to invalid item id");
            }

        }catch (Exception ex) {
            log.error("Error occurred while adding inventory", ex);
            msg = "Error occurred while adding inventory.";
        } finally {
            responseBean.setResponseMsg(msg);
            responseBean.setResponseCode(code);
            responseBean.setContent(null);
        }
        return responseBean;
    }

    @Override
    public ResponseBean updateInventory(Long InventoryId, InventoryDto inventory) {
        ResponseBean responseBean = new ResponseBean();
        String msg = "";
        String code = ResponseCode.RSP_ERROR;

        try {

            Optional<Inventory> inventoryOptional = inventoryRepository.findById(InventoryId);
            if(inventoryOptional.isPresent()){
                Optional<User> getSystemUser = userRepository.findByUsername(systemBeanDto.getSysUser());
                Inventory inventoryEntity = inventoryOptional.get();

                inventoryEntity.setPurchasePrice(inventory.getPurchasePrice());
                inventoryEntity.setSalesPrice(inventory.getSalesPrice());
                inventoryEntity.setOrderQuantity(inventory.getOrderQuantity());
                inventoryEntity.setSalesQuantity(inventory.getSalesQuantity());
                inventoryEntity.setCreatedAt(LocalDateTime.now());
                inventoryEntity.setCreatedUser(getSystemUser.get());

                inventoryRepository.saveAndFlush(inventoryEntity);

                code = ResponseCode.RSP_SUCCESS;
                msg = "Inventory updated successfully.";
                log.info("Inventory updated successfully. Inventory ID : {}, Inv Name : {}", inventoryEntity.getId(),
                        inventoryEntity.getItemName());
            }else {
                log.error("Invalid inventory id");
                msg = "Invalid inventory id.";
            }

        }catch (Exception ex) {
            log.error("Error occurred while updating inventory", ex);
            msg = "Error occurred while updating inventory.";
        } finally {
            responseBean.setResponseMsg(msg);
            responseBean.setResponseCode(code);
            responseBean.setContent(inventory);
        }
        return responseBean;
    }

    @Override
    public ResponseBean deleteInventory(Long inventoryId) {
        ResponseBean responseBean = new ResponseBean();
        String msg = "";
        String code = ResponseCode.RSP_ERROR;
        try {
            Optional<Inventory> inventoryOptional = inventoryRepository.findById(inventoryId);
            if (inventoryOptional.isPresent()) {
                Inventory inventory = inventoryOptional.get();
                inventoryRepository.delete(inventory);

                code = ResponseCode.RSP_SUCCESS;
                msg = "Inventory deleted successfully.";
                log.info("Inventory deleted successfully. Inventory ID : {}, Inv Name : {}", inventory.getId(),
                        inventory.getItemName());
            }else {
                log.error("Invalid inventory id");
                msg = "Invalid inventory id.";
            }
        }
        catch (Exception ex) {
            log.error("Error occurred while deleting inventory", ex);
            msg = "Error occurred while deleting inventory.";
        }
        finally {
            responseBean.setResponseMsg(msg);
            responseBean.setResponseCode(code);
            responseBean.setContent(null);
        }
        return responseBean;
    }


    private List<Object> mapSearchData(Page<Inventory> dataList) {
        List<Object> advanceSearchDataBeanList = new ArrayList<>();
        dataList.forEach(data -> {
            InventoryDto searchDataBean = new InventoryDto();

            searchDataBean.setId(data.getId());
            searchDataBean.setItemName(data.getItemName());
            searchDataBean.setIsRefundable(data.getIsRefundable());
            searchDataBean.setPurchasePrice(data.getPurchasePrice());
            searchDataBean.setSalesPrice(data.getSalesPrice());
            searchDataBean.setOrderQuantity(data.getOrderQuantity());
            searchDataBean.setSalesQuantity(data.getSalesQuantity());
            searchDataBean.setBalanceQuantity(data.getBalanceQuantity());
            searchDataBean.setStartBarcode(String.valueOf(data.getStartBarcode()));
            searchDataBean.setEndBarcode(String.valueOf(data.getEndBarcode()));

            advanceSearchDataBeanList.add(searchDataBean);
        });
        return advanceSearchDataBeanList;
    }


    private InventoryDto convertToDto(Inventory inventory) {
        InventoryDto inventoryDto = new InventoryDto();
        BeanUtils.copyProperties(inventory, inventoryDto);
        return inventoryDto;
    }

    private Inventory convertToEntity(InventoryDto inventoryDto) {
        Inventory inventory = new Inventory();
        BeanUtils.copyProperties(inventoryDto, inventory);
        return inventory;
    }

}
