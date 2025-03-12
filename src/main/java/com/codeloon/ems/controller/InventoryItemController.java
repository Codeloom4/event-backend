package com.codeloon.ems.controller;

import com.codeloon.ems.dto.InventoryDto;
import com.codeloon.ems.dto.InventoryItemDto;
import com.codeloon.ems.model.DataTableBean;
import com.codeloon.ems.model.EventBean;
import com.codeloon.ems.model.InventoryItemBean;
import com.codeloon.ems.service.InventoryItemService;
import com.codeloon.ems.util.ResponseBean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/ems/item")
@RequiredArgsConstructor
public class InventoryItemController {

    private final InventoryItemService inventoryItemService;

    @GetMapping("/itemdropdown")
    public ResponseEntity<?> getAllItems() {
        ResponseEntity<?> responseEntity;
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ResponseBean responseBean = new ResponseBean();

        try {
            responseBean = inventoryItemService.getAllInventoryItems();
            httpStatus = HttpStatus.OK;
        }catch (Exception ex){
            log.error("Error occurred while retrieving inventory Item.{} ", ex.getMessage());
        }finally {
            responseEntity = new ResponseEntity<>(responseBean, httpStatus);
        }
        return responseEntity;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createItem(@RequestBody InventoryItemDto inventoryItemDto) {
        ResponseEntity<?> responseEntity;
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ResponseBean responseBean = new ResponseBean();
        try {
            responseBean = inventoryItemService.createItem(inventoryItemDto);
            httpStatus = HttpStatus.CREATED;
        } catch (Exception ex) {
            log.error("Error occurred while adding new inventory Item.{} ", ex.getMessage());
        } finally {
            responseEntity = new ResponseEntity<>(responseBean, httpStatus);
        }
        return responseEntity;
    }

    @PutMapping
    public ResponseEntity<?> updateItem(@RequestBody InventoryItemDto inventoryItem) {
        return ResponseEntity.ok(inventoryItemService.updateItem(inventoryItem));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<?> deleteItem(@PathVariable Long itemId) {
        return ResponseEntity.ok(inventoryItemService.deleteItem(itemId));
    }


    @GetMapping("/getall")
    public ResponseEntity<?> getAllInventory() {
        ResponseEntity<?> responseEntity;
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ResponseBean responseBean = new ResponseBean();
        DataTableBean dataTableBean = new DataTableBean();
        try {
            dataTableBean = inventoryItemService.getItemsList();
            httpStatus = HttpStatus.OK;
            responseBean.setContent(dataTableBean);
            responseBean.setResponseMsg(dataTableBean.getMsg());
            responseBean.setResponseCode(dataTableBean.getCode());
        }catch (Exception ex){
            log.error("Error occurred while retrieving inventory list.{} ", ex.getMessage());
        }finally {
            responseEntity = new ResponseEntity<>(responseBean, httpStatus);
        }
        return responseEntity;
    }

}
