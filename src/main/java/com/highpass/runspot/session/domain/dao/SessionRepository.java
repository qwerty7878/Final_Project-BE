package com.highpass.runspot.session.domain.dao;

import com.highpass.runspot.session.domain.Session;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findByTitleContaining(String title);
}
