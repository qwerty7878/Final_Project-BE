package com.highpass.runspot.auth.api;

import com.highpass.runspot.auth.api.dto.LoginRequest;
import com.highpass.runspot.auth.api.dto.LoginResponse;
import com.highpass.runspot.auth.api.dto.SignupRequest;
import com.highpass.runspot.auth.api.dto.SignupResponse;
import com.highpass.runspot.auth.application.AuthService;
import com.highpass.runspot.auth.domain.User;
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

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest request) {
        User user = authService.signup(request);
        SignupResponse response = SignupResponse.from(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpSession session) {
        User user = authService.login(request, session);
        LoginResponse response = LoginResponse.from(user);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpSession session) {
        authService.logout(session);
        Map<String, String> response = new HashMap<>();
        response.put("message", "로그아웃 성공");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<Map<String, String>> withdraw(HttpSession session) {
        authService.withdraw(session);
        Map<String, String> response = new HashMap<>();
        response.put("message", "회원 탈퇴 처리됨");
        return ResponseEntity.ok(response);
    }
}