package com.barun.contact.dto;

import java.util.List;

public record ImportResult(int total, int imported, int failed, List<ImportFailure> failures) {
}
