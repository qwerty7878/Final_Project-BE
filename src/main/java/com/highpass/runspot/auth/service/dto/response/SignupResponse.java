package com.highpass.runspot.auth.service.dto.response;

import com.highpass.runspot.auth.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SignupResponse {

    private Long userId;

    public static SignupResponse from(User user) {
        return SignupResponse.builder()
                .userId(user.getId())
                .build();
    }
}