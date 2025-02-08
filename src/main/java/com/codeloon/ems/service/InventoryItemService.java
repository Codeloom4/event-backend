package com.codeloon.ems.service;

import com.codeloon.ems.dto.InventoryDto;
import com.codeloon.ems.dto.InventoryItemDto;
import com.codeloon.ems.model.EventBean;
import com.codeloon.ems.model.InventoryItemBean;
import com.codeloon.ems.util.ResponseBean;

import java.util.List;

public interface InventoryItemService {

    List<InventoryItemBean> getAllInventoryItems();

    ResponseBean createItem(InventoryItemDto inventoryItemDto);

}
