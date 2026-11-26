package com.barun.contact.controller;

import com.barun.contact.service.ContactService;
import com.barun.contact.vo.ContactCreateRequest;
import com.barun.contact.vo.ContactPageResponse;
import com.barun.contact.vo.ContactResponse;
import com.barun.contact.vo.ImportResult;
import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 연락처 API. 요청을 받아 서비스에 넘기고 결과를 돌려주기만 한다. 업무 규칙은 ContactService에 둔다.
 */
@RestController
@RequestMapping("/api/contacts")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    /**
     * 연락처를 등록한다. 성공하면 201 Created와 등록된 연락처를 돌려준다.
     */
    @PostMapping
    public ResponseEntity<ContactResponse> registerContact(@RequestBody ContactCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contactService.registerContact(request));
    }

    /**
     * id로 연락처를 조회한다.
     */
    @GetMapping("/{id}")
    public ContactResponse getContact(@PathVariable long id) {
        return contactService.getContact(id);
    }

    /**
     * 연락처 목록 한 페이지를 조회한다.
     */
    @GetMapping
    public ContactPageResponse getContactPage(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "id") String sort) {
        return contactService.getContactPage(page, size, sort);
    }

    /**
     * 전화번호 일부로 연락처를 검색한다.
     * phone이 빠진 요청도 서비스가 같은 규칙으로 판단하도록 required = false로 받는다.
     */
    @GetMapping("/search")
    public List<ContactResponse> searchContacts(@RequestParam(required = false) String phone) {
        return contactService.searchContactsByPhone(phone);
    }

    /**
     * CSV 파일로 연락처를 한꺼번에 가져온다. 실패한 줄이 있어도 200과 결과 요약을 돌려준다.
     */
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImportResult importContacts(@RequestPart("file") MultipartFile file) throws IOException {
        return contactService.importContacts(file.getInputStream());
    }
}
