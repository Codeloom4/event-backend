package com.codeloon.ems.repository;

import com.codeloon.ems.entity.InventoryItem;
import com.codeloon.ems.model.InventoryItemBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
    @Query (value = "SELECT a.id, a.itemName FROM InventoryItem a")
    List<InventoryItemBean> findAllInventoryItems();
}
