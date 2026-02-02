package com.highpass.runspot.session.service;

import static com.highpass.runspot.session.domain.SessionStatus.OPEN;

import com.highpass.runspot.session.domain.Session;
import com.highpass.runspot.session.domain.SessionStatus;
import com.highpass.runspot.session.domain.dao.SessionRepository;
import com.highpass.runspot.session.exception.SessionErrorCode;
import com.highpass.runspot.session.exception.SessionException;
import com.highpass.runspot.session.service.dto.response.SessionSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SessionQueryService {

    private final SessionRepository sessionRepository;

    public List<SessionSearchResponse> searchSessionByName(final String query) {
        validateQuery(query);
        final List<Session> findSessions = sessionRepository.findByStatusAndTitleContainingOrderByIdDesc(OPEN, query);
        return findSessions.stream()
                .map(SessionSearchResponse::from)
                .toList();
    }

    private void validateQuery(final String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new SessionException(SessionErrorCode.INVALID_SEARCH_QUERY);
        }
    }
}
