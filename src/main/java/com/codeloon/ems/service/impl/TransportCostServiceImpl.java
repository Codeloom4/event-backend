package com.codeloon.ems.service.impl;

import com.codeloon.ems.entity.District;
import com.codeloon.ems.entity.TransportCost;
import com.codeloon.ems.model.TransportCostBean;
import com.codeloon.ems.repository.DistrictRepository;
import com.codeloon.ems.repository.TransportCostRepository;
import com.codeloon.ems.service.TransportCostService;
import com.codeloon.ems.util.ResponseBean;
import com.codeloon.ems.util.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransportCostServiceImpl implements TransportCostService {

    private final TransportCostRepository transportCostRepository;
    private final DistrictRepository districtRepository;

    @Override
    public List<TransportCostBean> getAllTransportCosts() {
        return transportCostRepository.findAll().stream()
                .map(this::convertToBean)
                .collect(Collectors.toList());
    }

    @Override
    public ResponseBean findTransportCostById(Long transportCostId) {
        ResponseBean responseBean = new ResponseBean();
        try {
            Optional<TransportCost> transportCostOptional = transportCostRepository.findById(transportCostId);
            if (transportCostOptional.isPresent()) {
                TransportCostBean transportCostBean = convertToBean(transportCostOptional.get());
                responseBean.setResponseCode(ResponseCode.RSP_SUCCESS);
                responseBean.setResponseMsg("Transport cost retrieved successfully.");
                responseBean.setContent(transportCostBean);
            } else {
                responseBean.setResponseCode(ResponseCode.RSP_ERROR);
                responseBean.setResponseMsg("Transport cost not found.");
            }
        } catch (Exception ex) {
            log.error("Error occurred while retrieving transport cost", ex);
            responseBean.setResponseCode(ResponseCode.RSP_ERROR);
            responseBean.setResponseMsg("Error occurred while retrieving transport cost.");
        }
        return responseBean;
    }

    @Override
    public ResponseBean createTransportCost(TransportCostBean transportCostBean) {
        ResponseBean responseBean = new ResponseBean();
        try {
            Optional<District> districtOptional = districtRepository.findById(transportCostBean.getDistrictId());
            if (districtOptional.isPresent()) {
                TransportCost transportCost = new TransportCost();
                transportCost.setDistrict(districtOptional.get());
                transportCost.setDeliveryFee(transportCostBean.getDeliveryFee());
                transportCost.setCreatedAt(LocalDateTime.now());
                TransportCost savedTransportCost = transportCostRepository.save(transportCost);
                responseBean.setResponseCode(ResponseCode.RSP_SUCCESS);
                responseBean.setResponseMsg("Transport cost created successfully.");
                responseBean.setContent(convertToBean(savedTransportCost));
            } else {
                responseBean.setResponseCode(ResponseCode.RSP_ERROR);
                responseBean.setResponseMsg("District not found.");
            }
        } catch (Exception ex) {
            log.error("Error occurred while creating transport cost", ex);
            responseBean.setResponseCode(ResponseCode.RSP_ERROR);
            responseBean.setResponseMsg("Error occurred while creating transport cost.");
        }
        return responseBean;
    }

    @Override
    public ResponseBean updateTransportCost(Long transportCostId, TransportCostBean transportCostBean) {
        ResponseBean responseBean = new ResponseBean();
        try {
            Optional<TransportCost> transportCostOptional = transportCostRepository.findById(transportCostId);
            Optional<District> districtOptional = districtRepository.findById(transportCostBean.getDistrictId());
            if (transportCostOptional.isPresent() && districtOptional.isPresent()) {
                TransportCost transportCost = transportCostOptional.get();
                transportCost.setDistrict(districtOptional.get());
                transportCost.setDeliveryFee(transportCostBean.getDeliveryFee());
                TransportCost updatedTransportCost = transportCostRepository.save(transportCost);
                responseBean.setResponseCode(ResponseCode.RSP_SUCCESS);
                responseBean.setResponseMsg("Transport cost updated successfully.");
                responseBean.setContent(convertToBean(updatedTransportCost));
            } else {
                responseBean.setResponseCode(ResponseCode.RSP_ERROR);
                responseBean.setResponseMsg("Transport cost or District not found.");
            }
        } catch (Exception ex) {
            log.error("Error occurred while updating transport cost", ex);
            responseBean.setResponseCode(ResponseCode.RSP_ERROR);
            responseBean.setResponseMsg("Error occurred while updating transport cost.");
        }
        return responseBean;
    }

    @Override
    public ResponseBean deleteTransportCost(Long transportCostId) {
        ResponseBean responseBean = new ResponseBean();
        try {
            if (transportCostRepository.existsById(transportCostId)) {
                transportCostRepository.deleteById(transportCostId);
                responseBean.setResponseCode(ResponseCode.RSP_SUCCESS);
                responseBean.setResponseMsg("Transport cost deleted successfully.");
            } else {
                responseBean.setResponseCode(ResponseCode.RSP_ERROR);
                responseBean.setResponseMsg("Transport cost not found.");
            }
        } catch (Exception ex) {
            log.error("Error occurred while deleting transport cost", ex);
            responseBean.setResponseCode(ResponseCode.RSP_ERROR);
            responseBean.setResponseMsg("Error occurred while deleting transport cost.");
        }
        return responseBean;
    }

    @Override
    public ResponseBean getAllDistricts() {
        ResponseBean responseBean = new ResponseBean();
        try {
            List<District> districts = districtRepository.findAll();
            responseBean.setResponseCode(ResponseCode.RSP_SUCCESS);
            responseBean.setResponseMsg("Districts retrieved successfully.");
            responseBean.setContent(districts);
        } catch (Exception ex) {
            log.error("Error occurred while retrieving districts", ex);
            responseBean.setResponseCode(ResponseCode.RSP_ERROR);
            responseBean.setResponseMsg("Error occurred while retrieving districts.");
        }
        return responseBean;
    }

    private TransportCostBean convertToBean(TransportCost transportCost) {
        TransportCostBean transportCostBean = new TransportCostBean();
        BeanUtils.copyProperties(transportCost, transportCostBean);
        transportCostBean.setDistrictId(transportCost.getDistrict().getId());
        transportCostBean.setDistrictName(transportCost.getDistrict().getDistrictName());
        return transportCostBean;
    }
}

