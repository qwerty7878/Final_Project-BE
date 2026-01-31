package com.highpass.runspot.session.controller;

import com.highpass.runspot.session.dto.SessionCreateRequest;
import com.highpass.runspot.session.dto.SessionResponse;
import com.highpass.runspot.session.service.SessionService;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sessions")
public class SessionController {

    private final SessionService sessionService;

    @PostMapping
    public ResponseEntity<SessionResponse> createSession(
        @Valid @RequestBody SessionCreateRequest request,
        @SessionAttribute(name = "loginUserId", required = false) Long loginUserId
    ) {
        if (loginUserId == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        SessionResponse response = sessionService.createSession(loginUserId, request);

        return ResponseEntity.created(URI.create("/sessions/" + response.id()))
            .body(response);
    }
}
