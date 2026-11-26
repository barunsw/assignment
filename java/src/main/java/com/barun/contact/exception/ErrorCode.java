package com.barun.contact.exception;

/**
 * 오류 응답의 code 값. HTTP 상태 코드로 바꾸는 일은 컨트롤러 계층(GlobalExceptionHandler)이 맡는다.
 */
public enum ErrorCode {
    INVALID_INPUT,
    CONTACT_NOT_FOUND,
    DUPLICATE_CONTACT,
    NOT_IMPLEMENTED,
    INTERNAL_ERROR
}
