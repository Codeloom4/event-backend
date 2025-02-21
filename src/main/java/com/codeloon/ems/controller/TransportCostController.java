package com.codeloon.ems.controller;

import com.codeloon.ems.model.TransportCostBean;
import com.codeloon.ems.service.TransportCostService;
import com.codeloon.ems.util.ResponseBean;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ems/transport-costs")
@RequiredArgsConstructor
public class TransportCostController {

    private final TransportCostService transportCostService;

    @GetMapping
    public ResponseEntity<List<TransportCostBean>> getAllTransportCosts() {
        return ResponseEntity.ok(transportCostService.getAllTransportCosts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseBean> getTransportCostById(@PathVariable Long id) {
        return ResponseEntity.ok(transportCostService.findTransportCostById(id));
    }

    @PostMapping
    public ResponseEntity<ResponseBean> createTransportCost(@RequestBody TransportCostBean transportCostBean) {
        return ResponseEntity.ok(transportCostService.createTransportCost(transportCostBean));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseBean> updateTransportCost(@PathVariable Long id, @RequestBody TransportCostBean transportCostBean) {
        return ResponseEntity.ok(transportCostService.updateTransportCost(id, transportCostBean));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseBean> deleteTransportCost(@PathVariable Long id) {
        return ResponseEntity.ok(transportCostService.deleteTransportCost(id));
    }

    @GetMapping("/districts")
    public ResponseEntity<ResponseBean> getAllDistricts() {
        return ResponseEntity.ok(transportCostService.getAllDistricts());
    }
}

