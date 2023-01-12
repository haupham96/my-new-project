package com.example.bt.app.repository;

import com.example.bt.app.entity.AppUser;
import com.example.bt.common.SecurityJWT;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author HauPV
 * JUnit Test cho IAppUserRepository
 */
@SpringBootTest
class AppUserRepositoryTest {

    @Autowired
    IAppUserRepository iAppUserRepository;

    /**
     * Trường hợp tìm thấy user trong hệ thống
     */
    @Test
    @DisplayName("findByUsername_Success")
    void findByUsername_Success() {
        String validUsername = "admin";
        AppUser appUser = this.iAppUserRepository.findByUsername(validUsername);
        assertNotNull(appUser);
        assertEquals(validUsername, appUser.getUsername());
        assertEquals(SecurityJWT.ROLE_ADMIN, appUser.getRole().getRoleName());
    }

    /**
     * Trường hợp không tìm thấy user
     */
    @Test
    @DisplayName("findByUsername_FailWithInValidUsername")
    void findByUsername_FailWithInValidUsername() {
        String invalidUsername = "abc";
        AppUser appUser = this.iAppUserRepository.findByUsername(invalidUsername);
        assertNull(appUser);
    }

}
