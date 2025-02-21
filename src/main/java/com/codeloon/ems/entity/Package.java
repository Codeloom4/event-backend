package com.codeloon.ems.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "package")
public class Package {

    @Id
    @Column(name = "id", updatable = false, nullable = false, length = 50)
    private String id;  // Custom unique ID

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "eventtype")
    @OneToOne(mappedBy = "id", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    private Event event;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "created_user", referencedColumnName = "username", nullable = false)
    private User createdUser;

}
