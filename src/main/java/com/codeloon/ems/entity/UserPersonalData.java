package com.codeloon.ems.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "userpersonaldata")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPersonalData {

    @Id
    private String userId;

    @OneToOne
    @MapsId  // Ensures 'userId' is taken from 'user.id'
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, updatable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String position;

    @Column(nullable = false, length = 20)
    private String mobile;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
