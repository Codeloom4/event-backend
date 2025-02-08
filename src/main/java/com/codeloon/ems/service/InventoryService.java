package com.codeloon.ems.service;

import com.codeloon.ems.dto.InventoryDto;
import com.codeloon.ems.dto.UserDto;
import com.codeloon.ems.model.DataTableBean;
import com.codeloon.ems.util.ResponseBean;

import java.util.List;

public interface InventoryService {
    List<InventoryDto> getAllInventory();

    DataTableBean getInventoryByName(String name, int page, int size);

    ResponseBean createInventory(InventoryDto inventory);

    ResponseBean updateInventory(Long InventoryId, InventoryDto inventory);

    ResponseBean deleteInventory(Long inventoryId);

}
