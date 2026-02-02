package com.highpass.runspot.session.service;

import static com.highpass.runspot.session.domain.SessionStatus.OPEN;

import com.highpass.runspot.session.domain.Session;
import com.highpass.runspot.session.domain.SessionStatus;
import com.highpass.runspot.session.domain.dao.SessionRepository;
import com.highpass.runspot.session.exception.SessionErrorCode;
import com.highpass.runspot.common.dto.SliceResponse;
import com.highpass.runspot.session.exception.SessionException;
import com.highpass.runspot.session.service.dto.response.SessionSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SessionQueryService {

    private final SessionRepository sessionRepository;

    public SliceResponse<SessionSearchResponse> searchSessionByName(final String query, final Long cursorId, final int size) {
        validateQuery(query);

        // 다음 페이지 여부 확인을 위해 size + 1개 조회
        Pageable pageable = PageRequest.of(0, size + 1);
        List<Session> findSessions;

        if (cursorId == null) {
            findSessions = sessionRepository.findByStatusAndTitleContainingOrderByIdDesc(OPEN, query, pageable);
        } else {
            findSessions = sessionRepository.findByStatusAndTitleContainingAndIdLessThanOrderByIdDesc(OPEN, query, cursorId, pageable);
        }

        boolean hasNext = false;
        if (findSessions.size() > size) {
            hasNext = true;
            findSessions.remove(findSessions.size() - 1);
        }

        Long nextCursorId = null;
        if (hasNext && !findSessions.isEmpty()) {
            nextCursorId = findSessions.get(findSessions.size() - 1).getId();
        }

        final List<SessionSearchResponse> responses = findSessions.stream()
                .map(SessionSearchResponse::from)
                .toList();

        return SliceResponse.of(responses, nextCursorId, hasNext);
    }

    private void validateQuery(final String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new SessionException(SessionErrorCode.INVALID_SEARCH_QUERY);
        }
    }
}
