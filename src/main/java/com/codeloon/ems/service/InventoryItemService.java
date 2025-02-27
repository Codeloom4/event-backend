package com.codeloon.ems.service;

import com.codeloon.ems.dto.InventoryDto;
import com.codeloon.ems.dto.InventoryItemDto;
import com.codeloon.ems.model.DataTableBean;
import com.codeloon.ems.model.EventBean;
import com.codeloon.ems.model.InventoryItemBean;
import com.codeloon.ems.util.ResponseBean;

import java.util.List;

public interface InventoryItemService {

    ResponseBean getAllInventoryItems();

    ResponseBean createItem(InventoryItemDto inventoryItemDto);
    ResponseBean updateItem(InventoryItemDto inventoryItemDto);
    ResponseBean deleteItem(Long inventoryItemId);

    DataTableBean getItemsList();

}
