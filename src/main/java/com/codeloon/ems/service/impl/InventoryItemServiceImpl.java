package com.codeloon.ems.service.impl;

import com.codeloon.ems.dto.InventoryDto;
import com.codeloon.ems.dto.InventoryItemDto;
import com.codeloon.ems.dto.SystemBeanDto;
import com.codeloon.ems.entity.Inventory;
import com.codeloon.ems.entity.InventoryItem;
import com.codeloon.ems.entity.User;
import com.codeloon.ems.model.DataTableBean;
import com.codeloon.ems.model.InventoryItemBean;
import com.codeloon.ems.repository.InventoryItemRepository;
import com.codeloon.ems.repository.UserRepository;
import com.codeloon.ems.service.InventoryItemService;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryItemServiceImpl implements InventoryItemService {

    private final InventoryItemRepository inventoryItemRepository;
    private final UserRepository userRepository;

    @Autowired
    private SystemBeanDto systemBeanDto;

    @Override
    public ResponseBean getAllInventoryItems() {
        ResponseBean responseBean = new ResponseBean();
        List<InventoryItemBean> inventoryItemBeanList = new ArrayList<>();
        List<InventoryItem> inventories = new ArrayList<>();
        String msg = "";
        String code = ResponseCode.RSP_ERROR;
        try {

            inventories = inventoryItemRepository.findAll();
            if (!inventories.isEmpty()) {
                inventories.forEach(data->{
                    InventoryItemBean temp = new InventoryItemBean();
                    temp.setId(data.getId());
                    temp.setItemName(data.getItemName());
                    temp.setCategory(data.getCategory());

                    inventoryItemBeanList.add(temp);
                });
            }
            code = ResponseCode.RSP_SUCCESS;
            msg = "Success";
        } catch (Exception e) {
            throw new RuntimeException(e);

        } finally {
            responseBean.setResponseMsg(msg);
            responseBean.setResponseCode(code);
            responseBean.setContent(inventoryItemBeanList);
        }
        return responseBean;
    }

    @Override
    public ResponseBean getAllItemsByCategory(String category) {
        ResponseBean responseBean = new ResponseBean();
        List<InventoryItem> itemData = new ArrayList<>();
        String msg = "";
        String code = ResponseCode.RSP_ERROR;
        try {

            itemData = inventoryItemRepository.findInventoryItemsByCategory(category);
            code = ResponseCode.RSP_SUCCESS;
            msg = "Success";
        } catch (Exception e) {
            throw new RuntimeException(e);

        } finally {
            responseBean.setResponseMsg(msg);
            responseBean.setResponseCode(code);
            responseBean.setContent(itemData);
        }
        return responseBean;
    }


    @Override
    public DataTableBean getItemsList() {
        DataTableBean dataTableBean = new DataTableBean();
        List<Object> inventoryDtoList = new ArrayList<>();
        int page = 0;
        int size = 10;
        String code = ResponseCode.RSP_ERROR;
        Pageable pageable = PageRequest.of(page, size);
        try {
            Page<InventoryItem> inventoryList = inventoryItemRepository.findAll(pageable);
            inventoryList.forEach(inventory -> {
                InventoryItemDto inventoryItemDto = new InventoryItemDto();
                BeanUtils.copyProperties(inventory, inventoryItemDto);
                inventoryDtoList.add(inventoryItemDto);
            });

            dataTableBean.setPagecount(inventoryList.getTotalPages());
            dataTableBean.setCount(inventoryList.getTotalElements());
        } catch (Exception ex) {
            log.error("Error occurred while retrieving all item list", ex);
        } finally {
            dataTableBean.setMsg("Success");
            dataTableBean.setCode(ResponseCode.RSP_SUCCESS);
            dataTableBean.setList(inventoryDtoList);

        }
        return dataTableBean;
    }

    @Override
    public ResponseBean createInventoryItem(InventoryItemDto inventoryItemDto) {
        ResponseBean responseBean = new ResponseBean();
        String msg = "";
        String code = ResponseCode.RSP_ERROR;

        try {

            InventoryItem inventoryItemEntity = InventoryItem.builder()
                    .itemName(inventoryItemDto.getItemName())
                    .isRefundable(inventoryItemDto.getIsRefundable() != null ? inventoryItemDto.getIsRefundable() : false)
                    .updatedAt(LocalDateTime.now())
                    .createdUser(systemBeanDto.getSysUser())
                    .minOrderQty(inventoryItemDto.getMinOrderQty())
                    .category("Inventory")
                    .avgPrice(0D)
                    .quantity(0)
                    .build();

            inventoryItemRepository.saveAndFlush(inventoryItemEntity);

            code = ResponseCode.RSP_SUCCESS;
            msg = "Item created successfully.";
            log.info("Item created  successfully. Item ID : {}, item Name : {}", inventoryItemEntity.getId(),
                    inventoryItemEntity.getItemName());

        }catch (Exception ex) {
            log.error("Error occurred while creating item", ex);
            msg = "Error occurred while creating item.";
        } finally {
            responseBean.setResponseMsg(msg);
            responseBean.setResponseCode(code);
            responseBean.setContent(null);
        }
        return responseBean;
    }

    @Override
    public ResponseBean updateItem(Long itemId, InventoryItemDto inventoryItemDto) {
        ResponseBean responseBean = new ResponseBean();
        String msg = "";
        String code = ResponseCode.RSP_ERROR;

        try {

            Optional<InventoryItem> inventoryItemOptional = inventoryItemRepository.findById(itemId);
            if(inventoryItemOptional.isPresent()){
                Optional<User> getSystemUser = userRepository.findByUsername(inventoryItemDto.getCreatedUser());
                InventoryItem inventoryItemEntity = inventoryItemOptional.get();

                inventoryItemEntity.setItemName(inventoryItemDto.getItemName());
                inventoryItemEntity.setIsRefundable(inventoryItemDto.getIsRefundable() != null ? inventoryItemDto.getIsRefundable() : false);
                inventoryItemEntity.setUpdatedAt(LocalDateTime.now());
                inventoryItemEntity.setCreatedUser(systemBeanDto.getSysUser());
                inventoryItemEntity.setMinOrderQty(inventoryItemDto.getMinOrderQty());

                inventoryItemRepository.saveAndFlush(inventoryItemEntity);

                code = ResponseCode.RSP_SUCCESS;
                msg = "Inventory item updated successfully.";
                log.info("Inventory item updated successfully. Inventory Item ID : {}, Inventory Item Name : {}", inventoryItemEntity.getId(),
                        inventoryItemEntity.getItemName());
            }else {
                log.error("Invalid inventory id");
                msg = "Invalid inventory id.";
            }

        }catch (Exception ex) {
            log.error("Error occurred while updating inventory item", ex);
            msg = "Error occurred while updating inventory item.";
        } finally {
            responseBean.setResponseMsg(msg);
            responseBean.setResponseCode(code);
            responseBean.setContent(null);
        }
        return responseBean;
    }

    @Override
    public ResponseBean deleteItem(Long inventoryItemId) {
        ResponseBean responseBean = new ResponseBean();
        String msg = "";
        String code = ResponseCode.RSP_ERROR;
        try {
            Optional<InventoryItem> inventoryItemOptional = inventoryItemRepository.findById(inventoryItemId);
            if (inventoryItemOptional.isPresent()) {
                InventoryItem inventory = inventoryItemOptional.get();
                inventoryItemRepository.delete(inventory);

                code = ResponseCode.RSP_SUCCESS;
                msg = "Inventory item deleted successfully.";
                log.info("Inventory item deleted successfully. Inventory Item ID : {}, Inventory Item Name : {}", inventory.getId(),
                        inventory.getItemName());
            }else {
                log.error("Invalid inventory item id");
                msg = "Invalid inventory item id.";
            }
        }
        catch (Exception ex) {
            log.error("Error occurred while deleting inventory item", ex);
            msg = "Error occurred while deleting inventory item.";
        }
        finally {
            responseBean.setResponseMsg(msg);
            responseBean.setResponseCode(code);
            responseBean.setContent(null);
        }
        return responseBean;
    }

    @Override
    public ResponseBean createOtherItems(InventoryItemDto inventoryItemDto) {
        ResponseBean responseBean = new ResponseBean();
        String msg = "";
        String code = ResponseCode.RSP_ERROR;

        try {

            InventoryItem inventoryItemEntity = InventoryItem.builder()
                    .itemName(inventoryItemDto.getItemName())
                    .updatedAt(LocalDateTime.now())
                    .createdUser(systemBeanDto.getSysUser())
                    .minOrderQty(0)
                    .avgPrice(inventoryItemDto.getAvgPrice())
                    .quantity(0)
                    .category(inventoryItemDto.getCategory())
                    .description(inventoryItemDto.getDescription())
                    .isRefundable(false)
                    .build();

            inventoryItemRepository.saveAndFlush(inventoryItemEntity);

            code = ResponseCode.RSP_SUCCESS;
            msg = "Item created successfully.";
            log.info("Item created  successfully. Item ID : {}, item Name : {}", inventoryItemEntity.getId(),
                    inventoryItemEntity.getItemName());

        }catch (Exception ex) {
            log.error("Error occurred while creating item", ex);
            msg = "Error occurred while creating item.";
        } finally {
            responseBean.setResponseMsg(msg);
            responseBean.setResponseCode(code);
            responseBean.setContent(null);
        }
        return responseBean;
    }

    @Override
    public ResponseBean updateOtherItem(Long itemId, InventoryItemDto inventoryItemDto) {
        ResponseBean responseBean = new ResponseBean();
        String msg = "";
        String code = ResponseCode.RSP_ERROR;

        try {

            Optional<InventoryItem> inventoryItemOptional = inventoryItemRepository.findById(itemId);
            if(inventoryItemOptional.isPresent()){
                Optional<User> getSystemUser = userRepository.findByUsername(inventoryItemDto.getCreatedUser());
                InventoryItem inventoryItemEntity = inventoryItemOptional.get();

                inventoryItemEntity.setItemName(inventoryItemDto.getItemName());
                //inventoryItemEntity.setIsRefundable(inventoryItemDto.getIsRefundable());
                inventoryItemEntity.setUpdatedAt(LocalDateTime.now());
                inventoryItemEntity.setCreatedUser(systemBeanDto.getSysUser());
                inventoryItemEntity.setAvgPrice(inventoryItemDto.getAvgPrice());
                inventoryItemEntity.setDescription(inventoryItemDto.getDescription());
                inventoryItemEntity.setCategory(inventoryItemDto.getCategory());

                inventoryItemRepository.saveAndFlush(inventoryItemEntity);

                code = ResponseCode.RSP_SUCCESS;
                msg = "Inventory item updated successfully.";
                log.info("Inventory item updated successfully. Inventory Item ID : {}, Inventory Item Name : {}", inventoryItemEntity.getId(),
                        inventoryItemEntity.getItemName());
            }else {
                log.error("Invalid inventory id");
                msg = "Invalid inventory id.";
            }

        }catch (Exception ex) {
            log.error("Error occurred while updating inventory item", ex);
            msg = "Error occurred while updating inventory item.";
        } finally {
            responseBean.setResponseMsg(msg);
            responseBean.setResponseCode(code);
            responseBean.setContent(null);
        }
        return responseBean;
    }

    @Override
    public DataTableBean getOtherItemList(String category, int page, int size) {
        DataTableBean dataTableBean = new DataTableBean();
        List<Object> inventoryDtoList = new ArrayList<>();
//        int page = 0;
//        int size = 10;
        String code = ResponseCode.RSP_ERROR;
        Pageable pageable = PageRequest.of(page, size);
        try {
            Page<InventoryItem> inventoryList = inventoryItemRepository.findAllByCategory(category, pageable);
            inventoryList.forEach(inventory -> {
                InventoryItemDto inventoryItemDto = new InventoryItemDto();
                BeanUtils.copyProperties(inventory, inventoryItemDto);
                inventoryDtoList.add(inventoryItemDto);
            });

            dataTableBean.setPagecount(inventoryList.getTotalPages());
            dataTableBean.setCount(inventoryList.getTotalElements());
        } catch (Exception ex) {
            log.error("Error occurred while retrieving all item list", ex);
        } finally {
            dataTableBean.setMsg("Success");
            dataTableBean.setCode(ResponseCode.RSP_SUCCESS);
            dataTableBean.setList(inventoryDtoList);

        }
        return dataTableBean;
    }

    @Override
    public DataTableBean SearchItems(InventoryItemDto inventoryItemDto, int page, int size) {
        DataTableBean dataTableBean = new DataTableBean();
        String msg = "";
        String code = ResponseCode.RSP_ERROR;
        Page<InventoryItem> inventoryList = null;

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("itemName").ascending());

            if((inventoryItemDto.getItemName() != null && !inventoryItemDto.getItemName().isEmpty()) &&
                    (inventoryItemDto.getIsRefundable() != null)){

                //search from both
                inventoryList = inventoryItemRepository.findByItemNameAndIsRefundable(inventoryItemDto.getItemName(), inventoryItemDto.getIsRefundable(), pageable);

            } else if((inventoryItemDto.getItemName() != null && !inventoryItemDto.getItemName().isEmpty()) &&
                    (inventoryItemDto.getIsRefundable() == null))  {

                //search only from item name
                inventoryList = inventoryItemRepository.findByItemNameContaining(inventoryItemDto.getItemName(), pageable);

            }else if((inventoryItemDto.getItemName() == null || inventoryItemDto.getItemName().isEmpty()) &&
                    (inventoryItemDto.getIsRefundable() != null))  {

                //search only from is_refundable
                inventoryList = inventoryItemRepository.findByIsRefundable(inventoryItemDto.getIsRefundable(), pageable);

            }

            if(!inventoryList.isEmpty()){
                List<Object> inventoryDataList = this.mapSearchData(inventoryList);

                dataTableBean.setList(inventoryDataList);
                dataTableBean.setCount(inventoryList.getTotalElements());
                dataTableBean.setPagecount(inventoryList.getTotalPages());

                msg = "Item searched successfully.";
                code = ResponseCode.RSP_SUCCESS;
            }else {
                dataTableBean.setCount(0);
                dataTableBean.setPagecount(0);

                log.warn("Item not found");
                msg = "Item not found: ";
            }

        } catch (Exception ex) {
            log.error("Error occurred while searching item : {}", ex.getMessage(), ex);
            msg = "Error occurred while searching item.";
        }

        dataTableBean.setMsg(msg);
        dataTableBean.setCode(code);
        return dataTableBean;
    }

    private List<Object> mapSearchData(Page<InventoryItem> dataList) {
        List<Object> advanceSearchDataBeanList = new ArrayList<>();
        dataList.forEach(data -> {
            InventoryItemDto searchDataBean = new InventoryItemDto();

            searchDataBean.setId(data.getId());
            searchDataBean.setItemName(data.getItemName());
            searchDataBean.setIsRefundable(data.getIsRefundable());
            searchDataBean.setAvgPrice(data.getAvgPrice());
            searchDataBean.setQuantity(data.getQuantity());
            searchDataBean.setMinOrderQty(data.getMinOrderQty());
            searchDataBean.setCategory(data.getCategory());
            searchDataBean.setDescription(data.getDescription());

            advanceSearchDataBeanList.add(searchDataBean);
        });
        return advanceSearchDataBeanList;
    }
}
