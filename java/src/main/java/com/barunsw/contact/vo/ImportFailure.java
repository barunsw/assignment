package com.barunsw.contact.vo;

/**
 * CSV 가져오기에서 실패한 줄과 그 이유.
 */
public record ImportFailure(int line, String reason) {
}
