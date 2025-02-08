package com.codeloon.ems.service;

import com.codeloon.ems.dto.InventoryItemDto;
import com.codeloon.ems.entity.InventoryItem;
import com.codeloon.ems.model.InventoryItemBean;
import com.codeloon.ems.repository.InventoryItemRepository;
import com.codeloon.ems.util.ResponseBean;
import com.codeloon.ems.util.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryItemServiceImpl implements InventoryItemService {

    private final InventoryItemRepository inventoryItemRepository;

    @Override
    public List<InventoryItemBean> getAllInventoryItems() {
        List<InventoryItemBean> inventoryItemBeanList;
        try {
            inventoryItemBeanList = inventoryItemRepository.findAllInventoryItems();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return inventoryItemBeanList;
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
}
