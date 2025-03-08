package com.codeloon.ems.repository;

import com.codeloon.ems.entity.PackageSlide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PackageSlideRepository extends JpaRepository<PackageSlide, Long> {
    List<PackageSlide> findByPackageId(String packageId);

    void deleteAllByPackageId(String packageId);
}
