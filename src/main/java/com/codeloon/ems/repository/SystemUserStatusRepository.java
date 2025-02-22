package com.codeloon.ems.repository;

import com.codeloon.ems.entity.User;
import com.codeloon.ems.model.SystemUserStatusBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SystemUserStatusRepository extends JpaRepository<User, String> {

    @Query(nativeQuery = true, value =
            "SELECT u.id, u.username, u.email, upd.position, upd.mobile, upd.address, " +
                    "u.enabled, u.account_non_expired, u.credentials_non_expired, " +
                    "u.account_non_locked, u.force_password_change, u.created_at " +
                    "FROM users u " +
                    "LEFT JOIN userpersonaldata upd ON u.id = upd.user_id  ")
    List<SystemUserStatusBean> findAllSystemUserStatus();
}

