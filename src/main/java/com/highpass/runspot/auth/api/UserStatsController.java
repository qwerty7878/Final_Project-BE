package com.highpass.runspot.auth.api;

import static com.highpass.runspot.auth.service.AuthService.SESSION_USER_KEY;

import com.highpass.runspot.auth.service.UserStatsService;
import com.highpass.runspot.auth.service.dto.response.AppliedRunningsResponse;
import com.highpass.runspot.auth.service.dto.response.MyCreatedRunningsResponse;
import com.highpass.runspot.auth.service.dto.response.RecentRunningsResponse;
import com.highpass.runspot.auth.service.dto.response.UserProfileResponse;
import com.highpass.runspot.session.service.SessionService;
import com.highpass.runspot.session.service.dto.response.SessionResponse;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserStatsController {

    private final UserStatsService userStatsService;
    private final SessionService sessionService;

    @Operation(summary = "내 정보 조회", description = "상단의 내 정보를 기반으로 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile(
            @SessionAttribute(name = SESSION_USER_KEY, required = false) Long userId
    ) {
        if (userId == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        UserProfileResponse response = userStatsService.getMyProfile(userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "신청한 러닝 목록 조회 (게스트)", description = "게스트가 참여 신청한 러닝 세션 내역 중 3개를 최신순으로 조회합니다.")
    @GetMapping("/me/runnings/applied")
    public ResponseEntity<AppliedRunningsResponse> getAppliedRunnings(
            @RequestParam(required = false, defaultValue = "3") Integer limit,
            @SessionAttribute(name = SESSION_USER_KEY, required = false) Long userId
    ) {
        if (userId == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        AppliedRunningsResponse response = userStatsService.getAppliedRunnings(userId, limit);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "최근 참여 내역 조회", description = "출석한 러닝 세션 내역 중 3개를 최신순으로 조회합니다.")
    @GetMapping("/me/runnings/recent")
    public ResponseEntity<RecentRunningsResponse> getRecentRunnings(
            @RequestParam(required = false, defaultValue = "3") Integer limit,
            @SessionAttribute(name = SESSION_USER_KEY, required = false) Long userId
    ) {
        if (userId == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        RecentRunningsResponse response = userStatsService.getRecentRunnings(userId, limit);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "내가 개설한 러닝 조회(호스트)", description = "내가 개설한 러닝 세션 내역 중 3개를 최신순으로 조회합니다.")
    @GetMapping("/me/runnings/mySession")
    public ResponseEntity<List<MyCreatedRunningsResponse>> getMyHostedSessions(
            @SessionAttribute(name = SESSION_USER_KEY, required = false) Long userId
    ) {
        if (userId == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        List<MyCreatedRunningsResponse> responses = sessionService.getMyHostedSessions(userId);

        return ResponseEntity.ok(responses);
    }
}