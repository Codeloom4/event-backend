package com.codeloon.ems.repository;

import com.codeloon.ems.entity.Package;
import com.codeloon.ems.entity.PackageItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface PackageItemRepository extends JpaRepository<PackageItem, String> {
    @Query("select p from PackageItem p where p.package_id.id = ?1 and p.itemName = ?2 ")
    Optional<PackageItem> findByPackageIdAndItemName(String packageId, String itemCode);

    @Query("select p from PackageItem p where p.package_id.id = ?1 ")
    List<PackageItem> findByPackageId(String packageId);
    @Modifying
    @Transactional
    @Query("delete from PackageItem p where p.package_id.id = ?1 ")
    void deleteByPackageId(String packageId);
    @Modifying
    @Transactional
    @Query("delete from PackageItem p where p.package_id.id = ?1 and p.itemName = ?2 ")
    void deleteByPackageIdAndItemName(String packageId, String itemCode);

}
