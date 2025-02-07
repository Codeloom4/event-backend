package com.codeloon.ems.repository;

import com.codeloon.ems.entity.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long>{


    //Optional<Inventory> getByInventoryByName(String name);

    @Query("select i from Inventory i where lower(i.itemName) like lower(concat('%', ?1, '%') ) ")
    Page<Object[]> searchInventoryByName(String name, Pageable pageable);

}
