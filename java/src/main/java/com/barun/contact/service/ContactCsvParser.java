package com.barun.contact.service;

import com.barun.contact.dto.ContactCsvRow;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * name,phone,email,group 형식의 CSV를 줄 단위로 읽는다. 값 검증은 하지 않는다.
 */
@Component
public class ContactCsvParser {

    public List<ContactCsvRow> parse(InputStream inputStream) {
        List<ContactCsvRow> rowList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                return rowList;
            }
            int lineNumber = 1;
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) {
                    continue;
                }
                rowList.add(toRow(lineNumber, line));
            }
        } catch (IOException e) {
            throw new UncheckedIOException("CSV 파일을 읽지 못했습니다.", e);
        }
        return rowList;
    }

    private ContactCsvRow toRow(int lineNumber, String line) {
        String[] columns = line.split(",");
        return new ContactCsvRow(lineNumber, columns[0].trim(), columns[1].trim(), columns[2].trim(), columns[3].trim());
    }
}
