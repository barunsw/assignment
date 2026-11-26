package com.barun.contact.mapper;

import com.barun.contact.domain.Contact;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * contact 테이블 SQL. 구현 클래스는 MyBatis가 만든다.
 * SQL은 resources/mapper/ContactMapper.xml에 있고, 메서드 이름과 XML의 id가 짝을 이룬다.
 */
@Mapper
public interface ContactMapper {

    /**
     * id로 연락처 한 명을 조회한다. 없으면 null.
     */
    Contact selectContactById(@Param("id") long id);

    /**
     * 연락처 한 페이지를 정렬해서 조회한다.
     */
    List<Contact> selectContactList(@Param("sort") String sort, @Param("size") int size, @Param("offset") int offset);

    /**
     * 전체 연락처 수.
     */
    long countContact();

    /**
     * 숫자만 남긴 전화번호로 등록된 연락처 수. 중복 검사에 쓴다.
     */
    int countContactByPhone(@Param("phone") String phone);

    /**
     * 연락처를 저장한다. DB가 만든 id는 contact의 id 필드에 채워진다(XML의 useGeneratedKeys).
     */
    int insertContact(Contact contact);
}
