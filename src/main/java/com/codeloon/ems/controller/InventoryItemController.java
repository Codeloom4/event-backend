package com.codeloon.ems.controller;

import com.codeloon.ems.dto.InventoryDto;
import com.codeloon.ems.dto.InventoryItemDto;
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

    @GetMapping
    public ResponseEntity<List<InventoryItemBean>> getAllEvents() {
        return ResponseEntity.ok(inventoryItemService.getAllInventoryItems());
    }

    @PostMapping
    public ResponseEntity<ResponseBean> createItem(@RequestBody InventoryItemDto inventoryItemDto) {
        return ResponseEntity.ok(inventoryItemService.createItem(inventoryItemDto));
    }

    @PutMapping
    public ResponseEntity<?> updateItem(@RequestBody InventoryItemDto inventoryItem) {
        return ResponseEntity.ok(inventoryItemService.updateItem(inventoryItem));
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<?> deleteItem(@PathVariable Long itemId) {
        return ResponseEntity.ok(inventoryItemService.deleteItem(itemId));
    }

}
