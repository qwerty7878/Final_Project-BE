package com.highpass.runspot.common;

import com.highpass.runspot.auth.domain.AgeGroup;
import com.highpass.runspot.auth.domain.Gender;
import com.highpass.runspot.auth.domain.User;
import com.highpass.runspot.auth.domain.UserRunningStats;
import com.highpass.runspot.auth.domain.dao.UserRepository;
import com.highpass.runspot.auth.domain.dao.UserRunningStatsRepository;
import com.highpass.runspot.session.domain.GenderPolicy;
import com.highpass.runspot.session.domain.RunType;
import com.highpass.runspot.session.domain.Session;
import com.highpass.runspot.session.domain.SessionParticipant;
import com.highpass.runspot.session.domain.ParticipationStatus;
import com.highpass.runspot.session.domain.AttendanceStatus;
import com.highpass.runspot.session.domain.dao.SessionParticipantRepository;
import com.highpass.runspot.session.domain.dao.SessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
// @Profile("!prod") 제거: 서버 환경에서도 데이터가 없으면 실행되도록 변경
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final SessionParticipantRepository sessionParticipantRepository;
    private final UserRunningStatsRepository userRunningStatsRepository;
    // PasswordEncoder 제거 (AuthService 평문 비교 로직에 맞춤)

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
    private final Random random = new Random();

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            log.info("이미 데이터가 존재하여 더미 데이터 생성을 건너뜁니다.");
            return;
        }

        log.info("더미 데이터 생성을 시작합니다...");

        // 1. 유저 생성
        List<User> users = createUsers();

        // 2. 세션 및 참여자 생성 (여의도 집중)
        createSessions(users);

        log.info("더미 데이터 생성이 완료되었습니다.");
    }

    private List<User> createUsers() {
        List<User> users = new ArrayList<>();
        // 비밀번호 변경: 영문+숫자 포함 8자리 이상
        String password = "password1234";

        // Admin User
        User admin = User.builder()
                .username("admin")
                .password(password)
                .name("관리자")
                .ageGroup(AgeGroup.THIRTIES)
                .gender(Gender.MALE)
                .weeklyRunningGoal(5)
                .pacePreferenceSec(300)
                .build();
        users.add(userRepository.save(admin));
        userRunningStatsRepository.save(UserRunningStats.builder().user(admin).build());

        // Normal Users
        for (int i = 1; i <= 15; i++) {
            Gender gender = (i % 2 == 0) ? Gender.FEMALE : Gender.MALE;
            AgeGroup ageGroup = (i % 3 == 0) ? AgeGroup.TWENTIES : AgeGroup.THIRTIES;
            
            User user = User.builder()
                    .username("user" + i)
                    .password(password)
                    .name("러너" + i)
                    .ageGroup(ageGroup)
                    .gender(gender)
                    .weeklyRunningGoal(random.nextInt(5) + 1)
                    .pacePreferenceSec(300 + random.nextInt(180)) // 5분 ~ 8분 페이스
                    .build();
            
            users.add(userRepository.save(user));
            userRunningStatsRepository.save(UserRunningStats.builder().user(user).build());
        }
        return users;
    }

    private void createSessions(List<User> users) {
        // 여의도 한강공원 중심 좌표 (여의나루역 인근)
        double centerX = 126.9347;
        double centerY = 37.5284;

        String[] titles = {
                "여의도 한강 야간 러닝", "여의나루역 집결 5km", "주말 아침 여의도 공원", 
                "초보자 환영 천천히 뜁니다", "여의도 인터벌 훈련", "퇴근 후 여의도 스트레스 해소",
                "63빌딩 앞 집결", "국회의사당 한바퀴", "마포대교 건너기", "샛강생태공원 러닝"
        };

        for (int i = 0; i < 20; i++) {
            User host = users.get(random.nextInt(users.size()));
            
            // 여의도 중심 반경 약 1km 내 랜덤 분포
            // 0.01도 ≈ 1.1km
            double startX = centerX + (random.nextDouble() - 0.5) * 0.015;
            double startY = centerY + (random.nextDouble() - 0.5) * 0.010;
            
            Point location = geometryFactory.createPoint(new Coordinate(startX, startY));

            // 경로 생성 (시작점 주변을 도는 폴리라인)
            List<Session.RoutePoint> route = createDummyRoute(startX, startY);

            Session session = Session.builder()
                    .hostUser(host)
                    .title(titles[random.nextInt(titles.length)])
                    .runType(RunType.values()[random.nextInt(RunType.values().length)])
                    .locationName("여의도 한강공원")
                    .location(location)
                    .routePolyline(route)
                    .targetDistanceKm(BigDecimal.valueOf(3 + random.nextInt(7))) // 3~10km
                    .avgPaceSec(300 + random.nextInt(180))
                    .startAt(LocalDateTime.now().plusDays(random.nextInt(7)).plusHours(random.nextInt(12)))
                    .capacity(3 + random.nextInt(8)) // 3~10명
                    .genderPolicy(GenderPolicy.MIXED)
                    .build();

            sessionRepository.save(session);

            // 호스트 외 다른 참가자 추가
            addParticipants(session, users, host);
        }
    }

    private List<Session.RoutePoint> createDummyRoute(double startX, double startY) {
        List<Session.RoutePoint> route = new ArrayList<>();
        double currentX = startX;
        double currentY = startY;
        
        route.add(new Session.RoutePoint(BigDecimal.valueOf(currentX), BigDecimal.valueOf(currentY)));

        // 5~8개의 점을 이어 경로 생성
        for (int i = 0; i < 5 + random.nextInt(4); i++) {
            // 0.002도 약 200m 이동
            currentX += (random.nextDouble() - 0.5) * 0.004;
            currentY += (random.nextDouble() - 0.5) * 0.004;
            route.add(new Session.RoutePoint(BigDecimal.valueOf(currentX), BigDecimal.valueOf(currentY)));
        }
        return route;
    }

    private void addParticipants(Session session, List<User> users, User host) {
        int participantCount = random.nextInt(session.getCapacity()); // 0 ~ 정원 미만
        
        for (int i = 0; i < participantCount; i++) {
            User user = users.get(random.nextInt(users.size()));
            if (user.getId().equals(host.getId())) continue; // 호스트 제외
            
            // 이미 참여했는지 확인
            if (sessionParticipantRepository.existsBySessionAndUser(session, user)) continue;

            SessionParticipant participant = SessionParticipant.builder()
                    .session(session)
                    .user(user)
                    .status(ParticipationStatus.APPROVED)
                    .attendanceStatus(AttendanceStatus.ABSENT)
                    .messageToHost("잘 부탁드립니다!")
                    .build();
            
            sessionParticipantRepository.save(participant);
        }
    }
}
