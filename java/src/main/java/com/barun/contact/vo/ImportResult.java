package com.barun.contact.vo;

import java.util.List;

/**
 * CSV 가져오기 결과 요약.
 */
public record ImportResult(int total, int imported, int failed, List<ImportFailure> failures) {
}
