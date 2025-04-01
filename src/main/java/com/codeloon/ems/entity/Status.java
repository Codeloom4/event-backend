package com.codeloon.ems.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "status")
public class Status {
    @Id
    @Column(name = "code", length = 20)
    private String code;

    @Column(name = "description", length = 100, nullable = false)
    private String description;

    @Column(name = "type", length = 50, nullable = false)
    private String type;
}
