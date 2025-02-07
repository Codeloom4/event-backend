package com.codeloon.ems.service;

import com.codeloon.ems.dto.ResetDto;
import com.codeloon.ems.dto.UserDto;
import com.codeloon.ems.model.CommonResponse;
import com.codeloon.ems.model.UserBean;
import com.codeloon.ems.util.ResponseBean;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
    List<UserBean> getAllUsers();

    ResponseBean findByUsername(String userName);

    ResponseBean findByUserId(String userId);

    ResponseBean createUser(String userRole, UserDto emp);

    ResponseBean updateUser(String username, UserDto userDto);

    ResponseEntity<CommonResponse> resetPassword(ResetDto resetDto);
}
