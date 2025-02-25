package com.codeloon.ems.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

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

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "package_type", referencedColumnName = "code", nullable = false)
    private PackageType package_type;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "eventtype", referencedColumnName = "eventtype", nullable = false)
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

    // One-to-Many relationship with PackageItem
    @OneToMany(mappedBy = "package_id", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PackageItem> packageItems;

}
