package com.codeloon.ems.repository;

import com.codeloon.ems.entity.InventoryItem;
import com.codeloon.ems.model.InventoryItemBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
    @Query (value = "SELECT a.id, a.itemName FROM InventoryItem a")
    List<InventoryItemBean> findAllInventoryItems();
    Optional<InventoryItem> findInventoryItemByItemName(String name);
    Page<InventoryItem> findAll(Pageable pageable);

    Optional<InventoryItem> findInventoryItemById(Long id);

    @Query(value = "select i from InventoryItem i where i.category = ?1")
    List<InventoryItem> findInventoryItemsByCategory(String category);

    Page<InventoryItem> findAllByCategory(String category, Pageable pageable);

    Page<InventoryItem> findByItemNameAndIsRefundableContaining(String itemName, Boolean refundable, Pageable pageable);

    Page<InventoryItem> findByItemNameContaining(String itemName, Pageable pageable);

    Page<InventoryItem> findByIsRefundableContaining(Boolean refundable, Pageable pageable);

    Page<InventoryItem> findByIsRefundable(boolean b, Pageable pageable);

    Page<InventoryItem> findByItemNameAndIsRefundable(String itemName, Boolean isRefundable, Pageable pageable);

    @Query("SELECT itemName from InventoryItem where id = ?1 ")
    String getItemName(String itemCode);
}
