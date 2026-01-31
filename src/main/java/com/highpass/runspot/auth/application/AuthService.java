package com.highpass.runspot.auth.application;

import com.highpass.runspot.auth.api.dto.LoginRequest;
import com.highpass.runspot.auth.api.dto.SignupRequest;
import com.highpass.runspot.auth.domain.User;
import com.highpass.runspot.auth.domain.dao.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;

    private static final String SESSION_USER_KEY = "userId";

    @Transactional
    public User signup(SignupRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다");
        }

        User.UserBuilder userBuilder = User.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .name(request.getName())
                .ageGroup(request.getAgeGroup())
                .gender(request.getGender())
                .mannerTemp(new BigDecimal("36.5"));

        if (request.getWeeklyRuns() != null) {
            userBuilder.weeklyRunningGoal(request.getWeeklyRuns());
        } else {
            userBuilder.weeklyRunningGoal(3);
        }

        if (request.getAvgPaceMinPerKm() != null) {
            userBuilder.pacePreferenceSec(request.getAvgPaceInSeconds());
        } else {
            userBuilder.pacePreferenceSec(360);
        }

        User user = userBuilder.build();
        return userRepository.save(user);
    }

    @Transactional
    public User login(LoginRequest request, HttpSession session) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 일치하지 않습니다");
        }

        session.setAttribute(SESSION_USER_KEY, user.getId());
        return user;
    }

    public void logout(HttpSession session) {
        session.invalidate();
    }

    @Transactional
    public void withdraw(HttpSession session) {
        Long userId = (Long) session.getAttribute(SESSION_USER_KEY);

        if (userId == null) {
            throw new IllegalArgumentException("로그인이 필요합니다");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        userRepository.delete(user);
        session.invalidate();
    }
}