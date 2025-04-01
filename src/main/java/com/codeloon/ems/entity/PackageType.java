package com.codeloon.ems.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "package_type")
public class PackageType {
    @Id
    @Column(name = "code", updatable = false, nullable = false, length = 10)
    private String code;  // Custom unique ID

    @Column(nullable = false, unique = true, length = 20)
    private String description;
}
