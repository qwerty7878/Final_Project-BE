package com.highpass.runspot.session.api;

import com.highpass.runspot.common.dto.SliceResponse;
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

    @GetMapping("/search")
    public ResponseEntity<SliceResponse<SessionSearchResponse>> searchSessionByName(
            @RequestParam("q") final String query,
            @RequestParam(name = "cursorId", required = false) final Long cursorId,
            @RequestParam(name = "size", required = false, defaultValue = "10") final int size
    ) {
        final SliceResponse<SessionSearchResponse> searchResponses = sessionQueryService.searchSessionByName(query,
                cursorId, size);
        return ResponseEntity.ok(searchResponses);
    }
}
