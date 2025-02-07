package com.codeloon.ems.service;

import com.codeloon.ems.dto.InventoryDto;
import com.codeloon.ems.entity.Inventory;
import com.codeloon.ems.model.DataTableBean;
import com.codeloon.ems.repository.InventoryRepository;
import com.codeloon.ems.util.ResponseBean;
import com.codeloon.ems.util.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    @Override
    public List<InventoryDto> getAllInventory() {
        List<InventoryDto> inventoryDtoList = new ArrayList<>();

        try {
            List<Inventory> inventoryList = inventoryRepository.findAll();
            inventoryList.forEach(inventory -> {
                InventoryDto inventoryDto = new InventoryDto();
                BeanUtils.copyProperties(inventory, inventoryDto);
                inventoryDtoList.add(inventoryDto);
            });

        } catch (Exception ex) {
            log.error("Error occurred while retrieving all inventory details", ex);
        }
        return inventoryDtoList;
    }

    @Override
    public DataTableBean getInventoryByName(String itemName, int page, int size) {
        DataTableBean dataTableBean = new DataTableBean();
        String msg = "";
        String code = ResponseCode.RSP_ERROR;

        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("itemName").ascending());
            Page<Object[]> inventoryList = inventoryRepository.searchInventoryByName(itemName, pageable);

            if(!inventoryList.isEmpty()){
                List<Object> inventoryDataList = this.mapSearchData(inventoryList);

                dataTableBean.setList(inventoryDataList);
                dataTableBean.setCount(inventoryList.getTotalElements());
                dataTableBean.setPagecount(inventoryList.getTotalPages());

                msg = "Inventory searched successfully.";
                code = ResponseCode.RSP_SUCCESS;
            }else {
                dataTableBean.setCount(0);
                dataTableBean.setPagecount(0);

                log.warn("Inventory item with iemName '{}' not found", itemName);
                msg = "Inventory not found: " + itemName;
            }

        } catch (Exception ex) {
            log.error("Error occurred while searching inventory : {}", ex.getMessage(), ex);
            msg = "Error occurred while searching inventory.";
        }

        dataTableBean.setMsg(msg);
        dataTableBean.setCode(code);
        return dataTableBean;
    }


    @Override
    public ResponseBean createInventory(InventoryDto inventory) {
        return null;
    }

    @Override
    public ResponseBean updateInventory(InventoryDto inventory) {
        return null;
    }

    @Override
    public ResponseBean deleteInventory(Long inventoryId) {
        return null;
    }

    @Override
    public ResponseBean createInventory(String userRole, InventoryDto inventory) {
        return null;
    }


    private List<Object> mapSearchData(Page<Object[]> dataList) {
        List<Object> advanceSearchDataBeanList = new ArrayList<>();
        dataList.forEach(data -> {
            InventoryDto searchDataBean = new InventoryDto();

            searchDataBean.setId((Long) data[0]);
            searchDataBean.setItemName((String) data[1]);
            searchDataBean.setIsRefundable((Boolean) data[2]);
            searchDataBean.setPurchasePrice((Long) data[3]);
            searchDataBean.setSalesPrice((Long) data[4]);
            searchDataBean.setOrderQuantity((Integer) data[5]);
            searchDataBean.setSalesQuantity((Integer) data[6]);
            searchDataBean.setBalanceQuantity((Integer) data[7]);
            searchDataBean.setStartBarcode((String) data[8]);
            searchDataBean.setEndBarcode((String) data[9]);

            advanceSearchDataBeanList.add(searchDataBean);
        });
        return advanceSearchDataBeanList;
    }



}
