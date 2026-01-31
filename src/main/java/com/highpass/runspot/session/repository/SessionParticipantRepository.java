package com.highpass.runspot.session.repository;

import com.highpass.runspot.auth.domain.User;
import com.highpass.runspot.session.domain.Session;
import com.highpass.runspot.session.domain.SessionParticipant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionParticipantRepository extends JpaRepository<SessionParticipant, Long> {

    boolean existsBySessionAndUser(Session session, User user);

    List<SessionParticipant> findBySessionId(Long sessionId);
}
