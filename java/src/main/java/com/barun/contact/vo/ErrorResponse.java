package com.barun.contact.vo;

/**
 * 공통 오류 응답.
 */
public record ErrorResponse(String code, String message) {
}
