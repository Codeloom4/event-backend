package com.codeloon.ems.controller;

import com.codeloon.ems.dto.InventoryItemDto;
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


}
