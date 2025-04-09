package com.codeloon.ems.controller;

import com.codeloon.ems.dto.UserDto;
import com.codeloon.ems.model.UserBean;
import com.codeloon.ems.service.UserService;
import com.codeloon.ems.util.DataVarList;
import com.codeloon.ems.util.ResponseBean;
import com.codeloon.ems.util.ResponseCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/ems/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<?> getAllUser(Pageable pageable) {
        ResponseEntity<?> responseEntity;
        ResponseBean responseBean = new ResponseBean();
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        try {
            responseBean = userService.getAllUsers(pageable);
            responseBean.setResponseCode(ResponseCode.RSP_SUCCESS);
            httpStatus = HttpStatus.OK;
        } catch (Exception ex) {
            log.error("Error occurred while retrieving user list.{} ", ex.getMessage());
        } finally {
            responseEntity = new ResponseEntity<>(responseBean, httpStatus);
        }
        return responseEntity;
    }

    @GetMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE')")
    public ResponseEntity<?> geUserByUsername(@PathVariable String username) {
        ResponseEntity<?> responseEntity;
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ResponseBean responseBean = new ResponseBean();
        try {
            responseBean = userService.findByUserId(username);
            httpStatus = HttpStatus.CREATED;
        } catch (Exception ex) {
            log.error("Error occurred while retrieving new user.{} ", ex.getMessage());
        } finally {
            responseEntity = new ResponseEntity<>(responseBean, httpStatus);
        }
        return responseEntity;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDto user) {
        ResponseEntity<?> responseEntity;
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ResponseBean responseBean = new ResponseBean();
        try {
            responseBean = userService.createUser(user.getRole(), user);
            httpStatus = HttpStatus.CREATED;
        } catch (Exception ex) {
            log.error("Error occurred while saving new user.{} ", ex.getMessage());
        } finally {
            responseEntity = new ResponseEntity<>(responseBean, httpStatus);
        }
        return responseEntity;
    }

    @PostMapping("/register")
    public ResponseEntity<?> createClientUser(@RequestBody UserDto user) {
        ResponseEntity<?> responseEntity;
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ResponseBean responseBean = new ResponseBean();
        try {
            responseBean = userService.createUser(DataVarList.ROLE_CLIENT, user);
            httpStatus = HttpStatus.CREATED;
        } catch (Exception ex) {
            log.error("Error occurred while saving new client user.{} ", ex.getMessage());
        } finally {
            responseEntity = new ResponseEntity<>(responseBean, httpStatus);
        }
        return responseEntity;
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPLOYEE') or hasRole('CLIENT')")
    public ResponseEntity<?> updateUser(@PathVariable String userId, @RequestBody UserDto user) {
        ResponseEntity<?> responseEntity;
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ResponseBean responseBean = new ResponseBean();
        try {
            responseBean = userService.updateUser(userId, user);
            httpStatus = HttpStatus.CREATED;
        } catch (Exception ex) {
            log.error("Error occurred while saving new client user.{} ", ex.getMessage());
        } finally {
            responseEntity = new ResponseEntity<>(responseBean, httpStatus);
        }
        return responseEntity;
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable String userId) {
        ResponseEntity<?> responseEntity;
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ResponseBean responseBean = new ResponseBean();
        try {
            responseBean = userService.deleteUser(userId);
            httpStatus = HttpStatus.OK;
        } catch (Exception ex) {
            log.error("Error occurred while saving new client user.{} ", ex.getMessage());
        } finally {
            responseEntity = new ResponseEntity<>(responseBean, httpStatus);
        }
        return responseEntity;
    }
}