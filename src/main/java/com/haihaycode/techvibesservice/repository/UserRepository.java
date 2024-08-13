package com.haihaycode.techvibesservice.repository;

import com.haihaycode.techvibesservice.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
    @Query("SELECT u FROM UserEntity u "
            + "WHERE (:keyword IS NULL OR u.fullName LIKE %:keyword% OR u.email LIKE %:keyword%) AND "
            + "(:available IS NULL OR u.available = :available) AND "
            + "(:roleName IS NULL OR EXISTS (SELECT r FROM u.roles r WHERE r.name LIKE %:roleName%))")
    Page<UserEntity> findUsersByCriteria(@Param("keyword") String keyword,
                                         @Param("available") Boolean available,
                                         @Param("roleName") String roleName,
                                         Pageable pageable);

    Optional<UserEntity> findByVnpTxnRef(String vnpTxnRef);

}
