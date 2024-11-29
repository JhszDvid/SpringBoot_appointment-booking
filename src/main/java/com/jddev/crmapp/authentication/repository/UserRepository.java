package com.jddev.crmapp.authentication.repository;

import com.jddev.crmapp.authentication.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface UserRepository extends JpaRepository<AppUser, Integer> {
    Optional<AppUser> findByEmail(@NonNull String email);


}
