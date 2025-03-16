package com.codeloon.ems.service.impl;

import com.codeloon.ems.dto.InventoryItemDto;
import com.codeloon.ems.dto.SystemBeanDto;
import com.codeloon.ems.entity.*;
import com.codeloon.ems.entity.Package;
import com.codeloon.ems.model.OrderAccessBean;
import com.codeloon.ems.model.OrderItemListBean;
import com.codeloon.ems.repository.*;
import com.codeloon.ems.service.OrderRequestservice;
import com.codeloon.ems.util.DataVarList;
import com.codeloon.ems.util.ResponseBean;
import com.codeloon.ems.util.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderRequestserviceImpl implements OrderRequestservice {

    private final OrderRequestRepository orderRequestRepository;

    private final OrderRequestDetailRepository orderRequestDetailRepository;

    private final PackageRepository packageRepository;

    private final PackageItemRepository packageItemRepository;

    private final InventoryItemRepository inventoryItemRepository;
    private final SystemBeanDto systemBeanDto;

    private final StatusRepository statusRepository;

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
                orderAccessBean.setTotal_price(packageItems.get(0).getPackage_id().getPackagePrice());
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
                    orderItemListBean.setCategory(packItem.getItemCategory());
                    //orderItemListBean.setItemDes();

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

    @Override
    public ResponseBean createOrderRequest(OrderAccessBean orderAccessBean) {
        ResponseBean responseBean = new ResponseBean();
        String msg = "";
        String code = ResponseCode.RSP_ERROR;
        OrderRequest orderRequest = new OrderRequest();
        List<OrderRequestDetail> orderRequestDetail = new ArrayList<>();
        BigDecimal totalCost = null;

        try {

            Optional<Status> orderStatus = statusRepository.findById(DataVarList.ORD_PENDING);
            Optional<Status> payStatus = statusRepository.findById(DataVarList.UNPAID);
            Optional<Package> packageData = packageRepository.findById(orderAccessBean.getPackageId());

            //TODO calculate total cost after customizing
            //totalCost = ;  qty * sellingPrice in inventory + others directly get price

            orderRequest = OrderRequest.builder()
                    .orderId(this.generateOrderNumber())
                    .packageId(packageData.get())
                    .customerNote(orderAccessBean.getCusNote())
                    .total(totalCost)
                    .eventDate(orderAccessBean.getEventDate())
                    .requestedDate(LocalDateTime.now())
                    .customerUsername(systemBeanDto.getSysUser())
                    .lastUpdatedDatetime(LocalDateTime.now())
                    .orderStatus(orderStatus.get())
                    .paymentStatus(payStatus.get())
                    .build();

            orderRequestRepository.saveAndFlush(orderRequest);


            orderRequestDetailRepository.saveAll(orderRequestDetail);

            code = ResponseCode.RSP_SUCCESS;
            msg = "Order req created successfully.";
            log.info("Order req created  successfully. ");

        }catch (Exception ex) {
            log.error("Error occurred while creating Order", ex);
            msg = "Error occurred while creating Order.";
        } finally {
            responseBean.setResponseMsg(msg);
            responseBean.setResponseCode(code);
            responseBean.setContent(null);
        }
        return responseBean;
    }

    public String generateOrderNumber() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMdd");
        String datePart = today.format(formatter);

        // Fetch count from DB
        int count = orderRequestRepository.getOrderCount() + 1;

        // Generate Order Number
        String orderNumber = String.format("ORD_%s_%05d", datePart, count);

        return orderNumber;
    }



}
