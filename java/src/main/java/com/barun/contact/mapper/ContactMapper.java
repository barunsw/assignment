package com.barun.contact.mapper;

import com.barun.contact.domain.Contact;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ContactMapper {

    Contact selectContactById(@Param("id") long id);

    List<Contact> selectContactList(@Param("sort") String sort, @Param("size") int size, @Param("offset") int offset);

    long countContact();

    int countContactByPhone(@Param("phone") String phone);

    int insertContact(Contact contact);
}
