package com.codeloon.ems.service;

import com.codeloon.ems.dto.InventoryDto;
import com.codeloon.ems.entity.Inventory;
import com.codeloon.ems.entity.InventoryItem;
import com.codeloon.ems.entity.User;
import com.codeloon.ems.model.DataTableBean;
import com.codeloon.ems.repository.InventoryItemRepository;
import com.codeloon.ems.repository.InventoryRepository;
import com.codeloon.ems.repository.UserRepository;
import com.codeloon.ems.util.ResponseBean;
import com.codeloon.ems.util.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;
    private final InventoryItemRepository inventoryItemRepository;

    @Override
    public List<InventoryDto> getAllInventory() {
        List<InventoryDto> inventoryDtoList = new ArrayList<>();

        try {
            List<Inventory> inventoryList = inventoryRepository.findAll();
            inventoryList.forEach(inventory -> {
                InventoryDto inventoryDto = new InventoryDto();
                BeanUtils.copyProperties(inventory, inventoryDto);
                inventoryDtoList.add(inventoryDto);
            });

        } catch (Exception ex) {
            log.error("Error occurred while retrieving all inventory details", ex);
        }
        return inventoryDtoList;
    }

    @Override
    public DataTableBean getInventoryByName(String itemName, int page, int size) {
        DataTableBean dataTableBean = new DataTableBean();
        String msg = "";
        String code = ResponseCode.RSP_ERROR;

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("itemName").ascending());
            Page<Object[]> inventoryList = inventoryRepository.searchInventoryByName(itemName, pageable);

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
            String startingBcode = ""; //TODO generate Starting Barcode
            String endingBcode = "";  //TODO generate ending Barcode

            Optional<User> getSystemUser = userRepository.findByUsername(inventory.getCreatedUser());
            Optional<InventoryItem> getInventoryItem = inventoryItemRepository.findById(inventory.getItemId());
            InventoryItem inventoryItem = getInventoryItem.get();

            Inventory inventoryEntity = Inventory.builder()
                    .id(2L)
                    .itemName(inventory.getItemName())
                    .itemId(inventoryItem)
                    .startBarcode(startingBcode)
                    .endBarcode(endingBcode)
                    .isRefundable(inventory.getIsRefundable())
                    .purchasePrice(inventory.getPurchasePrice())
                    .salesPrice(inventory.getSalesPrice())
                    .orderQuantity(inventory.getOrderQuantity())
                    .salesQuantity(inventory.getSalesQuantity())
                    .createdAt(LocalDateTime.now())
                    .createdUser(getSystemUser.get())
                    .balanceQuantity(inventory.getOrderQuantity())
                    .totalAmount(Double.valueOf(inventory.getOrderQuantity() * inventory.getPurchasePrice()))
                    .build();

            inventoryRepository.saveAndFlush(inventoryEntity);
            inventoryItem.setAvgPrice((inventoryItem.getAvgPrice() + inventoryEntity.getSalesPrice())/2);
            inventoryItem.setQuantity(inventoryItem.getQuantity() + inventoryEntity.getOrderQuantity());
            inventoryItemRepository.saveAndFlush(inventoryItem);

            code = ResponseCode.RSP_SUCCESS;
            msg = "Inventory created successfully.";
            log.info("Inventory created  successfully. Inventory ID : {}, Inv Name : {}", inventoryEntity.getId(),
                    inventoryEntity.getItemName());

        }catch (Exception ex) {
            log.error("Error occurred while adding inventory", ex);
            msg = "Error occurred while adding inventory.";
        } finally {
            responseBean.setResponseMsg(msg);
            responseBean.setResponseCode(code);
            responseBean.setContent(inventory);
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
                Optional<User> getSystemUser = userRepository.findByUsername(inventory.getCreatedUser());
                Inventory inventoryEntity = inventoryOptional.get();

                inventoryEntity = Inventory.builder()
                        .itemName(inventory.getItemName())
                        .isRefundable(inventory.getIsRefundable())
                        .purchasePrice(inventory.getPurchasePrice())
                        .salesPrice(inventory.getSalesPrice())
                        .orderQuantity(inventory.getOrderQuantity())
                        .salesQuantity(inventory.getSalesQuantity())
                        .createdAt(LocalDateTime.now())
                        .createdUser(getSystemUser.get())
                        .build();

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


    private List<Object> mapSearchData(Page<Object[]> dataList) {
        List<Object> advanceSearchDataBeanList = new ArrayList<>();
        dataList.forEach(data -> {
            InventoryDto searchDataBean = new InventoryDto();

            searchDataBean.setId((Long) data[0]);
            searchDataBean.setItemName((String) data[1]);
            searchDataBean.setIsRefundable((Boolean) data[2]);
            searchDataBean.setPurchasePrice((Long) data[3]);
            searchDataBean.setSalesPrice((Long) data[4]);
            searchDataBean.setOrderQuantity((Integer) data[5]);
            searchDataBean.setSalesQuantity((Integer) data[6]);
            searchDataBean.setBalanceQuantity((Integer) data[7]);
            searchDataBean.setStartBarcode((String) data[8]);
            searchDataBean.setEndBarcode((String) data[9]);

            advanceSearchDataBeanList.add(searchDataBean);
        });
        return advanceSearchDataBeanList;
    }



}
