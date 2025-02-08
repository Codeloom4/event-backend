package com.codeloon.ems.controller;

import com.codeloon.ems.dto.InventoryItemDto;
import com.codeloon.ems.model.EventBean;
import com.codeloon.ems.model.InventoryItemBean;
import com.codeloon.ems.service.InventoryItemService;
import com.codeloon.ems.util.ResponseBean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/ems/item")
@RequiredArgsConstructor
public class InventoryItemController {

    private final InventoryItemService inventoryItemService;
    //TODO get all and all other cruds
    @GetMapping
    public ResponseEntity<List<InventoryItemBean>> getAllEvents() {
        return ResponseEntity.ok(inventoryItemService.getAllInventoryItems());
    }
    @PostMapping
    public ResponseEntity<ResponseBean> createItem(@RequestBody InventoryItemDto inventoryItemDto) {
        return ResponseEntity.ok(inventoryItemService.createItem(inventoryItemDto));
    }

}
