package com.codeloon.ems.service;

import com.codeloon.ems.dto.ResetDto;
import com.codeloon.ems.dto.UserDto;
import com.codeloon.ems.entity.Role;
import com.codeloon.ems.entity.User;
import com.codeloon.ems.entity.UserPersonalData;
import com.codeloon.ems.model.CommonResponse;
import com.codeloon.ems.model.UserBean;
import com.codeloon.ems.repository.RolesRepository;
import com.codeloon.ems.repository.UserPersonalDataRepository;
import com.codeloon.ems.repository.UserRepository;
import com.codeloon.ems.util.DataVarList;
import com.codeloon.ems.util.ResponseBean;
import com.codeloon.ems.util.ResponseCode;
import com.codeloon.ems.util.UserUtils;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RolesRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserPersonalDataRepository personalDataRepository;
    private final EntityManager entityManager;

    @Override
    public List<UserBean> getAllUsers() {
        List<UserBean> userBeans = new ArrayList<>();
        try {
            List<User> users = userRepository.findAll();
            UserBean userBean = null;
            for (User user : users) {
                userBean = new UserBean();
                BeanUtils.copyProperties(user, userBean);
                userBeans.add(userBean);
            }
        } catch (Exception ex) {
            log.error("Error occurred while retrieving all system users", ex);
        }
        return userBeans;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ResponseBean createUser(String userRole, UserDto userDto) {
        ResponseBean responseBean = new ResponseBean();
        String msg = "";
        String code = ResponseCode.RSP_ERROR;
        try {
            String customerId;
            Optional<User> user = userRepository.findByUsername(userDto.getUsername());
            if (user.isEmpty()) {
                customerId = UserUtils.generateCustomUUID(userRole, userDto.getUsername());

                // If role is customer.
                if(userRole.equalsIgnoreCase(DataVarList.ROLE_CLIENT)){
                    userDto.getRoles().add(DataVarList.ROLE_CLIENT);
                }

                User userEntity = User.builder()
                        .id(customerId)
                        .username(userDto.getUsername())
                        .password(passwordEncoder.encode(userDto.getPassword()))
                        .email(userDto.getEmail())
                        .enabled(userDto.getEnabled())
                        .accountNonExpired(true)
                        .credentialsNonExpired(true)
                        .accountNonLocked(true)
                        .forcePasswordChange(!userRole.equalsIgnoreCase(DataVarList.ROLE_CLIENT))
                        .createdAt(LocalDateTime.now())
                        .build();

                userEntity.setRoles(userDto.getRoles().stream()
                        .map(roleName -> roleRepository.findByName(roleName)
                                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)))
                        .collect(Collectors.toSet()));

                User savedUserEntity = userRepository.save(userEntity);
                entityManager.flush();  // Ensure it's persisted before using it in UserPersonalData

                //save personal data
                UserPersonalData personalData = UserPersonalData.builder()
                        .user(savedUserEntity)
                        .position(userDto.getPosition())
                        .mobile(userDto.getMobileNo())
                        .address(userDto.getAddress())
                        .createdAt(LocalDateTime.now())
                        .build();

                personalDataRepository.saveAndFlush(personalData);

                code = ResponseCode.RSP_SUCCESS;
                msg = "User created successfully.";
                log.info("User created successfully. User name : {}, UserID : {}", userDto.getUsername(), customerId);
            } else {
                msg = "Username already exist.";
            }
        } catch (Exception ex) {
            log.error("Error occurred while creating system user", ex);
            msg = "Error occurred while creating system user.";
        } finally {
            responseBean.setResponseMsg(msg);
            responseBean.setResponseCode(code);
            responseBean.setContent(userDto);
        }
        return responseBean;
    }

    @Override
    public ResponseBean updateUser(String userId, UserDto userDto) {
        ResponseBean responseBean = new ResponseBean();
        String msg;
        String code = ResponseCode.RSP_ERROR;

        try {
            Optional<User> userOptional = userRepository.findById(userId);

            if (userOptional.isPresent()) {
                User userEntity = userOptional.get();
                userEntity.setEmail(userDto.getEmail());
                userEntity.setEnabled(userDto.getEnabled());

                // Save the updated User entity
                userRepository.save(userEntity);

                // Update UserPersonalData if exists
                Optional<UserPersonalData> personalDataOptional = personalDataRepository.findById(userDto.getId());

                if (personalDataOptional.isPresent()) {
                    UserPersonalData personalData = personalDataOptional.get();
                    personalData.setAddress(userDto.getAddress());
                    personalData.setMobile(userDto.getMobileNo());
                    personalData.setPosition(userDto.getPosition());
                    personalData.setUser(userEntity);

                    // Save updated personal data
                    personalDataRepository.save(personalData);
                }

                msg = "User updated successfully.";
                code = ResponseCode.RSP_SUCCESS;
            } else {
                msg = "User does not exist.";
            }
        } catch (Exception ex) {
            log.error("Error occurred while updating user: {}", ex.getMessage(), ex);
            msg = "An error occurred while updating user details.";
        }

        responseBean.setResponseMsg(msg);
        responseBean.setResponseCode(code);
        responseBean.setContent(userDto);

        return responseBean;
    }

    @Override
    public ResponseEntity<CommonResponse> resetPassword(ResetDto resetDto) {
        return null;
    }


    @Override
    public ResponseBean findByUsername(String userName) {
        ResponseBean responseBean = new ResponseBean();
        String msg;
        String code = ResponseCode.RSP_ERROR;

        try {
            User user = userRepository.findByUsername(userName).orElse(null);

            if (user != null) {
                UserDto userDto = new UserDto();

                BeanUtils.copyProperties(user, userDto);

                Set<String> roleNames = user.getRoles().stream()
                        .map(Role::getName) // Extract role name
                        .collect(Collectors.toSet()); // Collect into a Set<String>

                userDto.setRoles(roleNames);

                userDto.setAddress(user.getPersonalData().getAddress());
                userDto.setMobileNo(user.getPersonalData().getMobile());
                userDto.setPosition(user.getPersonalData().getPosition());

                responseBean.setContent(userDto);

                msg = "User retrieved successfully.";
                code = ResponseCode.RSP_SUCCESS;
            } else {
                log.warn("User with username '{}' not found", userName);
                msg = "User not found: " + userName;
            }
        } catch (Exception ex) {
            log.error("Error occurred while retrieving system user: {}", ex.getMessage(), ex);
            msg = "Error occurred while retrieving system user.";
        }

        responseBean.setResponseMsg(msg);
        responseBean.setResponseCode(code);
        return responseBean;
    }

    @Override
    public ResponseBean findByUserId(String userId) {
        ResponseBean responseBean = new ResponseBean();
        String msg;
        String code = ResponseCode.RSP_ERROR;

        try {
            User user = userRepository.findById(userId).orElse(null);

            if (user != null) {
                UserDto userDto = new UserDto();

                BeanUtils.copyProperties(user, userDto);

                Set<String> roleNames = user.getRoles().stream()
                        .map(Role::getName) // Extract role name
                        .collect(Collectors.toSet()); // Collect into a Set<String>

                userDto.setRoles(roleNames);

                userDto.setAddress(user.getPersonalData().getAddress());
                userDto.setMobileNo(user.getPersonalData().getMobile());
                userDto.setPosition(user.getPersonalData().getPosition());

                responseBean.setContent(userDto);

                msg = "User retrieved successfully.";
                code = ResponseCode.RSP_SUCCESS;
            } else {
                log.warn("User with userId '{}' not found", userId);
                msg = "User not found: " + userId;
            }
        } catch (Exception ex) {
            log.error("Error occurred while retrieving system user: {}", ex.getMessage(), ex);
            msg = "Error occurred while retrieving system user.";
        }

        responseBean.setResponseMsg(msg);
        responseBean.setResponseCode(code);
        return responseBean;
    }

}
