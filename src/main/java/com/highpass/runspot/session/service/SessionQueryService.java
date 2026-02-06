package com.highpass.runspot.session.service;

import static com.highpass.runspot.session.domain.SessionStatus.OPEN;

import com.highpass.runspot.common.util.GeometryUtil;
import com.highpass.runspot.session.domain.ParticipationStatus;
import com.highpass.runspot.session.domain.Session;
import com.highpass.runspot.session.domain.SessionParticipant;
import com.highpass.runspot.session.domain.dao.SessionParticipantRepository;
import com.highpass.runspot.session.domain.dao.SessionRepository;
import com.highpass.runspot.session.exception.SessionErrorCode;
import com.highpass.runspot.session.exception.SessionException;
import com.highpass.runspot.session.service.dto.request.PositionBasedSearchCondition;
import com.highpass.runspot.session.service.dto.request.RangeBasedMarkerSearchCondition;
import com.highpass.runspot.session.service.dto.response.SessionInfoDetailResponse;
import com.highpass.runspot.session.service.dto.response.SessionInfoSummaryResponse;
import com.highpass.runspot.session.service.dto.response.SessionMarkerSearchResponse;
import com.highpass.runspot.session.service.dto.response.SessionNearbySearchResponse;
import com.highpass.runspot.session.service.dto.response.SessionSearchResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SessionQueryService {

    private static final double SEARCH_RADIUS_KM = 3.0;
    private static final double METERS_IN_KM = 1000.0;

    private final SessionRepository sessionRepository;
    private final SessionParticipantRepository sessionParticipantRepository;

    public Slice<SessionSearchResponse> searchSessionByName(final String query, final Long cursorId, final int size) {
        validateQuery(query);

        // 다음 페이지 여부 확인을 위해 size + 1개 조회
        Pageable pageable = PageRequest.of(0, size);
        // Repository에는 limit을 size + 1로 요청하기 위해 별도 Pageable 생성 혹은 limit 파라미터 사용
        // 여기서는 Repository가 Pageable.pageSize를 사용한다고 가정하고 size+1을 전달
        Pageable executionPageable = PageRequest.of(0, size + 1);
        
        List<Session> findSessions;

        if (cursorId == null) {
            findSessions = sessionRepository.findByStatusAndTitleContainingOrderByIdDesc(OPEN, query, executionPageable);
        } else {
            findSessions = sessionRepository.findByStatusAndTitleContainingAndIdLessThanOrderByIdDesc(OPEN, query, cursorId, executionPageable);
        }

        boolean hasNext = false;
        if (findSessions.size() > size) {
            hasNext = true;
            findSessions.remove(findSessions.size() - 1);
        }

        List<SessionSearchResponse> responses = findSessions.stream()
                .map(SessionSearchResponse::from)
                .toList();

        return new SliceImpl<>(responses, pageable, hasNext);
    }

    public SessionInfoSummaryResponse getSessionSummary(final Long sessionId) {
        final Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new SessionException(SessionErrorCode.SESSION_NOT_FOUND));
        return SessionInfoSummaryResponse.from(session);
    }

    public SessionInfoDetailResponse getSessionDetail(final Long sessionId) {
        final Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new SessionException(SessionErrorCode.SESSION_NOT_FOUND));

        final List<SessionParticipant> participants = sessionParticipantRepository
                .findBySessionIdAndStatusWithUser(sessionId, ParticipationStatus.APPROVED);

        return SessionInfoDetailResponse.from(session, participants);
    }

    public List<SessionNearbySearchResponse> getSessionsNearby(final @Valid PositionBasedSearchCondition condition, final int limit) {
        final Point currentPoint = GeometryUtil.createPoint(condition.y(), condition.x());
        final Geometry searchArea = GeometryUtil.createBoundingBox(condition.y(), condition.x(), SEARCH_RADIUS_KM);
        
        final List<Session> sessions = sessionRepository.findNearestSessions(currentPoint, searchArea, PageRequest.of(0, limit));

        return sessions.stream()
                .map(session -> {
                    // note: 해당 쿼리에서 N+1발생 가능. 하지만 현재는 쿼리가 적게 나가므로 그대로 진행함.
                    final long applicants = sessionParticipantRepository.countBySessionIdAndStatus(session.getId(), ParticipationStatus.APPROVED);
                    final double distanceMeter = GeometryUtil.calculateDistance(
                            condition.y().doubleValue(), condition.x().doubleValue(),
                            session.getLocation().getY(), session.getLocation().getX()
                    );
                    
                    return new SessionNearbySearchResponse(
                            session.getId(),
                            session.getTitle(),
                            (int) applicants,
                            session.getCapacity(),
                            session.getLocationName(),
                            BigDecimal.valueOf(distanceMeter / METERS_IN_KM).setScale(1, RoundingMode.HALF_UP),
                            session.getTargetDistanceKm(),
                            session.getAvgPaceSec(),
                            session.getStartAt()
                    );
                })
                .toList();
    }

    public List<SessionMarkerSearchResponse> getSessionsMarker(final @Valid RangeBasedMarkerSearchCondition condition) {
        final Polygon area = GeometryUtil.createPolygon(
                condition.leftX(), condition.rightY(),
                condition.rightX(), condition.leftY()
        );

        final List<Session> sessions = sessionRepository.findWithinArea(area);

        return sessions.stream()
                .map(session -> new SessionMarkerSearchResponse(
                        session.getId(),
                        session.getTitle(),
                        BigDecimal.valueOf(session.getLocation().getX()),
                        BigDecimal.valueOf(session.getLocation().getY())
                ))
                .toList();
    }

    private void validateQuery(final String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new SessionException(SessionErrorCode.INVALID_SEARCH_QUERY);
        }
    }
}
