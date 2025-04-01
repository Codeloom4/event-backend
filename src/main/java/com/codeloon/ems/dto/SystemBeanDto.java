package com.codeloon.ems.dto;

import lombok.*;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SystemBeanDto {
    private String sysUser;
}
