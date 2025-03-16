package com.codeloon.ems.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PackageWithItemsDto {

    private PackageDto packageDto;
    private List<PackageItemDto> items;

}
