package com.highpass.runspot.session.domain.dao;

import com.highpass.runspot.auth.domain.User;
import com.highpass.runspot.session.domain.ParticipationStatus;
import com.highpass.runspot.session.domain.Session;
import com.highpass.runspot.session.domain.SessionParticipant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SessionParticipantRepository extends JpaRepository<SessionParticipant, Long> {

    boolean existsBySessionAndUser(Session session, User user);

    List<SessionParticipant> findBySessionId(Long sessionId);

    List<SessionParticipant> findBySessionIdAndStatus(Long sessionId, ParticipationStatus status);

    @Query("SELECT sp FROM SessionParticipant sp JOIN FETCH sp.user WHERE sp.session.id = :sessionId AND sp.status = :status")
    List<SessionParticipant> findBySessionIdAndStatusWithUser(@Param("sessionId") Long sessionId, @Param("status") ParticipationStatus status);

    long countBySessionIdAndStatus(Long sessionId, ParticipationStatus status);
}
