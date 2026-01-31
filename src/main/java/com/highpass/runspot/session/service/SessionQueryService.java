package com.highpass.runspot.session.service;

import com.highpass.runspot.session.domain.Session;
import com.highpass.runspot.session.domain.dao.SessionRepository;
import com.highpass.runspot.session.service.dto.response.SessionSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionQueryService {

    private final SessionRepository sessionRepository;

    public List<SessionSearchResponse> searchSessionByName(final String query) {
        final List<Session> findSessions = sessionRepository.findByTitleContaining(query);
        return findSessions.stream()
                .map(SessionSearchResponse::from)
                .toList();
    }
}
