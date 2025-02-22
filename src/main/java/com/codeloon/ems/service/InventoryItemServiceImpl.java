package com.codeloon.ems.service;

import com.codeloon.ems.dto.InventoryItemDto;
import com.codeloon.ems.entity.Inventory;
import com.codeloon.ems.entity.InventoryItem;
import com.codeloon.ems.entity.User;
import com.codeloon.ems.model.DataTableBean;
import com.codeloon.ems.model.InventoryItemBean;
import com.codeloon.ems.repository.InventoryItemRepository;
import com.codeloon.ems.repository.UserRepository;
import com.codeloon.ems.util.ResponseBean;
import com.codeloon.ems.util.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public ResponseBean createItem(InventoryItemDto inventoryItemDto) {
        ResponseBean responseBean = new ResponseBean();
        String msg = "";
        String code = ResponseCode.RSP_ERROR;

        try {

            InventoryItem inventoryItemEntity = InventoryItem.builder()
                    .itemName(inventoryItemDto.getItemName())
                    .isRefundable(inventoryItemDto.getIsRefundable())
                    .updatedAt(LocalDateTime.now())
                    .createdUser(inventoryItemDto.getCreatedUser())
                    .build();

            inventoryItemRepository.saveAndFlush(inventoryItemEntity);

            code = ResponseCode.RSP_SUCCESS;
            msg = "Item created successfully.";
            log.info("Item created  successfully. Item ID : {}, item Name : {}", inventoryItemEntity.getId(),
                    inventoryItemEntity.getItemName());//

        }catch (Exception ex) {
            log.error("Error occurred while creating item", ex);
            msg = "Error occurred while creating item.";
        } finally {
            responseBean.setResponseMsg(msg);
            responseBean.setResponseCode(code);
            responseBean.setContent(inventoryItemDto);
        }
        return responseBean;
    }

    @Override
    public ResponseBean updateItem(InventoryItemDto inventoryItemDto) {
        ResponseBean responseBean = new ResponseBean();
        String msg = "";
        String code = ResponseCode.RSP_ERROR;

        try {

            Optional<InventoryItem> inventoryItemOptional = inventoryItemRepository.findById(inventoryItemDto.getId());
            if(inventoryItemOptional.isPresent()){
                Optional<User> getSystemUser = userRepository.findByUsername(inventoryItemDto.getCreatedUser());
                InventoryItem inventoryItemEntity = inventoryItemOptional.get();

                inventoryItemEntity = InventoryItem.builder()
                        .itemName(inventoryItemDto.getItemName())
                        .isRefundable(inventoryItemDto.getIsRefundable())
                        .updatedAt(inventoryItemDto.getUpdatedAt())
                        .createdUser(inventoryItemDto.getCreatedUser())
                        .avgPrice(inventoryItemDto.getAvgPrice())
                        .quantity(inventoryItemDto.getQuantity())
                        .build();

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
            responseBean.setContent(inventoryItemDto);
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
}
