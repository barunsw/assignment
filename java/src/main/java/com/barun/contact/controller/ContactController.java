package com.barun.contact.controller;

import com.barun.contact.dto.ContactCreateRequest;
import com.barun.contact.dto.ContactPageResponse;
import com.barun.contact.dto.ContactResponse;
import com.barun.contact.dto.ImportResult;
import com.barun.contact.service.ContactService;
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

@RestController
@RequestMapping("/api/contacts")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping
    public ResponseEntity<ContactResponse> registerContact(@RequestBody ContactCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contactService.registerContact(request));
    }

    @GetMapping("/{id}")
    public ContactResponse getContact(@PathVariable long id) {
        return contactService.getContact(id);
    }

    @GetMapping
    public ContactPageResponse getContactPage(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "id") String sort) {
        return contactService.getContactPage(page, size, sort);
    }

    @GetMapping("/search")
    public List<ContactResponse> searchContacts(@RequestParam(required = false) String phone) {
        return contactService.searchContactsByPhone(phone);
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImportResult importContacts(@RequestPart("file") MultipartFile file) throws IOException {
        return contactService.importContacts(file.getInputStream());
    }
}
