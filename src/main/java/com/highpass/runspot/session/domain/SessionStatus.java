package com.highpass.runspot.session.domain;

public enum SessionStatus {
    OPEN,
    CLOSED, //마감
    CANCELED, //취소
    FINISHED //출석체크 완료 및 러닝시작
}
