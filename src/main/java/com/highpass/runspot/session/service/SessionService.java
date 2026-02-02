package com.highpass.runspot.session.service;

import com.highpass.runspot.auth.domain.Gender;
import com.highpass.runspot.auth.domain.User;
import com.highpass.runspot.auth.domain.dao.UserRepository;
import com.highpass.runspot.auth.service.UserStatsService;
import com.highpass.runspot.session.domain.AttendanceStatus;
import com.highpass.runspot.session.domain.GenderPolicy;
import com.highpass.runspot.session.domain.ParticipationStatus;
import com.highpass.runspot.session.domain.Session;
import com.highpass.runspot.session.domain.SessionParticipant;
import com.highpass.runspot.session.domain.SessionStatus;
import com.highpass.runspot.session.service.dto.request.AttendanceUpdateRequest;
import com.highpass.runspot.session.service.dto.request.SessionCreateRequest;
import com.highpass.runspot.session.service.dto.request.SessionJoinRequest;
import com.highpass.runspot.session.service.dto.response.SessionParticipantResponse;
import com.highpass.runspot.session.service.dto.response.SessionResponse;
import com.highpass.runspot.session.domain.dao.SessionParticipantRepository;
import com.highpass.runspot.session.domain.dao.SessionRepository;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SessionService {

    private final SessionRepository sessionRepository;
    private final SessionParticipantRepository sessionParticipantRepository;
    private final UserRepository userRepository;
    private final UserStatsService userStatsService;

    @Transactional
    public SessionResponse createSession(Long userId, SessionCreateRequest request) {
        User hostUser = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));

        Session session = request.toEntity(hostUser);
        Session savedSession = sessionRepository.save(session);

        return SessionResponse.from(savedSession);
    }

    @Transactional
    public void closeSession(Long userId, Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new IllegalArgumentException("세션을 찾을 수 없습니다. ID: " + sessionId));

        session.close(userId);
    }

    @Transactional
    public void finishSession(Long userId, Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new IllegalArgumentException("세션을 찾을 수 없습니다. ID: " + sessionId));

        session.finish(userId);
    }

    @Transactional
    public void joinSession(Long userId, Long sessionId, SessionJoinRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));

        Session session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new IllegalArgumentException("세션을 찾을 수 없습니다. ID: " + sessionId));

        // 호스트 본인 신청 불가
        if (session.getHostUser().getId().equals(userId)) {
            throw new IllegalArgumentException("호스트는 참여 신청을 할 수 없습니다.");
        }

        // 중복 신청 불가
        if (sessionParticipantRepository.existsBySessionAndUser(session, user)) {
            throw new IllegalArgumentException("이미 신청한 세션입니다.");
        }

        // 세션 상태 체크 (OPEN만 가능)
        if (session.getStatus() != SessionStatus.OPEN) {
            throw new IllegalStateException("모집 중인 세션이 아닙니다.");
        }

        // 성별 정책 체크
        validateGenderPolicy(session.getGenderPolicy(), user.getGender());

        // 인원 제한 체크 (승인된 인원 기준)
        long approvedCount = sessionParticipantRepository.countBySessionIdAndStatus(sessionId, ParticipationStatus.APPROVED);
        if (approvedCount >= session.getCapacity()) {
            throw new IllegalStateException("모집 인원이 마감되었습니다.");
        }

        // 신청 저장
        SessionParticipant participant = SessionParticipant.builder()
            .session(session)
            .user(user)
            .messageToHost(request.messageToHost())
            .build(); // status 기본값 REQUESTED, attendanceStatus 기본값 ABSENT

        sessionParticipantRepository.save(participant);
    }

    public List<SessionParticipantResponse> getJoinRequests(Long userId, Long sessionId, ParticipationStatus status) {
        Session session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new IllegalArgumentException("세션을 찾을 수 없습니다. ID: " + sessionId));

        // 호스트 검증
        if (!Objects.equals(session.getHostUser().getId(), userId)) {
            throw new IllegalStateException("호스트만 신청 목록을 조회할 수 있습니다.");
        }

        List<SessionParticipant> participants;
        if (status == null) {
            participants = sessionParticipantRepository.findBySessionId(sessionId);
        } else {
            participants = sessionParticipantRepository.findBySessionIdAndStatus(sessionId, status);
        }

        return participants.stream()
            .map(SessionParticipantResponse::from)
            .toList();
    }

    @Transactional
    public void approveJoinRequest(Long userId, Long sessionId, Long participationId) {
        Session session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new IllegalArgumentException("세션을 찾을 수 없습니다. ID: " + sessionId));

        // 호스트 검증
        if (!Objects.equals(session.getHostUser().getId(), userId)) {
            throw new IllegalStateException("호스트만 승인할 수 있습니다.");
        }

        SessionParticipant participant = sessionParticipantRepository.findById(participationId)
            .orElseThrow(() -> new IllegalArgumentException("신청 정보를 찾을 수 없습니다."));

        // 인원 마감 체크 (승인 시점에 다시 한번 체크)
        long approvedCount = sessionParticipantRepository.countBySessionIdAndStatus(sessionId, ParticipationStatus.APPROVED);
        if (approvedCount >= session.getCapacity()) {
            throw new IllegalStateException("모집 인원이 마감되어 승인할 수 없습니다.");
        }

        participant.approve();
    }

    @Transactional
    public void rejectJoinRequest(Long userId, Long sessionId, Long participationId) {
        Session session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new IllegalArgumentException("세션을 찾을 수 없습니다. ID: " + sessionId));

        // 호스트 검증
        if (!Objects.equals(session.getHostUser().getId(), userId)) {
            throw new IllegalStateException("호스트만 거절할 수 있습니다.");
        }

        SessionParticipant participant = sessionParticipantRepository.findById(participationId)
            .orElseThrow(() -> new IllegalArgumentException("신청 정보를 찾을 수 없습니다."));

        participant.reject();
    }

    public List<SessionParticipantResponse> getAttendanceList(Long userId, Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new IllegalArgumentException("세션을 찾을 수 없습니다. ID: " + sessionId));

        // 일단 호스트만 보는 것으로 구현
        if (!Objects.equals(session.getHostUser().getId(), userId)) {
            throw new IllegalStateException("호스트만 출석부를 조회할 수 있습니다.");
        }

        // 승인된 참여자만 조회
        List<SessionParticipant> participants = sessionParticipantRepository.findBySessionIdAndStatus(sessionId, ParticipationStatus.APPROVED);

        return participants.stream()
            .map(SessionParticipantResponse::from)
            .toList();
    }

    @Transactional
    public void updateAttendance(Long userId, Long sessionId, Long participationId, AttendanceUpdateRequest request) {
        Session session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new IllegalArgumentException("세션을 찾을 수 없습니다. ID: " + sessionId));

        // 호스트 검증
        if (!Objects.equals(session.getHostUser().getId(), userId)) {
            throw new IllegalStateException("호스트만 출석 체크를 할 수 있습니다.");
        }

        SessionParticipant participant = sessionParticipantRepository.findById(participationId)
            .orElseThrow(() -> new IllegalArgumentException("참여 정보를 찾을 수 없습니다."));

        // 해당 세션의 참여자인지 확인
        if (!participant.getSession().getId().equals(sessionId)) {
            throw new IllegalArgumentException("해당 세션의 참여자가 아닙니다.");
        }

        // 기존 출석 상태 확인
        AttendanceStatus previousStatus = participant.getAttendanceStatus();

        participant.updateAttendance(request.attendanceStatus());

        if (previousStatus != AttendanceStatus.ATTENDED
                && request.attendanceStatus() == AttendanceStatus.ATTENDED) {
            userStatsService.updateRunningStatsOnAttendance(
                    participant.getUser().getId(),
                    session.getTargetDistanceKm()
            );
        }
    }

    private void validateGenderPolicy(GenderPolicy policy, Gender userGender) {
        if (policy == GenderPolicy.MALE_ONLY && userGender != Gender.MALE) {
            throw new IllegalArgumentException("남성만 참여 가능한 세션입니다.");
        }
        if (policy == GenderPolicy.FEMALE_ONLY && userGender != Gender.FEMALE) {
            throw new IllegalArgumentException("여성만 참여 가능한 세션입니다.");
        }
    }
}
