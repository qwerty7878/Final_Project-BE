package com.highpass.runspot.session.service;

import com.highpass.runspot.session.domain.dao.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SessionQueryService {

    private final SessionRepository sessionRepository;


}
