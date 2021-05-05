package com.dgut;

import com.dgut.model.Role;
import com.dgut.model.User;
import com.dgut.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SecurityTestApplicationTests {

    @Autowired
    UserService userService;

    @Test
    void contextLoads() {
        final Role r1 = new Role();
        r1.setId(1L);
        final Role r2 = new Role();
        r2.setId(2L);

        final User u1 = new User("linux", "123", true, r1);
        System.out.println("u1 = " + u1);
        final User u2 = new User("admin", "123", true, r2);
        System.out.println("u2 = " + u2);

        userService.saveUser(u1);
        userService.saveUser(u2);
    }

}
