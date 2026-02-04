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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입", description = "회원가입 기능입니다.")
    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest request) {
        User user = authService.signup(request);
        SignupResponse response = SignupResponse.from(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "로그인", description = "로그인 기능입니다.")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpSession session) {
        User user = authService.login(request, session);
        LoginResponse response = LoginResponse.from(user);
        return ResponseEntity.ok(response);
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