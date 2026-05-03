package com.ritvik.zodiacverseBackend.repo;

import com.ritvik.zodiacverseBackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface  UserRepo extends JpaRepository<User,Integer>{

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);


}
