package com.barunsw.contact.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * 테스트마다 트랜잭션을 롤백하므로 서로의 데이터에 영향을 주지 않는다.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ContactApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void 연락처를_등록하면_201과_하이픈_넣은_번호를_돌려준다() throws Exception {
        mockMvc.perform(post("/api/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"홍길동\",\"phone\":\"01012345678\",\"email\":\"hong@example.com\",\"group_id\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("홍길동"))
                .andExpect(jsonPath("$.phone").value("010-1234-5678"))
                .andExpect(jsonPath("$.group_name").value("가족"));
    }

    @Test
    void 전화번호_형식이_틀리면_400() throws Exception {
        mockMvc.perform(post("/api/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"홍길동\",\"phone\":\"02-123\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_INPUT"));
    }

    @Test
    void 하이픈만_다른_같은_번호를_다시_등록하면_409() throws Exception {
        registerContact("홍길동", "010-1234-5678");

        mockMvc.perform(post("/api/contacts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"고길동\",\"phone\":\"01012345678\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("DUPLICATE_CONTACT"));
    }

    @Test
    void 등록한_연락처를_id로_조회한다() throws Exception {
        long id = registerContact("홍길동", "010-1234-5678");

        mockMvc.perform(get("/api/contacts/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.group_id").isEmpty());
    }

    @Test
    void 목록을_이름순으로_조회한다() throws Exception {
        registerContact("나영", "010-1111-1111");
        registerContact("가영", "010-2222-2222");

        mockMvc.perform(get("/api/contacts").param("sort", "name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total_count").value(2))
                .andExpect(jsonPath("$.items[0].name").value("가영"))
                .andExpect(jsonPath("$.items[1].name").value("나영"));
    }

    @Test
    void CSV_예시_파일을_가져온다() throws Exception {
        String csv = """
                name,phone,email,group
                홍길동,010-1234-5678,hong@example.com,가족
                김철수,010-2222-3333,,친구
                """;
        MockMultipartFile file = new MockMultipartFile("file", "contacts.csv", "text/csv", csv.getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/api/contacts/import").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(2))
                .andExpect(jsonPath("$.imported").value(2))
                .andExpect(jsonPath("$.failed").value(0));
    }

    @Test
    void 그룹_목록은_연락처가_없는_그룹도_0으로_보여준다() throws Exception {
        mockMvc.perform(get("/api/groups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].name").value("가족"))
                .andExpect(jsonPath("$[0].contact_count").value(0));
    }

    private long registerContact(String name, String phone) throws Exception {
        String body = "{\"name\":\"" + name + "\",\"phone\":\"" + phone + "\"}";
        String response = mockMvc.perform(post("/api/contacts").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        JsonNode jsonNode = objectMapper.readTree(response);
        return jsonNode.get("id").asLong();
    }
}
