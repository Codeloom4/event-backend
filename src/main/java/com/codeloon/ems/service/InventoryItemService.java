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

    ResponseBean getAllItemsByCategory(String category);

    ResponseBean createInventoryItem(InventoryItemDto inventoryItemDto);

    ResponseBean createOtherItems(InventoryItemDto inventoryItemDto);
    ResponseBean updateItem(Long itemId, InventoryItemDto inventoryItemDto);

    ResponseBean updateOtherItem(Long itemId, InventoryItemDto inventoryItemDto);
    ResponseBean deleteItem(Long inventoryItemId);

    DataTableBean getItemsList();

    DataTableBean getOtherItemList(String category);


}
