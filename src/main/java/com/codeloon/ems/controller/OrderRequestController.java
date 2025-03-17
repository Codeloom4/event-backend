package com.codeloon.ems.controller;

import com.codeloon.ems.dto.InventoryItemDto;
import com.codeloon.ems.model.DataTableBean;
import com.codeloon.ems.model.OrderAccessBean;
import com.codeloon.ems.service.OrderRequestservice;
import com.codeloon.ems.util.ResponseBean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/ems/orderrequest")
@RequiredArgsConstructor
public class OrderRequestController {

    private final OrderRequestservice orderRequestservice;

    @GetMapping("/access/{packid}")
    public ResponseEntity<?> accessAndLoad(@PathVariable String packid) {
        ResponseEntity<?> responseEntity;
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ResponseBean responseBean = new ResponseBean();

        try {
            responseBean = orderRequestservice.accessAndLoad(packid);
            httpStatus = HttpStatus.OK;
        }catch (Exception ex){
            log.error("Error occurred while retrieving inventory Item.{} ", ex.getMessage());
        }finally {
            responseEntity = new ResponseEntity<>(responseBean, httpStatus);
        }
        return responseEntity;
    }

    @PostMapping("/request")
    public ResponseEntity<?> createOrderRequest(@RequestBody OrderAccessBean orderAccessBean) {
        ResponseEntity<?> responseEntity;
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ResponseBean responseBean = new ResponseBean();
        try {
            responseBean = orderRequestservice.createOrderRequest(orderAccessBean);
            httpStatus = HttpStatus.CREATED;
        } catch (Exception ex) {
            log.error("Error occurred while adding new inventory Item.{} ", ex.getMessage());
        } finally {
            responseEntity = new ResponseEntity<>(responseBean, httpStatus);
        }
        return responseEntity;
    }

    //order req list by cus id - for customer side - 3
    @GetMapping("/cuslist")
    public ResponseEntity<?> customerList(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        ResponseEntity<?> responseEntity;
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ResponseBean responseBean = new ResponseBean();
        DataTableBean dataTableBean = new DataTableBean();
        try {
            dataTableBean = orderRequestservice.customerList(page, size);
            httpStatus = HttpStatus.OK;
            responseBean.setContent(dataTableBean);
            responseBean.setResponseMsg(dataTableBean.getMsg());
            responseBean.setResponseCode(dataTableBean.getCode());
        }catch (Exception ex){
            log.error("Error occurred while searching item list.{} ", ex.getMessage());
        }finally {
            responseEntity = new ResponseEntity<>(responseBean, httpStatus);
        }
        return responseEntity;
    }

    //View all order req list - for Admin - 5
    @GetMapping("/alllist")
    public ResponseEntity<?> orderReqList(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        ResponseEntity<?> responseEntity;
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ResponseBean responseBean = new ResponseBean();
        DataTableBean dataTableBean = new DataTableBean();
        try {
            dataTableBean = orderRequestservice.orderReqList(page, size);
            httpStatus = HttpStatus.OK;
            responseBean.setContent(dataTableBean);
            responseBean.setResponseMsg(dataTableBean.getMsg());
            responseBean.setResponseCode(dataTableBean.getCode());
        }catch (Exception ex){
            log.error("Error occurred while searching item list.{} ", ex.getMessage());
        }finally {
            responseEntity = new ResponseEntity<>(responseBean, httpStatus);
        }
        return responseEntity;
    }

    //View full details of each order - by order id - for both Admin & Customer - 4, 6
    @GetMapping("/alllist/{orderid}")
    public ResponseEntity<?> viewOrderDetails(@PathVariable String orderid) {
        ResponseEntity<?> responseEntity;
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ResponseBean responseBean = new ResponseBean();

        try {
            responseBean = orderRequestservice.viewOrderDetails(orderid);
            httpStatus = HttpStatus.OK;
        }catch (Exception ex){
            log.error("Error occurred while retrieving inventory Item.{} ", ex.getMessage());
        }finally {
            responseEntity = new ResponseEntity<>(responseBean, httpStatus);
        }
        return responseEntity;
    }

    //TODO View all order req list by order status - for Admin

    //TODO Order req update (Status update , add Remark or Assign new package according to cus requirements) - for Admin - 7

    //TODO Order req update - for customer side

    //TODO Accept / Reject

}
