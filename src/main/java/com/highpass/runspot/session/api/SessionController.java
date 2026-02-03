package com.highpass.runspot.session.api;

import com.highpass.runspot.session.domain.ParticipationStatus;
import com.highpass.runspot.session.service.SessionQueryService;
import com.highpass.runspot.session.service.SessionService;
import com.highpass.runspot.session.service.dto.request.AttendanceUpdateRequest;
import com.highpass.runspot.session.service.dto.request.SessionCreateRequest;
import com.highpass.runspot.session.service.dto.request.SessionJoinRequest;
import com.highpass.runspot.session.service.dto.response.SessionParticipantResponse;
import com.highpass.runspot.session.service.dto.response.SessionResponse;
import com.highpass.runspot.session.service.dto.response.SessionSearchResponse;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import org.springframework.web.bind.annotation.SessionAttribute;

@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;
    private final SessionQueryService sessionQueryService;

    @GetMapping("/search")
    public ResponseEntity<Slice<SessionSearchResponse>> searchSessionByName(
            @RequestParam("q") final String query,
            @RequestParam(name = "cursorId", required = false) final Long cursorId,
            @RequestParam(name = "size", required = false, defaultValue = "10") final int size
    ) {
        final Slice<SessionSearchResponse> searchResponses = sessionQueryService.searchSessionByName(query,
                cursorId, size);
        return ResponseEntity.ok(searchResponses);
    }

    @PostMapping
    public ResponseEntity<SessionResponse> createSession(
        @Valid @RequestBody SessionCreateRequest request,
        @SessionAttribute(name = "userId", required = false) Long loginUserId
    ) {
        if (loginUserId == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        SessionResponse response = sessionService.createSession(loginUserId, request);

        return ResponseEntity.created(URI.create("/sessions/" + response.id()))
            .body(response);
    }

    @PostMapping("/{sessionId}/close")
    public ResponseEntity<Void> closeSession(
        @PathVariable Long sessionId,
        @SessionAttribute(name = "userId", required = false) Long loginUserId
    ) {
        if (loginUserId == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        sessionService.closeSession(loginUserId, sessionId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{sessionId}/finish")
    public ResponseEntity<Void> finishSession(
        @PathVariable Long sessionId,
        @SessionAttribute(name = "userId", required = false) Long loginUserId
    ) {
        if (loginUserId == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        sessionService.finishSession(loginUserId, sessionId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{sessionId}/join")
    public ResponseEntity<Void> joinSession(
        @PathVariable Long sessionId,
        @Valid @RequestBody SessionJoinRequest request,
        @SessionAttribute(name = "userId", required = false) Long loginUserId
    ) {
        if (loginUserId == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        sessionService.joinSession(loginUserId, sessionId, request);

        return ResponseEntity.created(URI.create("/sessions/" + sessionId + "/join-requests")).build();
    }

    @GetMapping("/{sessionId}/join-requests")
    public ResponseEntity<List<SessionParticipantResponse>> getJoinRequests(
        @PathVariable Long sessionId,
        @RequestParam(required = false) ParticipationStatus status,
        @SessionAttribute(name = "userId", required = false) Long loginUserId
    ) {
        if (loginUserId == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        List<SessionParticipantResponse> responses = sessionService.getJoinRequests(loginUserId, sessionId, status);

        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{sessionId}/join-requests/{participationId}/approve")
    public ResponseEntity<Void> approveJoinRequest(
        @PathVariable Long sessionId,
        @PathVariable Long participationId,
        @SessionAttribute(name = "userId", required = false) Long loginUserId
    ) {
        if (loginUserId == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        sessionService.approveJoinRequest(loginUserId, sessionId, participationId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{sessionId}/join-requests/{participationId}/reject")
    public ResponseEntity<Void> rejectJoinRequest(
        @PathVariable Long sessionId,
        @PathVariable Long participationId,
        @SessionAttribute(name = "userId", required = false) Long loginUserId
    ) {
        if (loginUserId == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        sessionService.rejectJoinRequest(loginUserId, sessionId, participationId);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{sessionId}/attendance")
    public ResponseEntity<List<SessionParticipantResponse>> getAttendanceList(
        @PathVariable Long sessionId,
        @SessionAttribute(name = "userId", required = false) Long loginUserId
    ) {
        if (loginUserId == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        List<SessionParticipantResponse> responses = sessionService.getAttendanceList(loginUserId, sessionId);

        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/{sessionId}/participants/{participationId}/attendance")
    public ResponseEntity<Void> updateAttendance(
        @PathVariable Long sessionId,
        @PathVariable Long participationId,
        @Valid @RequestBody AttendanceUpdateRequest request,
        @SessionAttribute(name = "userId", required = false) Long loginUserId
    ) {
        if (loginUserId == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        sessionService.updateAttendance(loginUserId, sessionId, participationId, request);

        return ResponseEntity.ok().build();
    }
}
