package com.barun.contact.service;

import com.barun.contact.domain.Contact;
import com.barun.contact.domain.ContactGroup;
import com.barun.contact.domain.PhoneNumber;
import com.barun.contact.dto.ContactCreateRequest;
import com.barun.contact.dto.ContactCsvRow;
import com.barun.contact.dto.ContactPageResponse;
import com.barun.contact.dto.ContactResponse;
import com.barun.contact.dto.ImportFailure;
import com.barun.contact.dto.ImportResult;
import com.barun.contact.exception.AppException;
import com.barun.contact.exception.DuplicateContactException;
import com.barun.contact.exception.InvalidInputException;
import com.barun.contact.mapper.ContactMapper;
import com.barun.contact.mapper.GroupMapper;
import com.barun.contact.validator.ContactValidator;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContactService {

    private static final Logger log = LoggerFactory.getLogger(ContactService.class);
    private static final int MAX_PAGE_SIZE = 100;

    private final ContactMapper contactMapper;
    private final GroupMapper groupMapper;
    private final List<ContactValidator> contactValidatorList;
    private final ContactCsvParser contactCsvParser;

    public ContactService(ContactMapper contactMapper, GroupMapper groupMapper, List<ContactValidator> contactValidatorList, ContactCsvParser contactCsvParser) {
        this.contactMapper = contactMapper;
        this.groupMapper = groupMapper;
        this.contactValidatorList = contactValidatorList;
        this.contactCsvParser = contactCsvParser;
    }

    @Transactional
    public ContactResponse registerContact(ContactCreateRequest request) {
        validate(request);
        if (request.groupId() != null && groupMapper.selectGroupById(request.groupId()) == null) {
            throw new InvalidInputException("존재하지 않는 그룹입니다. group_id=" + request.groupId());
        }
        Contact contact = insertNewContact(request);
        return ContactResponse.from(contactMapper.selectContactById(contact.getId()));
    }

    @Transactional(readOnly = true)
    public ContactResponse getContact(long id) {
        Contact contact = contactMapper.selectContactById(id);
        return ContactResponse.from(contact);
    }

    @Transactional(readOnly = true)
    public ContactPageResponse getContactPage(int page, int size, String sort) {
        if (page < 1) {
            throw new InvalidInputException("page는 1 이상이어야 합니다.");
        }
        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new InvalidInputException("size는 1~" + MAX_PAGE_SIZE + " 사이여야 합니다.");
        }
        int offset = (page - 1) * size;
        List<ContactResponse> itemList = contactMapper.selectContactList(sort, size, offset).stream()
                .map(ContactResponse::from)
                .toList();
        return new ContactPageResponse(page, size, contactMapper.countContact(), itemList);
    }

    @Transactional(readOnly = true)
    public List<ContactResponse> searchContactsByPhone(String phone) {
        // TODO: 전화번호 부분 일치 검색 구현 (README의 API 사양 참고)
        throw new UnsupportedOperationException("전화번호 검색은 아직 구현되지 않았습니다.");
    }

    @Transactional
    public ImportResult importContacts(InputStream inputStream) {
        List<ContactCsvRow> rowList = contactCsvParser.parse(inputStream);
        Map<String, Long> groupIdByName = loadGroupIdByName();
        Set<PhoneNumber> importedPhoneSet = new HashSet<>();
        List<ImportFailure> failureList = new ArrayList<>();

        for (ContactCsvRow row : rowList) {
            try {
                importRow(row, groupIdByName, importedPhoneSet);
            } catch (AppException e) {
                failureList.add(new ImportFailure(row.lineNumber(), e.getMessage()));
            }
        }

        int importedCount = rowList.size() - failureList.size();
        log.info("CSV 가져오기 완료: 전체 {}건, 성공 {}건, 실패 {}건", rowList.size(), importedCount, failureList.size());
        return new ImportResult(rowList.size(), importedCount, failureList.size(), failureList);
    }

    private void importRow(ContactCsvRow row, Map<String, Long> groupIdByName, Set<PhoneNumber> importedPhoneSet) {
        Long groupId = resolveGroupId(row.groupName(), groupIdByName);
        ContactCreateRequest request = new ContactCreateRequest(row.name(), row.phone(), row.email(), groupId);
        validate(request);
        // 같은 파일 안의 중복은 DB에 넣기 전에 걸러서 사유를 따로 알려 준다.
        if (!importedPhoneSet.add(PhoneNumber.of(request.phone()))) {
            throw new DuplicateContactException("파일 안에서 중복된 전화번호입니다.");
        }
        insertNewContact(request);
    }

    private Long resolveGroupId(String groupName, Map<String, Long> groupIdByName) {
        if (groupName == null || groupName.isBlank()) {
            return null;
        }
        Long groupId = groupIdByName.get(groupName);
        if (groupId == null) {
            throw new InvalidInputException("존재하지 않는 그룹입니다: " + groupName);
        }
        return groupId;
    }

    private Map<String, Long> loadGroupIdByName() {
        Map<String, Long> groupIdByName = new HashMap<>();
        for (ContactGroup group : groupMapper.selectGroupList()) {
            groupIdByName.put(group.getName(), group.getId());
        }
        return groupIdByName;
    }

    private Contact insertNewContact(ContactCreateRequest request) {
        PhoneNumber phoneNumber = PhoneNumber.of(request.phone());
        if (contactMapper.countContactByPhone(phoneNumber.getDigits()) > 0) {
            throw new DuplicateContactException("이미 등록된 전화번호입니다.");
        }

        Contact contact = new Contact();
        contact.setName(request.name().strip());
        contact.setPhone(phoneNumber.getDigits());
        contact.setEmail(blankToNull(request.email()));
        contact.setGroupId(request.groupId());
        contactMapper.insertContact(contact);
        log.info("연락처 등록: {}", contact);
        return contact;
    }

    private void validate(ContactCreateRequest request) {
        for (ContactValidator contactValidator : contactValidatorList) {
            contactValidator.validate(request);
        }
    }

    private static String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.strip();
    }
}
