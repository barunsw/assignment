package com.barunsw.contact.service;

import com.barunsw.contact.vo.ContactCsvRow;
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

    /**
     * CSV를 행 목록으로 바꾼다. 첫 줄(헤더)과 빈 줄은 건너뛴다.
     */
    public List<ContactCsvRow> parse(InputStream inputStream) {
        List<ContactCsvRow> rowList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            // 파일 맨 앞의 BOM은 헤더 줄에 붙어 있으므로, 헤더를 읽고 버릴 때 함께 사라진다.
            String headerLine = reader.readLine();
            if (headerLine == null) {
                return rowList;
            }
            // 실패 사유에 파일의 줄 번호를 알려 주려고, 건너뛴 빈 줄도 센다.
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

    /**
     * 한 줄을 열로 나눠 ContactCsvRow로 만든다. 값의 앞뒤 공백은 지운다.
     */
    private ContactCsvRow toRow(int lineNumber, String line) {
        String[] columns = line.split(",");
        return new ContactCsvRow(lineNumber, columns[0].trim(), columns[1].trim(), columns[2].trim(), columns[3].trim());
    }
}
