package com.codeloon.ems.service;

import com.codeloon.ems.model.TransportCostBean;
import com.codeloon.ems.util.ResponseBean;

import java.util.List;

public interface TransportCostService {
    List<TransportCostBean> getAllTransportCosts();
    ResponseBean findTransportCostById(Long transportCostId);
    ResponseBean createTransportCost(TransportCostBean transportCostBean);
    ResponseBean updateTransportCost(Long transportCostId, TransportCostBean transportCostBean);
    ResponseBean deleteTransportCost(Long transportCostId);
    ResponseBean getAllDistricts();
}

