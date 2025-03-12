package com.codeloon.ems.controller;

import com.codeloon.ems.dto.InventoryDto;
import com.codeloon.ems.dto.UserDto;
import com.codeloon.ems.model.DataTableBean;
import com.codeloon.ems.model.EventBean;
import com.codeloon.ems.service.InventoryService;
import com.codeloon.ems.util.ResponseBean;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/ems/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;

    @GetMapping("/getall")
    public ResponseEntity<?> getAllInventory() {
        ResponseEntity<?> responseEntity;
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ResponseBean responseBean = new ResponseBean();
        DataTableBean dataTableBean = new DataTableBean();
        try {
            dataTableBean = inventoryService.getAllInventory();
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

    @GetMapping("/{name}")
    public ResponseEntity<?> getInventoryByName(@PathVariable String name, @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        ResponseEntity<?> responseEntity;
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ResponseBean responseBean = new ResponseBean();
        try {
            DataTableBean dataTableBean = inventoryService.getInventoryByName(name, page, size);
            httpStatus = HttpStatus.OK;
            responseBean.setContent(dataTableBean);
        } catch (Exception ex) {
            log.error("Error occurred while retrieving inventory.{} ", ex.getMessage());
        } finally {
            responseEntity = new ResponseEntity<>(responseBean, httpStatus);
        }
        return responseEntity;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createInventory(@RequestBody InventoryDto inventory) {
        ResponseEntity<?> responseEntity;
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ResponseBean responseBean = new ResponseBean();
        try {
            responseBean = inventoryService.createInventory(inventory);
            httpStatus = HttpStatus.CREATED;
        } catch (Exception ex) {
            log.error("Error occurred while saving new inventory.{} ", ex.getMessage());
        } finally {
            responseEntity = new ResponseEntity<>(responseBean, httpStatus);
        }
        return responseEntity;
    }

    @PutMapping("/{inventoryId}")
    public ResponseEntity<?> updateInventory(@PathVariable Long inventoryId, @RequestBody InventoryDto inventory) {
        ResponseEntity<?> responseEntity;
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ResponseBean responseBean = new ResponseBean();
        try {
            responseBean = inventoryService.updateInventory(inventoryId, inventory);
            httpStatus = HttpStatus.CREATED;
        } catch (Exception ex) {
            log.error("Error occurred while saving inventory.{} ", ex.getMessage());
        } finally {
            responseEntity = new ResponseEntity<>(responseBean, httpStatus);
        }
        return responseEntity;
    }

    @DeleteMapping("/{inventoryId}")
    public ResponseEntity<?> deleteInventory(@PathVariable Long inventoryId) {
        return ResponseEntity.ok(inventoryService.deleteInventory(inventoryId));
    }
}
