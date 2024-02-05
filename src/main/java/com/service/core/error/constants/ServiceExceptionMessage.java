package com.service.core.error.constants;

public enum ServiceExceptionMessage {
    // 인증 처리 관련 에러 메시지
    NOT_VERIFIED_VIDEO_TOKEN("인증되지 않은 비디오 토큰 입니다.");

    private final String message;

    ServiceExceptionMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
