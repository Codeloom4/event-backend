package com.codeloon.ems.model;

import com.codeloon.ems.dto.PackageInfoDTO;
import com.codeloon.ems.dto.PackageItemDto;
import com.codeloon.ems.entity.PackageSlide;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PackageViewBean {
    private List<PackageSlide> packageSlides;
    private PackageInfoDTO packageInfo;
    private List<PackageItemDto> packageItems;

}
