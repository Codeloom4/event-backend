package com.codeloon.ems.service.impl;

import com.codeloon.ems.entity.InventoryItem;
import com.codeloon.ems.entity.PackageItem;
import com.codeloon.ems.model.OrderAccessBean;
import com.codeloon.ems.model.OrderItemListBean;
import com.codeloon.ems.repository.*;
import com.codeloon.ems.service.OrderRequestservice;
import com.codeloon.ems.util.ResponseBean;
import com.codeloon.ems.util.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderRequestserviceImpl implements OrderRequestservice {

    private final OrderRequestRepository orderRequestRepository;

    private final OrderRequestDetailRepository orderRequestDetailRepository;

    private final PackageRepository packageRepository;

    private final PackageItemRepository packageItemRepository;

    private final InventoryItemRepository inventoryItemRepository;

    @Override
    public ResponseBean accessAndLoad(String packid) {
        ResponseBean responseBean = new ResponseBean();
        List<PackageItem>  packageItems = new ArrayList<>();
        OrderAccessBean orderAccessBean = new OrderAccessBean();
        List<OrderItemListBean> orderItemListBeanList = new ArrayList<>();

        String msg = "";
        String code = ResponseCode.RSP_ERROR;
        try {

            packageItems = packageItemRepository.findByPackageId(packid);
            if(!packageItems.isEmpty()){
                orderAccessBean.setPackageId(packageItems.get(0).getPackage_id().getId());
                //orderAccessBean.setTotal_price(packageItems.get(0).getPackage_id().getTotal());
                orderAccessBean.setPackageTypeCode(packageItems.get(0).getPackage_id().getPackage_type().getCode());
                orderAccessBean.setPackageTypeDes(packageItems.get(0).getPackage_id().getPackage_type().getDescription());

                packageItems.forEach(packItem ->{
                    OrderItemListBean orderItemListBean = new OrderItemListBean();
                    orderItemListBean.setPackageItemId(packItem.getId());
                    orderItemListBean.setPackageId(packItem.getPackage_id().getId());
                    orderItemListBean.setInventoryItemId(Long.valueOf(packItem.getItemCode()));
                    orderItemListBean.setItemName(packItem.getItemName());
                    orderItemListBean.setQuantity(packItem.getQuantity());
                    orderItemListBean.setSellPrice(packItem.getSellPrice());
                    orderItemListBean.setBulkPrice(packItem.getBulkPrice());

                    orderItemListBeanList.add(orderItemListBean);

                });
                orderAccessBean.setOrderItemListBeanList(orderItemListBeanList);

                code = ResponseCode.RSP_SUCCESS;
                msg = "Success";

            }else {
                code = ResponseCode.RSP_ERROR;
                msg = "Invalid Package id";
            }

        } catch (Exception e) {
            throw new RuntimeException(e);

        } finally {
            responseBean.setResponseMsg(msg);
            responseBean.setResponseCode(code);
            responseBean.setContent(orderAccessBean);
        }
        return responseBean;
    }
}
