package com.codeloon.ems.service;

import com.codeloon.ems.dto.PackageDto;
import com.codeloon.ems.dto.PackageItemDto;
import com.codeloon.ems.util.ResponseBean;
import org.springframework.data.domain.Pageable;

public interface PackageService {

    ResponseBean access();

    ResponseBean createPackage(PackageDto pack);

    ResponseBean updatePackage(PackageDto pack);

    ResponseBean deletePackage(String package_id);

    ResponseBean createPackageItem(PackageItemDto packageItemDto);

    ResponseBean getPackagesByEventType(String event);

    ResponseBean updatePackageItem(String packageId, PackageItemDto packItem);

    ResponseBean deletePackageItem(String itemCode, String packageId);

    ResponseBean getPackageItems(String packageId);

    ResponseBean getAllPackages(Pageable pageable);
}
