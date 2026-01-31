package com.highpass.runspot.session.api;

import com.highpass.runspot.session.service.SessionQueryService;
import com.highpass.runspot.session.service.dto.response.SessionSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionQueryService sessionQueryService;

    @GetMapping
    public ResponseEntity<List<SessionSearchResponse>> searchSessionByName(@RequestParam("q") final String query) {
        final List<SessionSearchResponse> searchResponses = sessionQueryService.searchSessionByName(query);
        return ResponseEntity.ok(searchResponses);
    }
}
