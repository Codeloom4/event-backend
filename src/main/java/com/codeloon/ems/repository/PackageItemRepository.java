package com.codeloon.ems.repository;

import com.codeloon.ems.entity.PackageItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PackageItemRepository extends JpaRepository<PackageItem, String> {

}
