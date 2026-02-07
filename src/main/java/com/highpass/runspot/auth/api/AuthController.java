package com.highpass.runspot.auth.api;

import com.highpass.runspot.auth.service.dto.request.LoginRequest;
import com.highpass.runspot.auth.service.dto.response.LoginResponse;
import com.highpass.runspot.auth.service.dto.request.SignupRequest;
import com.highpass.runspot.auth.service.dto.response.SignupResponse;
import com.highpass.runspot.auth.service.AuthService;
import com.highpass.runspot.auth.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입", description = "회원가입 기능입니다.")
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {
        try {
            log.info("=== Signup Request Start ===");
            log.info("Username: {}", request.getUsername());
            log.info("Name: {}", request.getName());
            log.info("AgeGroup: {}", request.getAgeGroup());
            log.info("Gender: {}", request.getGender());
            log.info("WeeklyRuns: {}", request.getWeeklyRuns());
            log.info("AvgPaceMinPerKm: {}", request.getAvgPaceMinPerKm());

            User user = authService.signup(request);
            log.info("User created successfully: {}", user.getId());

            SignupResponse response = SignupResponse.from(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            log.error("Signup failed", e);

            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getClass().getSimpleName());
            error.put("message", e.getMessage());

            // 스택트레이스 추가
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            error.put("stackTrace", sw.toString());

            if (e.getCause() != null) {
                error.put("cause", e.getCause().toString());
            }

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @Operation(summary = "로그인", description = "로그인 기능입니다.")
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest request,
            HttpSession session) {
        try {
            User user = authService.login(request, session);
            LoginResponse response = LoginResponse.from(user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Login failed", e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getClass().getSimpleName());
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @Operation(summary = "로그아웃", description = "로그인 된 계정의 세션을 만료합니다.")
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpSession session) {
        authService.logout(session);
        Map<String, String> response = new HashMap<>();
        response.put("message", "로그아웃 성공");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "회원탈퇴", description = "현재 로그인 된 세션을 기반으로 회원탈퇴합니다.")
    @DeleteMapping("/withdraw")
    public ResponseEntity<Map<String, String>> withdraw(HttpSession session) {
        authService.withdraw(session);
        Map<String, String> response = new HashMap<>();
        response.put("message", "회원 탈퇴 처리됨");
        return ResponseEntity.ok(response);
    }
}