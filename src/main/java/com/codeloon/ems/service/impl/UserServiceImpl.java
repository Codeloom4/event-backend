package com.codeloon.ems.service.impl;

import com.codeloon.ems.dto.ResetDto;
import com.codeloon.ems.dto.UserDto;
import com.codeloon.ems.entity.PasswordHistory;
import com.codeloon.ems.entity.Role;
import com.codeloon.ems.entity.User;
import com.codeloon.ems.entity.UserPersonalData;
import com.codeloon.ems.model.EmailRequestBean;
import com.codeloon.ems.model.UserBean;
import com.codeloon.ems.repository.PasswordHistoryRepository;
import com.codeloon.ems.repository.RolesRepository;
import com.codeloon.ems.repository.UserPersonalDataRepository;
import com.codeloon.ems.repository.UserRepository;
import com.codeloon.ems.service.EmailSenderService;
import com.codeloon.ems.service.UserService;
import com.codeloon.ems.util.DataVarList;
import com.codeloon.ems.util.ResponseBean;
import com.codeloon.ems.util.ResponseCode;
import com.codeloon.ems.util.UserUtils;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final PasswordEncoder encoder;
    private final PasswordHistoryRepository passwordHistoryRepository;
    private final EntityManager entityManager;
    private final EmailSenderService emailSenderService;


    @Value("${ems.support.email}")
    private String support_email;
    @Value("${ems.support.contact}")
    private String support_contact;
    @Value("${ems.companyName}")
    private String companyName;

    @Override
    public ResponseBean getAllUsers(Pageable pageable) {
        ResponseBean responseBean = new ResponseBean();
        String msg = "";
        String code = ResponseCode.RSP_ERROR;

        List<UserBean> userBeans = new ArrayList<>();
        try {
            Page<User> users = userRepository.findAll(pageable);
            List<User> usersList = users.getContent();
            UserBean userBean = null;
            for (User user : usersList) {
                userBean = new UserBean();
                BeanUtils.copyProperties(user, userBean);
                UserPersonalData personalData = personalDataRepository.findById(user.getId()).orElse(null);
                if (personalData != null) {
                    userBean.setEmail(personalData.getAddress());
                    userBean.setAddress(personalData.getAddress());
                    userBean.setPosition(personalData.getPosition());
                    userBean.setMobileNo(personalData.getMobile());
                } else {
                    userBean.setEmail("NA");
                    userBean.setAddress("NA");
                    userBean.setPosition("NA");
                    userBean.setMobileNo("NA");
                }
                userBeans.add(userBean);
            }
            responseBean.setContent(userBeans);
            code = ResponseCode.RSP_SUCCESS;
            msg = "User retrieval success";

        } catch (Exception ex) {
            log.error("Error occurred while retrieving all system users", ex);
        }
        responseBean.setResponseCode(code);
        responseBean.setResponseMsg(msg);
        return responseBean;
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
                if (userRole.equalsIgnoreCase(DataVarList.ROLE_CLIENT)) {
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
                        .forcePasswordChange(userRole.equalsIgnoreCase(DataVarList.ROLE_CLIENT) ? true : false)
                        .createdAt(LocalDateTime.now())
                        .build();

                userEntity.setRoles(userDto.getRoles().stream()
                        .map(roleName -> roleRepository.findByName(roleName)
                                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)))
                        .collect(Collectors.toSet()));

                //save user
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

                //save password history
                PasswordHistory passwordHistory = PasswordHistory.builder()
                        .username(userDto.getUsername())
                        .password(userEntity.getPassword())
                        .build();
                passwordHistoryRepository.saveAndFlush(passwordHistory);

                //send user creation email notification
                if (!userRole.equalsIgnoreCase(DataVarList.ROLE_CLIENT)) {
                    emailSenderService.sendPlainTextEmail(formCredentialEmail(userDto));
                }

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
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ResponseBean updateUser(String userId, UserDto userDto) {
        ResponseBean responseBean = new ResponseBean();
        String msg;
        String code = ResponseCode.RSP_ERROR;

        try {
            Optional<User> userOptional = userRepository.findById(userId);

            if (userOptional.isPresent()) {
                User userEntity = userOptional.get();
                userEntity.setUsername(userDto.getUsername());
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
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ResponseBean resetPassword(ResetDto resetDto) {
        ResponseBean responseBean = new ResponseBean();
        String msg = "";
        String code = ResponseCode.RSP_ERROR;
        try {
            // Check if user exists
            log.info("user password reset request for userName : {}", resetDto.getUserName());
            User user = userRepository.findByUsername(resetDto.getUserName()).orElse(null);

            if (user != null) {
                // Check if old password matches the stored password
                if (encoder.matches(resetDto.getOldPassword(), user.getPassword())) {

                    // Check if the new password has already been used before
                    String newEncodedPassword = encoder.encode(resetDto.getPassword());
                    List<PasswordHistory> passwordHistoryList = passwordHistoryRepository
                            .findAllByUsernameAndPassword(resetDto.getUserName(), newEncodedPassword);

                    if (passwordHistoryList.isEmpty()) {
                        // Update the password
                        user.setPassword(newEncodedPassword);
                        user.setForcePasswordChange(false);
                        userRepository.saveAndFlush(user);

                        // Save the new password in password history
                        PasswordHistory passwordHistory = PasswordHistory.builder()
                                .username(resetDto.getUserName())
                                .password(newEncodedPassword)
                                .build();
                        passwordHistoryRepository.saveAndFlush(passwordHistory);

                        msg = "User password updated successfully.";
                        code = ResponseCode.RSP_SUCCESS;
                    } else {
                        msg = "Old passwords cannot be reused.";
                    }
                } else {
                    msg = "Incorrect old password.";
                }
            } else {
                msg = "User does not exist.";
            }
        } catch (Exception ex) {
            log.error("Error occurred while resetting user password", ex);
            msg = "Error occurred while resetting user password.";
        } finally {
            responseBean.setResponseMsg(msg);
            responseBean.setResponseCode(code);
            responseBean.setContent(null);
        }
        return responseBean;
    }

    @Override
    public ResponseBean deleteUser(String userId) {
        ResponseBean responseBean = new ResponseBean();
        String msg;
        String code = ResponseCode.RSP_ERROR;

        try {
            UserPersonalData personalData = personalDataRepository.findById(userId).orElse(null);
            User user = userRepository.findById(userId).orElse(null);

            if (personalData != null) {

                personalDataRepository.delete(personalData);
                userRepository.delete(user);

                msg = "User deleted successfully.";
                code = ResponseCode.RSP_SUCCESS;
            } else {
                msg = "User does not exist.";
            }
        } catch (Exception ex) {
            log.error("Error occurred while deleting user: {}", ex.getMessage(), ex);
            msg = "An error occurred while deleting user details.";
        }

        responseBean.setResponseMsg(msg);
        responseBean.setResponseCode(code);
        responseBean.setContent(null);

        return responseBean;
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

    private EmailRequestBean formCredentialEmail(UserDto userDto) {
        StringBuilder emailBody = new StringBuilder();

        emailBody.append("Dear ").append(userDto.getUsername()).append(",\n\n")
                .append("Welcome to ").append(companyName).append("! We are excited to have you on board.\n\n")
                .append("Your account has been successfully created. Below are your login credentials:\n\n")
                .append("🔹 Username: ").append(userDto.getUsername()).append("\n")
                .append("🔹 Temporary Password: ").append(userDto.getPassword()).append("\n\n")
                .append("To ensure the security of your account, please log in and change your password immediately.\n\n")
                .append("How to Log In:\n")
                .append("1. Visit: ").append("http://localhost:5000/ems/reset").append("\n")
                .append("2. Enter your username and temporary password.\n")
                .append("3. Follow the on-screen instructions to set a new password.\n\n")
                .append("If you have any questions or need assistance, feel free to contact our support team at ")
                .append(support_email).append(".\n\n")
                .append("We look forward to working with you!\n\n")
                .append("Best regards,\n")
                .append(companyName).append(" Team\n")
                .append(support_contact);

        String subject = " Welcome to " + companyName + " – Your Account Details ";

        return EmailRequestBean.builder()
                .to(userDto.getEmail())
                .subject(subject)
                .text(emailBody.toString())
                .build();
    }

}
