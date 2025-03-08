package com.codeloon.ems.service;

import com.codeloon.ems.model.GroupingBean;
import com.codeloon.ems.util.ResponseBean;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface GroupingService {
    ResponseBean createGrouping(GroupingBean groupingBean, MultipartFile file);
    ResponseBean updateGrouping(Long id, GroupingBean groupingBean, MultipartFile file);
    ResponseBean deleteGrouping(Long id);
    ResponseBean processGrouping(Long id);
    List<GroupingBean> getGroupingsByUsername(String username);
}