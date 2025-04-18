package com.codeloon.ems.service.impl;

import com.codeloon.ems.dto.OrderRequestDto;
import com.codeloon.ems.dto.SystemBeanDto;
import com.codeloon.ems.entity.Package;
import com.codeloon.ems.entity.*;
import com.codeloon.ems.model.*;
import com.codeloon.ems.repository.*;
import com.codeloon.ems.service.EmailSenderService;
import com.codeloon.ems.service.OrderRequestservice;
import com.codeloon.ems.util.DataVarList;
import com.codeloon.ems.util.ResponseBean;
import com.codeloon.ems.util.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    private final UserRepository userRepository;

    private final EmailSenderService emailSenderService;


    @Value("${ems.support.email}")
    private String support_email;
    @Value("${ems.support.contact}")
    private String support_contact;
    @Value("${ems.companyName}")
    private String companyName;

    @Override
    public ResponseBean accessAndLoad(String packid) {
        ResponseBean responseBean = new ResponseBean();
        List<PackageItem> packageItems = new ArrayList<>();
        OrderAccessBean orderAccessBean = new OrderAccessBean();
        List<OrderItemListBean> orderItemListBeanList = new ArrayList<>();

        String msg = "";
        String code = ResponseCode.RSP_ERROR;
        try {

            packageItems = packageItemRepository.findByPackageId(packid);
            if (!packageItems.isEmpty()) {
                orderAccessBean.setPackageId(packageItems.get(0).getPackage_id().getId());
                orderAccessBean.setTotal_price(packageItems.get(0).getPackage_id().getPackagePrice());
                orderAccessBean.setPackageTypeCode(packageItems.get(0).getPackage_id().getPackage_type().getCode());
                orderAccessBean.setPackageTypeDes(packageItems.get(0).getPackage_id().getPackage_type().getDescription());
                orderAccessBean.setEventType(packageItems.get(0).getPackage_id().getEvent().getEventType());

                packageItems.forEach(packItem -> {
                    OrderItemListBean orderItemListBean = new OrderItemListBean();
                    Optional<InventoryItem> inventoryItem = inventoryItemRepository.findById(Long.valueOf(packItem.getItemCode()));
                    InventoryItem inventoryItem1 = inventoryItem.get();

                    orderItemListBean.setPackageItemId(packItem.getId());
                    orderItemListBean.setPackageId(packItem.getPackage_id().getId());
                    orderItemListBean.setInventoryItemId(Long.valueOf(packItem.getItemCode()));
                    orderItemListBean.setItemName(inventoryItem.get().getItemName());
                    orderItemListBean.setQuantity(packItem.getQuantity());
                    orderItemListBean.setSellPrice(packItem.getSellPrice());
                    orderItemListBean.setBulkPrice(packItem.getBulkPrice());
                    orderItemListBean.setCategory(inventoryItem1.getCategory());
                    orderItemListBean.setItemDes(inventoryItem.get().getDescription() != null ? inventoryItem.get().getDescription() : "--");

                    orderItemListBeanList.add(orderItemListBean);
                });
                orderAccessBean.setOrderItemListBeanList(orderItemListBeanList);

                code = ResponseCode.RSP_SUCCESS;
                msg = "Success";

            } else {
                code = ResponseCode.RSP_ERROR;
                msg = "Invalid Package id";
            }

        } catch (Exception e) {
            log.error(String.valueOf(e));
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
        int isRef = 0;

        try {

            Optional<Status> orderStatus = statusRepository.findById(DataVarList.ORD_PENDING);
            Optional<Status> payStatus = statusRepository.findById(DataVarList.UNPAID);
            Optional<Package> packageData = packageRepository.findById(orderAccessBean.getPackageId());
            orderId = this.generateOrderNumber();

            Optional<User> user = userRepository.findByUsername(systemBeanDto.getSysUser());

            orderRequest.setOrderId(orderId);
            orderRequest.setPackageId(packageData.get());
            orderRequest.setCustomerNote(orderAccessBean.getCusNote());
            orderRequest.setTotal(totalCost);
            orderRequest.setEventDate(orderAccessBean.getEventDate());
            orderRequest.setRequestedDate(LocalDateTime.now());
            orderRequest.setCustomerUsername(user.get());
            orderRequest.setLastUpdatedDatetime(LocalDateTime.now());
            orderRequest.setOrderStatus(orderStatus.get());
            orderRequest.setPaymentStatus(payStatus.get());
            orderRequest.setDeliveryFee(orderAccessBean.getDeliveryFee());
            orderRequest.setDistrictName(orderAccessBean.getDistrictName());
            orderRequest.setAddress(orderAccessBean.getAddress());
            for (OrderItemListBean orderData : orderAccessBean.getOrderItemListBeanList()) {
                OrderRequestDetail orderRequestDetail1 = new OrderRequestDetail();
                Optional<InventoryItem> inventoryItem = inventoryItemRepository.findById(orderData.getInventoryItemId());

                orderRequestDetail1.setOrderId(orderRequest);
                orderRequestDetail1.setItemId(inventoryItem.get());
                orderRequestDetail1.setItemName(inventoryItem.get().getItemName());
                orderRequestDetail1.setUnitPrice(BigDecimal.valueOf(orderData.getSellPrice()));
                orderRequestDetail1.setQuantity(orderData.getQuantity());
                orderRequestDetail1.setBulkPrice(BigDecimal.valueOf(orderData.getBulkPrice()));
                orderRequestDetail1.setCreatedDatetime(LocalDateTime.now());
                orderRequestDetail1.setCustomerId(systemBeanDto.getSysUser());

                orderRequestDetail.add(orderRequestDetail1);

                boolean isrefGet = inventoryItem.get().getIsRefundable() != null ? inventoryItem.get().getIsRefundable() : false;
                if (isrefGet) {
                    isRef = 1;
                }
            }
            ;

            orderRequest.setTotal(BigDecimal.valueOf(orderAccessBean.getTotal_price()));
            if (isRef == 1) {
                orderRequest.setRefStatus(DataVarList.REFUNDABLE_PENDING);
            }

            orderRequestRepository.saveAndFlush(orderRequest);
            orderRequestDetailRepository.saveAll(orderRequestDetail);

            code = ResponseCode.RSP_SUCCESS;
            msg = "Order req created successfully.";
            log.info("Order req created  successfully. ");

        } catch (Exception ex) {
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

            if (!inventoryList.isEmpty()) {
                List<Object> orderDataList = this.mapSearchData(inventoryList);
                dataTableBean.setList(orderDataList);
                dataTableBean.setCount(inventoryList.getTotalElements());
                dataTableBean.setPagecount(inventoryList.getTotalPages());

                msg = "List review successfully.";
                code = ResponseCode.RSP_SUCCESS;
            } else {
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

            if (!inventoryList.isEmpty()) {
                List<Object> orderDataList = this.mapSearchData(inventoryList);
                dataTableBean.setList(orderDataList);
                dataTableBean.setCount(inventoryList.getTotalElements());
                dataTableBean.setPagecount(inventoryList.getTotalPages());

                msg = "List review successfully.";
                code = ResponseCode.RSP_SUCCESS;
            } else {
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
        List<OrderRequestDetail> orderRequestDetails = new ArrayList<>();
        OrderDetailsBean orderDetailsBean = new OrderDetailsBean();
        List<OrderDetailListBean> orderDetailListBeanList = new ArrayList<>();

        String msg = "";
        String code = ResponseCode.RSP_ERROR;
        try {

            Optional<OrderRequest> orderRequest = orderRequestRepository.findById(orderid);
            if (orderRequest.isPresent()) {
                orderRequestDetails = orderRequestDetailRepository.findByorderId(orderRequest.get());

                orderDetailsBean.setOrderId(orderRequest.get().getOrderId());
                orderDetailsBean.setPackageId(orderRequest.get().getPackageId().getId());
                orderDetailsBean.setCusNote(orderRequest.get().getCustomerNote());
                orderDetailsBean.setTotal_amount(orderRequest.get().getTotal());
                orderDetailsBean.setEventDate(orderRequest.get().getEventDate());
                orderDetailsBean.setRequestedDate(orderRequest.get().getRequestedDate());
                orderDetailsBean.setCusId(orderRequest.get().getCustomerUsername().getUsername());
                orderDetailsBean.setOrderStatus(orderRequest.get().getOrderStatus().getCode());
                orderDetailsBean.setOrderStatusDes(orderRequest.get().getOrderStatus().getDescription());
                orderDetailsBean.setAdminRemark(orderRequest.get().getRemark());
                orderDetailsBean.setApprovedUser(orderRequest.get().getApprovedUser());
                orderDetailsBean.setPaymentStatus(orderRequest.get().getPaymentStatus().getCode());
                orderDetailsBean.setPaymentStatusDes(orderRequest.get().getPaymentStatus().getDescription());
                orderDetailsBean.setRefundableStatus(orderRequest.get().getRefStatus());

                if (!orderRequestDetails.isEmpty()) {

                    orderRequestDetails.forEach(packItem -> {
                        OrderDetailListBean orderItemListBean = new OrderDetailListBean();
                        orderItemListBean.setOrderDetailId(packItem.getId());
                        orderItemListBean.setOrderId(packItem.getOrderId().getOrderId());
                        orderItemListBean.setInventoryItemId(packItem.getItemId().getId());
                        orderItemListBean.setItemName(packItem.getItemName());
                        orderItemListBean.setQuantity(packItem.getQuantity());
                        orderItemListBean.setUnitPrice(packItem.getUnitPrice());
                        orderItemListBean.setBulkPrice(packItem.getBulkPrice());
                        orderItemListBean.setItemCategory(packItem.getItemId().getCategory());

                        //TODO balance calculation with date
                        orderItemListBean.setItemBalance(packItem.getItemId().getQuantity());

                        orderDetailListBeanList.add(orderItemListBean);

                    });
                    orderDetailsBean.setOrderDetailListBeanList(orderDetailListBeanList);

                    code = ResponseCode.RSP_SUCCESS;
                    msg = "Success";

                } else {
                    code = ResponseCode.RSP_ERROR;
                    msg = "Invalid Order id";
                }
            } else {
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
            orderRequestDto.setCustomerUsername(data.getCustomerUsername().getUsername());
            orderRequestDto.setLastUpdatedDatetime(data.getLastUpdatedDatetime());
            orderRequestDto.setOrderStatus(data.getOrderStatus().getCode());
            orderRequestDto.setPaymentStatus(data.getPaymentStatus().getCode());
            orderRequestDto.setOrderStatusDes(data.getOrderStatus().getDescription());
            orderRequestDto.setPaymentStatusDes(data.getPaymentStatus().getDescription());
            orderRequestDto.setRemark(data.getRemark());
            orderRequestDto.setApprovedUser(data.getApprovedUser());
            orderRequestDto.setRefundableStatus(data.getRefStatus() != null ? data.getRefStatus() : "--");

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

            if (!inventoryList.isEmpty()) {
                List<Object> orderDataList = this.mapSearchData(inventoryList);
                dataTableBean.setList(orderDataList);
                dataTableBean.setCount(inventoryList.getTotalElements());
                dataTableBean.setPagecount(inventoryList.getTotalPages());

                msg = "List searched successfully.";
                code = ResponseCode.RSP_SUCCESS;
            } else {
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

            if (orderRequest.isPresent()) {
                OrderRequest orderRequest1 = orderRequest.get();
                orderRequest1.setRemark(orderRequestDto.getRemark());

                if (orderRequestDto.getOrderStatus().equals("A")) {
                    statusOptional = statusRepository.findById(DataVarList.ORD_APPROVED);
                    orderRequest1.setOrderStatus(statusOptional.get());

                } else if (orderRequestDto.getOrderStatus().equals("R")) {
                    statusOptional = statusRepository.findById(DataVarList.ORD_REJECTED);
                    orderRequest1.setOrderStatus(statusOptional.get());
                }

                orderRequest1.setLastUpdatedDatetime(LocalDateTime.now());
                orderRequest1.setApprovedUser(systemBeanDto.getSysUser());

                orderRequestRepository.saveAndFlush(orderRequest1);

                code = ResponseCode.RSP_SUCCESS;
                msg = "Order req updated successfully.";
                log.info("Order req updated successfully. ");

            } else {
                code = ResponseCode.RSP_ERROR;
                msg = "Invalid Order id";
                log.info("Invalid Order id. ");
            }

        } catch (Exception ex) {
            log.error("Error occurred while updating order status", ex);
            msg = "Error occurred while updating order status.";

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
        List<PackageItem> packageItems = new ArrayList<>();

        try {
            Optional<Status> orderStatus = statusRepository.findById(DataVarList.ORD_PENDING);
            Optional<Status> payStatus = statusRepository.findById(DataVarList.UNPAID);
            Optional<Package> packageData = packageRepository.findById(orderRequestDto.getPackageId());
            Optional<OrderRequest> orderRequest1 = orderRequestRepository.findById(orderRequestDto.getOrderId());
            Optional<Status> statusOptional = statusRepository.findById(DataVarList.ORD_APPROVED);
            packageItems = packageItemRepository.findByPackageId(orderRequestDto.getPackageId());

            orderRequestDetail = orderRequestDetailRepository.findByorderId(orderRequest1.get());
            orderRequestDetailRepository.deleteAll(orderRequestDetail);

            if (orderRequest1.isPresent()) {
                orderRequest = orderRequest1.get();

                orderRequest.setPackageId(packageData.get());
                orderRequest.setLastUpdatedDatetime(LocalDateTime.now());
                orderRequest.setTotal(BigDecimal.valueOf(packageData.get().getPackagePrice()));
                orderRequest.setApprovedUser(systemBeanDto.getSysUser());
                orderRequest.setOrderStatus(statusOptional.get());
            }

            for (PackageItem packageItem : packageItems) {
                OrderRequestDetail orderRequestDetail1 = new OrderRequestDetail();
                Optional<InventoryItem> inventoryItem = inventoryItemRepository.findById(Long.valueOf(packageItem.getItemCode()));

                orderRequestDetail1.setOrderId(orderRequest);
                orderRequestDetail1.setItemId(inventoryItem.get());
                orderRequestDetail1.setItemName(inventoryItem.get().getItemName());
                orderRequestDetail1.setUnitPrice(BigDecimal.valueOf(packageItem.getSellPrice()));
                orderRequestDetail1.setQuantity(packageItem.getQuantity());
                orderRequestDetail1.setBulkPrice(BigDecimal.valueOf(packageItem.getBulkPrice()));
                orderRequestDetail1.setCreatedDatetime(LocalDateTime.now());
                orderRequestDetail1.setCustomerId(systemBeanDto.getSysUser());

                orderRequestDetail.add(orderRequestDetail1);
            }
            ;

            orderRequestRepository.saveAndFlush(orderRequest);
            orderRequestDetailRepository.saveAll(orderRequestDetail);

            code = ResponseCode.RSP_SUCCESS;
            msg = "Order req created successfully.";
            log.info("Order req created  successfully. ");

        } catch (Exception ex) {
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

            if (orderRequest.isPresent()) {
                OrderRequest orderRequest1 = orderRequest.get();

                if (orderRequestDto.getOrderStatus().equals("A")) {
                    statusOptional = statusRepository.findById(DataVarList.ORD_APPROVED);
                    orderRequest1.setOrderStatus(statusOptional.get());

                } else if (orderRequestDto.getOrderStatus().equals("R")) {
                    statusOptional = statusRepository.findById(DataVarList.ORD_REJECTED);
                    orderRequest1.setOrderStatus(statusOptional.get());
                }
                orderRequestRepository.saveAndFlush(orderRequest1);

                code = ResponseCode.RSP_SUCCESS;
                msg = "Order req updated successfully.";
                log.info("Order req updated successfully. ");
            } else {
                code = ResponseCode.RSP_ERROR;
                msg = "Invalid Order id";
                log.info("Invalid Order id. ");
            }

        } catch (Exception ex) {
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
    public ResponseBean paymentStatusUpdate(OrderRequestDto orderRequestDto) {
        ResponseBean responseBean = new ResponseBean();
        String msg = "";
        String code = ResponseCode.RSP_ERROR;

        try {
            Optional<OrderRequest> orderRequest = orderRequestRepository.findById(orderRequestDto.getOrderId());
            Optional<Status> statusOptional;

            if (orderRequest.isPresent()) {
                boolean requestStatus = false;
                OrderRequest orderRequest1 = orderRequest.get();

                if (orderRequestDto.getOrderStatus().equals("A")) {
                    statusOptional = statusRepository.findById(DataVarList.PAYEMNT_APPROVED);
                    orderRequest1.setPaymentStatus(statusOptional.get());
                    requestStatus = true;
                } else if (orderRequestDto.getOrderStatus().equals("R")) {
                    statusOptional = statusRepository.findById(DataVarList.PAYEMNT_REJECTED);
                    orderRequest1.setPaymentStatus(statusOptional.get());
                }

                orderRequest1.setLastUpdatedDatetime(LocalDateTime.now());
                orderRequest1.setApprovedUser(systemBeanDto.getSysUser());

                orderRequestRepository.saveAndFlush(orderRequest1);

                /* -- Order confirmation email -- */
                Package confirmedPackage = packageRepository.findById(orderRequest1.getPackageId().getId()).orElseThrow(() -> new RuntimeException("Package not found"));
                User user = userRepository.findByUsername(orderRequest1.getCustomerUsername().getUsername()).orElseThrow(() -> new RuntimeException("Customer not found"));
                EmailRequestBean emailBean = null;

                if (requestStatus) {
                    emailBean = this.formOrderConfirmationEmail(user, orderRequest1, confirmedPackage);
                } else {
                    emailBean = this.formOrderRejectionEmail(user, orderRequest1, confirmedPackage);
                }

                emailSenderService.sendPlainTextEmail(emailBean);
                /* -- Order confirmation email ends -- */

                code = ResponseCode.RSP_SUCCESS;
                msg = "Payment status updated successfully.";
                log.info("Payment status updated successfully. ");

            } else {
                code = ResponseCode.RSP_ERROR;
                msg = "Invalid Order id";
                log.info("Invalid Order id. ");
            }

        } catch (Exception ex) {
            log.error("Error occurred while updating payment status", ex);
            msg = "Error occurred while updating payment status.";

        } finally {
            responseBean.setResponseMsg(msg);
            responseBean.setResponseCode(code);
            responseBean.setContent(null);
        }
        return responseBean;
    }


    @Override
    public DataTableBean refundableOrderList(int page, int size) {
        DataTableBean dataTableBean = new DataTableBean();
        String msg = "";
        String code = ResponseCode.RSP_ERROR;
        Page<OrderRequest> inventoryList = null;

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("requestedDate").ascending());

            //search only from item name
            inventoryList = orderRequestRepository.findAllByRefStatus(DataVarList.REFUNDABLE_PENDING, pageable);

            if (!inventoryList.isEmpty()) {
                List<Object> orderDataList = this.mapSearchData(inventoryList);
                dataTableBean.setList(orderDataList);
                dataTableBean.setCount(inventoryList.getTotalElements());
                dataTableBean.setPagecount(inventoryList.getTotalPages());

                msg = "List review successfully.";
                code = ResponseCode.RSP_SUCCESS;
            } else {
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
    public ResponseBean refundStatusUpdate(OrderRequestDto orderRequestDto) {
        ResponseBean responseBean = new ResponseBean();
        String msg = "";
        String code = ResponseCode.RSP_ERROR;

        try {
            Optional<OrderRequest> orderRequest = orderRequestRepository.findById(orderRequestDto.getOrderId());
            Optional<Status> statusOptional;

            if (orderRequest.isPresent()) {
                OrderRequest orderRequest1 = orderRequest.get();
                orderRequest1.setRefStatus(DataVarList.REFUNDABLE_RECEIVED);

                orderRequest1.setLastUpdatedDatetime(LocalDateTime.now());
                orderRequest1.setApprovedUser(systemBeanDto.getSysUser());

                orderRequestRepository.saveAndFlush(orderRequest1);

                code = ResponseCode.RSP_SUCCESS;
                msg = "Refundable status updated successfully.";
                log.info("Refundable status updated successfully. ");

            } else {
                code = ResponseCode.RSP_ERROR;
                msg = "Invalid Order id";
                log.info("Invalid Order id. ");
            }

        } catch (Exception ex) {
            log.error("Error occurred while updating Refundable status", ex);
            msg = "Error occurred while updating Refundable status.";

        } finally {
            responseBean.setResponseMsg(msg);
            responseBean.setResponseCode(code);
            responseBean.setContent(null);
        }
        return responseBean;
    }


    private EmailRequestBean formOrderConfirmationEmail(User userDto, OrderRequest orderRequest1, Package confirmedPackage) {
        StringBuilder emailBody = new StringBuilder();

        emailBody.append("Dear ").append(userDto.getUsername()).append(",\n\n")
                .append("Thank you for your order! We're excited to let you know that your order has been confirmed and is being processed.\n\n")
                .append("📦 **Order Summary**\n")
                .append("🔹 Order ID: ").append(orderRequest1.getOrderId()).append("\n")
                .append("🔹 Package ID: ").append(confirmedPackage.getId()).append("\n")
                .append("🔹 Package Description: ").append(confirmedPackage.getDescription()).append("\n")
                .append("🔹 Event Date: ").append(orderRequest1.getEventDate()).append("\n")
                .append("🔹 Delivery Fee: ").append(orderRequest1.getDeliveryFee()).append("\n")
                .append("💰 Order Fee: ").append(orderRequest1.getTotal()).append("\n\n")
                .append("🏠 **Delivery Address**\n")
                .append(orderRequest1.getAddress()).append("\n\n")
                .append("We'll notify you again when your order ships. If you have any questions, please contact our support team at ")
                .append(support_email).append(".\n\n")
                .append("Thank you for choosing us!\n\n")
                .append("Best regards,\n")
                .append(companyName).append(" Team\n")
                .append(support_contact);

        String subject = "Your Order is Confirmed! 🎉 [Order ID: #" + orderRequest1.getOrderId() + "]";

        return EmailRequestBean.builder()
                .to(userDto.getEmail())
                .subject(subject)
                .text(emailBody.toString())
                .build();
    }

    private EmailRequestBean formOrderRejectionEmail(User userDto, OrderRequest orderRequest1, Package rejectedPackage) {
        StringBuilder emailBody = new StringBuilder();

        emailBody.append("Dear ").append(userDto.getUsername()).append(",\n\n")
                .append("We regret to inform you that your recent order could not be processed.\n\n")
                .append("📦 **Order Details**\n")
                .append("🔹 Order ID: ").append(orderRequest1.getOrderId()).append("\n")
                .append("🔹 Package ID: ").append(rejectedPackage.getId()).append("\n")
                .append("🔹 Package Description: ").append(rejectedPackage.getDescription()).append("\n")
                .append("🔹 Event Date: ").append(orderRequest1.getEventDate()).append("\n")
                .append("💰 Order Fee : ").append(orderRequest1.getTotal()).append("\n\n")
                .append("🏠 **Delivery Address**\n")
                .append(orderRequest1.getAddress()).append("\n\n")
                .append("**Reason for rejection**: Payment verification failed\n\n") // Customize this reason as needed
                .append("Please contact our support team at ").append(support_email)
                .append(" to resolve this issue or place a new order.\n\n")
                .append("We apologize for any inconvenience caused.\n\n")
                .append("Best regards,\n")
                .append(companyName).append(" Team\n")
                .append(support_contact);

        String subject = "Order Declined ❌ [Order ID: #" + orderRequest1.getOrderId() + "]";

        return EmailRequestBean.builder()
                .to(userDto.getEmail())
                .subject(subject)
                .text(emailBody.toString())
                .build();
    }
}
