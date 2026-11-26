package com.barun.contact.dto;

/**
 * CSV 한 줄. lineNumber는 헤더를 1번째 줄로 센 파일의 줄 번호.
 */
public record ContactCsvRow(int lineNumber, String name, String phone, String email, String groupName) {
}
