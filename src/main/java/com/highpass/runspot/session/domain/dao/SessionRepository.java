package com.highpass.runspot.session.domain.dao;

import com.highpass.runspot.session.domain.Session;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, Long> {
}
