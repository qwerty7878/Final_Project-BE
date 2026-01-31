package com.highpass.runspot.session.service;

import com.highpass.runspot.auth.domain.User;
import com.highpass.runspot.auth.repository.UserRepository;
import com.highpass.runspot.session.domain.Session;
import com.highpass.runspot.session.dto.SessionCreateRequest;
import com.highpass.runspot.session.dto.SessionResponse;
import com.highpass.runspot.session.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SessionService {

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;

    @Transactional
    public SessionResponse createSession(Long userId, SessionCreateRequest request) {
        User hostUser = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));

        Session session = request.toEntity(hostUser);
        Session savedSession = sessionRepository.save(session);

        return SessionResponse.from(savedSession);
    }
}
