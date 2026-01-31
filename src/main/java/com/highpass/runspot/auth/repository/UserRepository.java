package com.highpass.runspot.auth.repository;

import com.highpass.runspot.auth.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
