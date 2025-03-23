package com.codeloon.ems.service.impl;

import com.codeloon.ems.dto.InventoryItemDto;
import com.codeloon.ems.dto.OrderRequestDto;
import com.codeloon.ems.dto.SystemBeanDto;
import com.codeloon.ems.entity.*;
import com.codeloon.ems.entity.Package;
import com.codeloon.ems.model.*;
import com.codeloon.ems.repository.*;
import com.codeloon.ems.service.OrderRequestservice;
import com.codeloon.ems.util.DataVarList;
import com.codeloon.ems.util.ResponseBean;
import com.codeloon.ems.util.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
        BigDecimal totalCost = BigDecimal.valueOf(0);
        String orderId = null;

        try {

            Optional<Status> orderStatus = statusRepository.findById(DataVarList.ORD_PENDING);
            Optional<Status> payStatus = statusRepository.findById(DataVarList.UNPAID);
            Optional<Package> packageData = packageRepository.findById(orderAccessBean.getPackageId());
            orderId = this.generateOrderNumber();

            orderRequest = OrderRequest.builder()
                    .orderId(orderId)
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

            orderRequest.setOrderId(orderId);
            orderRequest.setPackageId(packageData.get());
            orderRequest.setCustomerNote(orderAccessBean.getCusNote());
            orderRequest.setTotal(totalCost);
            orderRequest.setEventDate(orderAccessBean.getEventDate());
            orderRequest.setRequestedDate(LocalDateTime.now());
            orderRequest.setCustomerUsername(systemBeanDto.getSysUser());
            orderRequest.setLastUpdatedDatetime(LocalDateTime.now());
            orderRequest.setOrderStatus(orderStatus.get());
            orderRequest.setPaymentStatus(payStatus.get());

            for (OrderItemListBean orderData : orderAccessBean.getOrderItemListBeanList()) {
                OrderRequestDetail orderRequestDetail1 = new OrderRequestDetail();
                Optional<InventoryItem> inventoryItem = inventoryItemRepository.findById(orderData.getInventoryItemId());
                BigDecimal bulkPrice = orderRequestDetail1.getUnitPrice().multiply(BigDecimal.valueOf(orderRequestDetail1.getQuantity()));

                totalCost = totalCost.add(bulkPrice);

                orderRequestDetail1.setOrderId(orderRequest);
                orderRequestDetail1.setItemId(inventoryItem.get());
                orderRequestDetail1.setItemName(inventoryItem.get().getItemName());
                orderRequestDetail1.setUnitPrice(BigDecimal.valueOf(orderData.getSellPrice()));
                orderRequestDetail1.setQuantity(orderData.getQuantity());
                orderRequestDetail1.setBulkPrice(bulkPrice);
                orderRequestDetail1.setCreatedDatetime(LocalDateTime.now());
                orderRequestDetail1.setCustomerId(systemBeanDto.getSysUser());

                orderRequestDetail.add(orderRequestDetail1);
            };

            orderRequest.setTotal(totalCost);

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

    @Override
    public DataTableBean customerList(int page, int size) {
        DataTableBean dataTableBean = new DataTableBean();
        String msg = "";
        String code = ResponseCode.RSP_ERROR;
        Page<OrderRequest> inventoryList = null;

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("requestedDate").ascending());

            //search only from item name
            inventoryList = orderRequestRepository.findByCustomerUsername(systemBeanDto.getSysUser(), pageable);

            if(!inventoryList.isEmpty()){
                List<Object> orderDataList = this.mapSearchData(inventoryList);
                dataTableBean.setList(orderDataList);
                dataTableBean.setCount(inventoryList.getTotalElements());
                dataTableBean.setPagecount(inventoryList.getTotalPages());

                msg = "List review successfully.";
                code = ResponseCode.RSP_SUCCESS;
            }else {
                dataTableBean.setCount(0);
                dataTableBean.setPagecount(0);

                log.warn("List review not found");
                msg = "Order List not found: ";
            }
        } catch (Exception ex) {
            log.error("Error occurred while reviewing order list : {}", ex.getMessage(), ex);
            msg = "Error occurred while eviewing order list.";
        }
        dataTableBean.setMsg(msg);
        dataTableBean.setCode(code);
        return dataTableBean;
    }

    @Override
    public DataTableBean orderReqList(int page, int size) {
        DataTableBean dataTableBean = new DataTableBean();
        String msg = "";
        String code = ResponseCode.RSP_ERROR;
        Page<OrderRequest> inventoryList = null;

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("requestedDate").ascending());

            //search only from item name
            inventoryList = orderRequestRepository.findAll(pageable);

            if(!inventoryList.isEmpty()){
                List<Object> orderDataList = this.mapSearchData(inventoryList);
                dataTableBean.setList(orderDataList);
                dataTableBean.setCount(inventoryList.getTotalElements());
                dataTableBean.setPagecount(inventoryList.getTotalPages());

                msg = "List review successfully.";
                code = ResponseCode.RSP_SUCCESS;
            }else {
                dataTableBean.setCount(0);
                dataTableBean.setPagecount(0);

                log.warn("List review not found");
                msg = "Order List not found: ";
            }
        } catch (Exception ex) {
            log.error("Error occurred while reviewing order list : {}", ex.getMessage(), ex);
            msg = "Error occurred while eviewing order list.";
        }
        dataTableBean.setMsg(msg);
        dataTableBean.setCode(code);
        return dataTableBean;
    }

    @Override
    public ResponseBean viewOrderDetails(String orderid) {
        ResponseBean responseBean = new ResponseBean();
        List<OrderRequestDetail>  orderRequestDetails = new ArrayList<>();
        OrderDetailsBean orderDetailsBean = new OrderDetailsBean();
        List<OrderDetailListBean> orderDetailListBeanList = new ArrayList<>();

        String msg = "";
        String code = ResponseCode.RSP_ERROR;
        try {

            Optional<OrderRequest> orderRequest = orderRequestRepository.findById(orderid);
            if(orderRequest.isPresent()){
                orderRequestDetails = orderRequestDetailRepository.findByorderId(orderRequest.get());

                orderDetailsBean.setOrderId(orderRequest.get().getOrderId());
                orderDetailsBean.setPackageId(orderRequest.get().getPackageId().getId());
                orderDetailsBean.setCusNote(orderRequest.get().getCustomerNote());
                orderDetailsBean.setTotal_amount(orderRequest.get().getTotal());
                orderDetailsBean.setEventDate(orderRequest.get().getEventDate());
                orderDetailsBean.setRequestedDate(orderRequest.get().getRequestedDate());
                orderDetailsBean.setCusId(orderRequest.get().getCustomerUsername());
                orderDetailsBean.setOrderStatus(orderRequest.get().getOrderStatus().getCode());
                orderDetailsBean.setOrderStatusDes(orderRequest.get().getOrderStatus().getDescription());
                orderDetailsBean.setAdminRemark(orderRequest.get().getRemark());
                orderDetailsBean.setApprovedUser(orderRequest.get().getApprovedUser());

                if(!orderRequestDetails.isEmpty()) {

                    orderRequestDetails.forEach(packItem -> {
                        OrderDetailListBean orderItemListBean = new OrderDetailListBean();
                        orderItemListBean.setOrderDetailId(packItem.getId());
                        orderItemListBean.setOrderId(packItem.getOrderId().getOrderId());
                        orderItemListBean.setInventoryItemId(packItem.getItemId().getId());
                        orderItemListBean.setItemName(packItem.getItemName());
                        orderItemListBean.setQuantity(packItem.getQuantity());
                        orderItemListBean.setUnitPrice(packItem.getUnitPrice());
                        orderItemListBean.setBulkPrice(packItem.getBulkPrice());

                        orderDetailListBeanList.add(orderItemListBean);

                    });
                    orderDetailsBean.setOrderDetailListBeanList(orderDetailListBeanList);

                    code = ResponseCode.RSP_SUCCESS;
                    msg = "Success";

                }else {
                    code = ResponseCode.RSP_ERROR;
                    msg = "Invalid Order id";
                }
            }else {
                code = ResponseCode.RSP_ERROR;
                msg = "Invalid Order id";
            }

        } catch (Exception e) {
            throw new RuntimeException(e);

        } finally {
            responseBean.setResponseMsg(msg);
            responseBean.setResponseCode(code);
            responseBean.setContent(orderDetailsBean);
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


    private List<Object> mapSearchData(Page<OrderRequest> dataList) {
        List<Object> dataBeanList = new ArrayList<>();
        dataList.forEach(data -> {
            OrderRequestDto orderRequestDto = new OrderRequestDto();

            orderRequestDto.setOrderId(data.getOrderId());
            orderRequestDto.setPackageId(data.getPackageId().getId());
            orderRequestDto.setCustomerNote(data.getCustomerNote());
            orderRequestDto.setTotal(data.getTotal());
            orderRequestDto.setEventDate(data.getEventDate());
            orderRequestDto.setRequestedDate(data.getRequestedDate());
            orderRequestDto.setCustomerUsername(data.getCustomerUsername());
            orderRequestDto.setLastUpdatedDatetime(data.getLastUpdatedDatetime());
            orderRequestDto.setOrderStatus(data.getOrderStatus());
            orderRequestDto.setPaymentStatus(data.getPaymentStatus());
            orderRequestDto.setRemark(data.getRemark());
            orderRequestDto.setApprovedUser(data.getApprovedUser());

            dataBeanList.add(orderRequestDto);
        });
        return dataBeanList;
    }

    @Override
    public DataTableBean orderReqListStatus(String status, int page, int size) {
        DataTableBean dataTableBean = new DataTableBean();
        String msg = "";
        String code = ResponseCode.RSP_ERROR;
        Page<OrderRequest> inventoryList = null;

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("requestedDate").ascending());

            //search only from item name
            inventoryList = orderRequestRepository.findAllByOrderStatus(status, pageable);

            if(!inventoryList.isEmpty()){
                List<Object> orderDataList = this.mapSearchData(inventoryList);
                dataTableBean.setList(orderDataList);
                dataTableBean.setCount(inventoryList.getTotalElements());
                dataTableBean.setPagecount(inventoryList.getTotalPages());

                msg = "List searched successfully.";
                code = ResponseCode.RSP_SUCCESS;
            }else {
                dataTableBean.setCount(0);
                dataTableBean.setPagecount(0);

                log.warn("List searching not found");
                msg = "Order List not found: ";
            }
        } catch (Exception ex) {
            log.error("Error occurred while searching order list : {}", ex.getMessage(), ex);
            msg = "Error occurred while searching order list.";
        }
        dataTableBean.setMsg(msg);
        dataTableBean.setCode(code);
        return dataTableBean;
    }

    @Override
    public ResponseBean adminStatusUpdate(OrderRequestDto orderRequestDto) {
        ResponseBean responseBean = new ResponseBean();
        String msg = "";
        String code = ResponseCode.RSP_ERROR;

        try {
            Optional<OrderRequest> orderRequest = orderRequestRepository.findById(orderRequestDto.getOrderId());
            Optional<Status> statusOptional;

            if(orderRequest.isPresent()){
                OrderRequest orderRequest1 = orderRequest.get();
                orderRequest1.setRemark(orderRequestDto.getRemark());

                if(orderRequestDto.getOrderStatus().equals("A")){
                    statusOptional = statusRepository.findById(DataVarList.ORD_APPROVED);
                    orderRequest1.setOrderStatus(statusOptional.get());

                }else if (orderRequestDto.getOrderStatus().equals("R")){
                    statusOptional = statusRepository.findById(DataVarList.ORD_REJECTED);
                    orderRequest1.setOrderStatus(statusOptional.get());
                }

                orderRequestRepository.saveAndFlush(orderRequest1);

                code = ResponseCode.RSP_SUCCESS;
                msg = "Order req updated successfully.";
                log.info("Order req updated successfully. ");

            }else {
                code = ResponseCode.RSP_ERROR;
                msg = "Invalid Order id";
                log.info("Invalid Order id. ");
            }

        }catch (Exception ex) {
            log.error("Error occurred while updating order status", ex);
            msg = "Error occurred while creating updating order status.";

        } finally {
            responseBean.setResponseMsg(msg);
            responseBean.setResponseCode(code);
            responseBean.setContent(null);
        }
        return responseBean;
    }

    @Override
    public ResponseBean updateOrder(OrderRequestDto orderRequestDto) {
        ResponseBean responseBean = new ResponseBean();
        String msg = "";
        String code = ResponseCode.RSP_ERROR;
        OrderRequest orderRequest = new OrderRequest();
        List<OrderRequestDetail> orderRequestDetail = new ArrayList<>();
        BigDecimal totalCost = BigDecimal.valueOf(0);

        try {
            Optional<Status> orderStatus = statusRepository.findById(DataVarList.ORD_PENDING);
            Optional<Status> payStatus = statusRepository.findById(DataVarList.UNPAID);
            Optional<Package> packageData = packageRepository.findById(orderRequestDto.getPackageId());
            Optional<OrderRequest> orderRequest1 = orderRequestRepository.findById(orderRequestDto.getOrderId());

            if(orderRequest1.isPresent()){
                orderRequest = orderRequest1.get();

                orderRequest.setPackageId(packageData.get());
                orderRequest.setLastUpdatedDatetime(LocalDateTime.now());
            }

//            for (OrderItemListBean orderData : orderAccessBean.getOrderItemListBeanList()) {
//                OrderRequestDetail orderRequestDetail1 = new OrderRequestDetail();
//                Optional<InventoryItem> inventoryItem = inventoryItemRepository.findById(orderData.getInventoryItemId());
//                BigDecimal bulkPrice = orderRequestDetail1.getUnitPrice().multiply(BigDecimal.valueOf(orderRequestDetail1.getQuantity()));
//
//                totalCost = totalCost.add(bulkPrice);
//
//                orderRequestDetail1.setOrderId(orderRequest);
//                orderRequestDetail1.setItemId(inventoryItem.get());
//                orderRequestDetail1.setItemName(inventoryItem.get().getItemName());
//                orderRequestDetail1.setUnitPrice(BigDecimal.valueOf(orderData.getSellPrice()));
//                orderRequestDetail1.setQuantity(orderData.getQuantity());
//                orderRequestDetail1.setBulkPrice(bulkPrice);
//                orderRequestDetail1.setCreatedDatetime(LocalDateTime.now());
//                orderRequestDetail1.setCustomerId(systemBeanDto.getSysUser());
//
//                orderRequestDetail.add(orderRequestDetail1);
//            };
//
//            orderRequest.setTotal(totalCost);
//
//            orderRequestRepository.saveAndFlush(orderRequest);
//            orderRequestDetailRepository.saveAll(orderRequestDetail);

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

    @Override
    public ResponseBean cusStatusUpdate(OrderRequestDto orderRequestDto) {
        ResponseBean responseBean = new ResponseBean();
        String msg = "";
        String code = ResponseCode.RSP_ERROR;

        try {
            Optional<OrderRequest> orderRequest = orderRequestRepository.findById(orderRequestDto.getOrderId());
            Optional<Status> statusOptional;

            if(orderRequest.isPresent()){
                OrderRequest orderRequest1 = orderRequest.get();

                if(orderRequestDto.getOrderStatus().equals("A")){
                    statusOptional = statusRepository.findById(DataVarList.ORD_APPROVED);
                    orderRequest1.setOrderStatus(statusOptional.get());

                }else if (orderRequestDto.getOrderStatus().equals("R")){
                    statusOptional = statusRepository.findById(DataVarList.ORD_REJECTED);
                    orderRequest1.setOrderStatus(statusOptional.get());
                }
                orderRequestRepository.saveAndFlush(orderRequest1);

                code = ResponseCode.RSP_SUCCESS;
                msg = "Order req updated successfully.";
                log.info("Order req updated successfully. ");
            }else {
                code = ResponseCode.RSP_ERROR;
                msg = "Invalid Order id";
                log.info("Invalid Order id. ");
            }

        }catch (Exception ex) {
            log.error("Error occurred while updating order status", ex);
            msg = "Error occurred while creating updating order status.";

        } finally {
            responseBean.setResponseMsg(msg);
            responseBean.setResponseCode(code);
            responseBean.setContent(null);
        }
        return responseBean;
    }
}
