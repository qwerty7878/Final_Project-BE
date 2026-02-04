package com.highpass.runspot.session.domain.dao;

import com.highpass.runspot.session.domain.Session;
import com.highpass.runspot.session.domain.SessionStatus;
import java.util.List;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SessionRepository extends JpaRepository<Session, Long> {

    List<Session> findByStatusAndTitleContainingAndIdLessThanOrderByIdDesc(SessionStatus sessionStatus, String title, Long id, Pageable pageable);

    List<Session> findByStatusAndTitleContainingOrderByIdDesc(SessionStatus sessionStatus, String title, Pageable pageable);

    @Query("""
            SELECT s FROM Session s
            WHERE s.status = 'OPEN'
            AND within(s.location, :area) = true
            ORDER BY function('ST_Distance_Sphere', s.location, :point)
            """)
    List<Session> findNearestSessions(@Param("point") Point point, @Param("area") Geometry area, Pageable pageable);
}
