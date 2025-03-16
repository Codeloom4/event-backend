package com.codeloon.ems.service;

import com.codeloon.ems.dto.InventoryItemDto;
import com.codeloon.ems.model.OrderAccessBean;
import com.codeloon.ems.util.ResponseBean;

public interface OrderRequestservice {

    ResponseBean accessAndLoad(String packid);

    ResponseBean createOrderRequest(OrderAccessBean orderAccessBean);
}
