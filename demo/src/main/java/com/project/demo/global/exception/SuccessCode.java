package com.project.demo.global.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum SuccessCode {
    // 200 OK
    LOGIN_USER_SUCCESS(HttpStatus.OK, "로그인에 성공했습니다"),
    GET_POST_SUCCESS(HttpStatus.OK, "게시글 조회가 완료되었습니다."),
    GET_ALL_POST_SUCCESS(HttpStatus.OK, "사용자가 작성한 전체 게시글 조회를 완료했습니다"),
    UPDATE_POST_SUCCESS(HttpStatus.OK, "게시글 수정이 완료되었습니다."),
    REPORT_SUCCESS(HttpStatus.OK, "신고가 정상적으로 이루어졌습니다."),
    SMS_CERT_MESSAGE_SUCCESS(HttpStatus.OK, "메세지 전송이 완료되었습니다."),
    SMS_VERIFY_SUCCESS(HttpStatus.OK, "본인확인 인증에 성공했습니다"),
    EMAIL_CERT_MESSAGE_SUCCESS(HttpStatus.OK, "이메일 전송이 완료되었습니다."),
    EMAIL_VERIFY_SUCCESS(HttpStatus.OK, "이메일 인증에 성공했습니다"),

    // 201 Created, Delete
    CREATE_POST_SUCCESS(HttpStatus.CREATED, "게시글 생성이 완료되었습니다."),
    DELETE_ATTENDANCE_SUCCESS(HttpStatus.NO_CONTENT, "작업 현장 탈퇴가 완료되었습니다."),
    DELETE_REPORT_SUCCESS(HttpStatus.NO_CONTENT, "신고 내역이 정상적으로 삭제되었습니다. "),

    // Notice
    NOTICE_CREATED(HttpStatus.CREATED, "공지사항이 성공적으로 생성되었습니다."),
    NOTICE_UPDATED(HttpStatus.OK, "공지사항이 성공적으로 수정되었습니다."),
    NOTICE_DELETED(HttpStatus.OK, "공지사항이 성공적으로 삭제되었습니다."),
    NOTICE_FOUND(HttpStatus.OK, "공지사항 조회 성공"),

    // Recipe
    RECIPE_CREATED(HttpStatus.CREATED, "레시피가 성공적으로 생성되었습니다."),
    RECIPE_UPDATED(HttpStatus.OK, "레시피가 성공적으로 수정되었습니다."),
    RECIPE_DELETED(HttpStatus.OK, "레시피가 성공적으로 삭제되었습니다."),
    RECIPE_FOUND(HttpStatus.OK, "레시피 조회 성공"),

    // Server
    ANALYSIS_SUCCESS(HttpStatus.OK, "이미지 분석 성공"),

    IMAGE_UPLOAD_SUCCESS(HttpStatus.OK, "이미지 업로드 성공");

    private final HttpStatus httpStatus;
    private final String message;

    public int getHttpStatusCode(){
        return httpStatus.value();
    }
}
