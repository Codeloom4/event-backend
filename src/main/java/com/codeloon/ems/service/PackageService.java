package com.codeloon.ems.service;

import com.codeloon.ems.dto.PackageDto;
import com.codeloon.ems.dto.PackageItemDto;
import com.codeloon.ems.util.ResponseBean;

public interface PackageService {
    ResponseBean createPackage(PackageDto pack);

    ResponseBean updatePackage(PackageDto pack);

    ResponseBean deletePackage(String package_id);

    ResponseBean createPackageItem(PackageItemDto packageItemDto);

}
