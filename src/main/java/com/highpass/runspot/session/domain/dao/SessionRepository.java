package com.highpass.runspot.session.domain.dao;

import com.highpass.runspot.session.domain.Session;
import com.highpass.runspot.session.domain.SessionStatus;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, Long> {

    List<Session> findByStatusAndTitleContainingAndIdLessThanOrderByIdDesc(SessionStatus sessionStatus, String title, Long id, Pageable pageable);

    List<Session> findByStatusAndTitleContainingOrderByIdDesc(SessionStatus sessionStatus, String title, Pageable pageable);
}
