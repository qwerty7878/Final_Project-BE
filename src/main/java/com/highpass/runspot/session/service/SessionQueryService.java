package com.highpass.runspot.session.service;

import static com.highpass.runspot.session.domain.SessionStatus.OPEN;

import com.highpass.runspot.session.domain.Session;
import com.highpass.runspot.session.domain.SessionStatus;
import com.highpass.runspot.session.domain.dao.SessionRepository;
import com.highpass.runspot.session.exception.SessionErrorCode;
import com.highpass.runspot.session.exception.SessionException;
import com.highpass.runspot.session.service.dto.response.SessionSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SessionQueryService {

    private final SessionRepository sessionRepository;

    public Slice<SessionSearchResponse> searchSessionByName(final String query, final Long cursorId, final int size) {
        validateQuery(query);

        // 다음 페이지 여부 확인을 위해 size + 1개 조회
        Pageable pageable = PageRequest.of(0, size);
        // Repository에는 limit을 size + 1로 요청하기 위해 별도 Pageable 생성 혹은 limit 파라미터 사용
        // 여기서는 Repository가 Pageable.pageSize를 사용한다고 가정하고 size+1을 전달
        Pageable executionPageable = PageRequest.of(0, size + 1);
        
        List<Session> findSessions;

        if (cursorId == null) {
            findSessions = sessionRepository.findByStatusAndTitleContainingOrderByIdDesc(OPEN, query, executionPageable);
        } else {
            findSessions = sessionRepository.findByStatusAndTitleContainingAndIdLessThanOrderByIdDesc(OPEN, query, cursorId, executionPageable);
        }

        boolean hasNext = false;
        if (findSessions.size() > size) {
            hasNext = true;
            findSessions.remove(findSessions.size() - 1);
        }

        List<SessionSearchResponse> responses = findSessions.stream()
                .map(SessionSearchResponse::from)
                .toList();

        return new SliceImpl<>(responses, pageable, hasNext);
    }

    private void validateQuery(final String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new SessionException(SessionErrorCode.INVALID_SEARCH_QUERY);
        }
    }
}
