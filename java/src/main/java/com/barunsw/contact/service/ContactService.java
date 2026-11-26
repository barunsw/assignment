package com.barunsw.contact.service;

import com.barunsw.contact.domain.PhoneNumber;
import com.barunsw.contact.exception.AppException;
import com.barunsw.contact.exception.DuplicateContactException;
import com.barunsw.contact.exception.InvalidInputException;
import com.barunsw.contact.mapper.ContactMapper;
import com.barunsw.contact.mapper.GroupMapper;
import com.barunsw.contact.validator.ContactValidator;
import com.barunsw.contact.vo.Contact;
import com.barunsw.contact.vo.ContactCreateRequest;
import com.barunsw.contact.vo.ContactCsvRow;
import com.barunsw.contact.vo.ContactGroup;
import com.barunsw.contact.vo.ContactPageResponse;
import com.barunsw.contact.vo.ContactResponse;
import com.barunsw.contact.vo.ImportFailure;
import com.barunsw.contact.vo.ImportResult;
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

/**
 * 연락처 업무 규칙: 등록, 조회, 목록, 검색, CSV 가져오기.
 * public 메서드 하나가 트랜잭션 하나다(@Transactional). 조회 메서드는 readOnly로 둬서 쓰기가 섞이지 않게 한다.
 */
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

    /**
     * 연락처를 등록하고, 그룹 이름까지 붙은 결과를 다시 조회해 돌려준다.
     */
    @Transactional
    public ContactResponse registerContact(ContactCreateRequest request) {
        validate(request);
        // 없는 그룹은 외래 키로도 막히지만, 그러면 500이 난다. 400과 이유를 주려고 먼저 확인한다.
        if (request.groupId() != null && groupMapper.selectGroupById(request.groupId()) == null) {
            throw new InvalidInputException("존재하지 않는 그룹입니다. group_id=" + request.groupId());
        }
        Contact contact = insertNewContact(request);
        return ContactResponse.from(contactMapper.selectContactById(contact.getId()));
    }

    /**
     * id로 연락처 한 명을 조회한다.
     */
    @Transactional(readOnly = true)
    public ContactResponse getContact(long id) {
        Contact contact = contactMapper.selectContactById(id);
        return ContactResponse.from(contact);
    }

    /**
     * 연락처 목록 한 페이지와 전체 연락처 수를 돌려준다.
     */
    @Transactional(readOnly = true)
    public ContactPageResponse getContactPage(int page, int size, String sort) {
        if (page < 1) {
            throw new InvalidInputException("page는 1 이상이어야 합니다.");
        }
        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new InvalidInputException("size는 1~" + MAX_PAGE_SIZE + " 사이여야 합니다.");
        }
        // page는 1부터 시작하므로 1페이지의 offset이 0이 되게 한다.
        int offset = (page - 1) * size;
        List<ContactResponse> itemList = contactMapper.selectContactList(sort, size, offset).stream()
                .map(ContactResponse::from)
                .toList();
        return new ContactPageResponse(page, size, contactMapper.countContact(), itemList);
    }

    /**
     * 전화번호 일부로 연락처를 검색한다.
     */
    @Transactional(readOnly = true)
    public List<ContactResponse> searchContactsByPhone(String phone) {
        // TODO: 전화번호 부분 일치 검색 구현 (README의 API 사양 참고)
        throw new UnsupportedOperationException("전화번호 검색은 아직 구현되지 않았습니다.");
    }

    /**
     * CSV로 연락처를 가져온다. 규칙에 맞지 않는 줄은 실패 목록에 적고, 나머지 줄은 계속 가져온다.
     */
    @Transactional
    public ImportResult importContacts(InputStream inputStream) {
        List<ContactCsvRow> rowList = contactCsvParser.parse(inputStream);
        // 줄마다 DB를 조회하지 않도록 그룹 이름 → id를 한 번에 읽어 둔다.
        Map<String, Long> groupIdByName = loadGroupIdByName();
        // PhoneNumber는 숫자로 비교(equals/hashCode)하므로, 하이픈 유무가 달라도 같은 번호로 걸러진다.
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

    /**
     * CSV 한 줄을 등록 요청으로 바꿔, 등록 API와 같은 규칙으로 검증하고 저장한다.
     */
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

    /**
     * 그룹 이름을 id로 바꾼다. 비어 있으면 그룹 없음(null).
     */
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

    /**
     * 그룹 이름 → id.
     */
    private Map<String, Long> loadGroupIdByName() {
        Map<String, Long> groupIdByName = new HashMap<>();
        for (ContactGroup group : groupMapper.selectGroupList()) {
            groupIdByName.put(group.getName(), group.getId());
        }
        return groupIdByName;
    }

    /**
     * 중복을 확인하고 저장한다. 전화번호는 숫자만 저장한다.
     */
    private Contact insertNewContact(ContactCreateRequest request) {
        PhoneNumber phoneNumber = PhoneNumber.of(request.phone());
        // phone 열의 UNIQUE 제약으로도 막히지만, 그러면 500이 난다. 409와 이유를 주려고 먼저 확인한다.
        if (contactMapper.countContactByPhone(phoneNumber.getDigits()) > 0) {
            throw new DuplicateContactException("이미 등록된 전화번호입니다.");
        }

        Contact contact = new Contact();
        contact.setName(request.name().strip());
        contact.setPhone(phoneNumber.getDigits());
        contact.setEmail(blankToNull(request.email()));
        contact.setGroupId(request.groupId());
        contactMapper.insertContact(contact);
        // Contact.toString()이 전화번호를 가리고 이메일을 빼므로 개인정보가 로그에 그대로 남지 않는다.
        log.info("연락처 등록: {}", contact);
        return contact;
    }

    /**
     * 검증 규칙을 @Order 순서대로 적용한다. 규칙을 추가해도 이 메서드는 바뀌지 않는다.
     */
    private void validate(ContactCreateRequest request) {
        for (ContactValidator contactValidator : contactValidatorList) {
            contactValidator.validate(request);
        }
    }

    /**
     * 빈 문자열을 null로 바꾼다. "값 없음"을 DB에 NULL 하나로만 저장하기 위해서다.
     */
    private static String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.strip();
    }
}
