package com.codeloon.ems.model;

import com.codeloon.ems.dto.EventDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PackageMgtAccessBean {
    private List<EventDto> eventList;
    private List<PackageTypeBean> packageTypeBeanList;
    private List<String> uniqueCategories;
}
