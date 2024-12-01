package com.ecom.repository;

import com.ecom.entity.UserDtls;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserReposistory extends JpaRepository<UserDtls, Integer> {
    UserDtls findByEmail(String email);
    List<UserDtls> findByRole(String role);
    UserDtls findByVerificationCode(String token);
    Boolean existsByEmail(String email);
}
