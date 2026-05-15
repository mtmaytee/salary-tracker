package com.dev.salarytracker.repository;

import com.dev.salarytracker.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsersRepository extends JpaRepository<Users, UUID> {
    // Spring Data JPA จะสร้าง Query ให้เองอัตโนมัติจากชื่อ Method
    Optional<Users> findByUsername(String username);

    Users findByVerificationToken(String token);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByNationalId(String nationalId); // เพิ่มตัวนี้
    boolean existsByPhoneNumber(String phoneNumber); // เพิ่มตัวนี้
}
