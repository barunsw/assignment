package com.barunsw.contact;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 주소록 API 실행 클래스.
 * 스프링이 이 패키지 아래의 @RestController, @Service, @Component, @Mapper를 찾아 객체를 만들고 생성자로 서로 연결한다(Python판의 app_factory.py가 하는 일).
 */
@SpringBootApplication
public class ContactApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContactApplication.class, args);
    }
}
