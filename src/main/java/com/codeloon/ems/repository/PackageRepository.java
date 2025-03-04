package com.codeloon.ems.repository;

import com.codeloon.ems.entity.Event;
import com.codeloon.ems.entity.Package;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PackageRepository extends JpaRepository<Package, String> {

    List<Package> findPackagesByEvent(Event event);
}
