package com.highpass.runspot.session.domain.dao;

import com.highpass.runspot.auth.domain.User;
import com.highpass.runspot.session.domain.Session;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SessionRepository extends JpaRepository<Session, Long> {
    // 특정 상태의 최신 3개만 조회
    @Query("""
        SELECT s FROM Session s 
        WHERE s.hostUser = :hostUser 
        ORDER BY 
            CASE s.status 
                WHEN 'OPEN' THEN 1 
                WHEN 'CLOSED' THEN 2 
                WHEN 'FINISHED' THEN 3 
            END ASC,
            s.createdAt DESC
        LIMIT 3
        """)
    List<Session> findTop3ByHostUserOrderByStatusAscCreatedAtDesc(@Param("hostUser") User hostUser);
}
