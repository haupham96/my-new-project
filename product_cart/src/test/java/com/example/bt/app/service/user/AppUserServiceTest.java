package com.example.bt.app.service.user;

import com.example.bt.app.entity.AppUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author HauPV
 * JUnit Test cho AppUserService
 * => OK
 */
@SpringBootTest
 class AppUserServiceTest {

    @Autowired
    private IAppUserService iAppUserService;

    /**
     * Trường hợp không tìm thấy user -> throw UsernameNotFoundException
     */
    @Test
    @DisplayName("loadUserByUsername_FailWithInvalidUsername")
     void loadUserByUsername_FailWithInvalidUsername() {
        String invalidUsername = "abc";
        assertThrows(UsernameNotFoundException.class, () -> {
            this.iAppUserService.loadUserByUsername(invalidUsername);
        });
    }

    /**
     * Trường hợp có kí tự đặc biệt
     */
    @Test
    @DisplayName("loadUserByUsername_FailWithSpecialCharacter")
    void loadUserByUsername_FailWithSpecialCharacter() {
        String invalidUsername = "ab///--c";
        assertThrows(UsernameNotFoundException.class, () -> {
            this.iAppUserService.loadUserByUsername(invalidUsername);
        });
    }

    /**
     * Trường hợp tìm thấy user
     */
    @Test
    @DisplayName("loadUserByUsername_Success")
     void loadUserByUsername_Success() {
        String validUsername = "user";
        UserDetails userDetails = this.iAppUserService.loadUserByUsername(validUsername);
        assertNotNull(userDetails);
        assertEquals(validUsername, userDetails.getUsername());
    }

    /**
     * Trường hợp tìm thấy user
     */
    @Test
    @DisplayName("findUserByUsername_Success")
     void findUserByUsername_Success() {
        String validUsername = "user";
        AppUser appUser = this.iAppUserService.findByUsername(validUsername);
        assertNotNull(appUser);
        assertEquals(validUsername, appUser.getUsername());
    }

    /**
     * Trường hợp không tìm thấy user -> throw UsernameNotFoundException
     */
    @Test
    @DisplayName("findUserByUsername_FailWithInvalidUsername")
    void findUserByUsername_FailWithInvalidUsername() {
        String invalidUsername = "abc";
        assertThrows(UsernameNotFoundException.class, () -> {
            this.iAppUserService.findByUsername(invalidUsername);
        });
    }

}
