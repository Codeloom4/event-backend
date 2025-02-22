package com.codeloon.ems.controller;

import com.codeloon.ems.model.TransportCostBean;
import com.codeloon.ems.service.TransportCostService;
import com.codeloon.ems.util.ResponseBean;
import com.codeloon.ems.util.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@RestController
@RequestMapping("/ems/transport-costs")
@RequiredArgsConstructor
public class TransportCostController {

    private final TransportCostService transportCostService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<?> getAllTransportCosts() {
        ResponseEntity<?> responseEntity;
                ResponseBean responseBean = new ResponseBean();
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        try {
            List<TransportCostBean> trancosts = transportCostService.getAllTransportCosts();
            responseBean.setResponseCode(ResponseCode.RSP_SUCCESS);
            responseBean.setResponseMsg("transport costs retrieved successfully.");
            responseBean.setContent(trancosts);
            httpStatus = HttpStatus.OK;
        } catch (Exception ex) {
            log.error("Error occurred while retrieving event list.{} ", ex.getMessage());
        } finally {
            responseEntity = new ResponseEntity<>(responseBean, httpStatus);
        }
        return responseEntity;


    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<ResponseBean> getTransportCostById(@PathVariable Long id) {
        return ResponseEntity.ok(transportCostService.findTransportCostById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<ResponseBean> createTransportCost(@RequestBody TransportCostBean transportCostBean) {
        return ResponseEntity.ok(transportCostService.createTransportCost(transportCostBean));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<ResponseBean> updateTransportCost(@PathVariable Long id, @RequestBody TransportCostBean transportCostBean) {
        return ResponseEntity.ok(transportCostService.updateTransportCost(id, transportCostBean));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<ResponseBean> deleteTransportCost(@PathVariable Long id) {
        return ResponseEntity.ok(transportCostService.deleteTransportCost(id));
    }

    @GetMapping("/districts")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE') or hasRole('CLIENT')")
    public ResponseEntity<ResponseBean> getAllDistricts() {
        return ResponseEntity.ok(transportCostService.getAllDistricts());
    }
}

