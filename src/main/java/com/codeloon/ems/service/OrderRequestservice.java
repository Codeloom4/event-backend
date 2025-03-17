package com.codeloon.ems.service;

import com.codeloon.ems.dto.InventoryItemDto;
import com.codeloon.ems.model.DataTableBean;
import com.codeloon.ems.model.OrderAccessBean;
import com.codeloon.ems.util.ResponseBean;

public interface OrderRequestservice {

    ResponseBean accessAndLoad(String packid);

    ResponseBean createOrderRequest(OrderAccessBean orderAccessBean);

    DataTableBean customerList(int page, int size);

    DataTableBean orderReqList(int page, int size);

    ResponseBean viewOrderDetails(String packid);


}
