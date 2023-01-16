package com.example.oath2springsecurity5.controller;

import com.example.oath2springsecurity5.dto.request.LoginRequest;
import com.example.oath2springsecurity5.dto.response.LoginResponse;
import com.example.oath2springsecurity5.service.LoginService;
import com.example.oath2springsecurity5.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/login")
public class LoginRestController {

    @Value("${oauth2.google.authorization.request-url}")
    private String googleAuthorizationRequestUrl;
    @Value("${oauth2.facebook.authorization.request-url}")
    private String facebookAuthorizationRequestUrl;

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;
    @Autowired
    private LoginService loginService;

    @Autowired
    private UserServiceImpl userServiceImpl;

//    @GetMapping("/oauth-login")
//    public String getLoginPage(Model model) {
////        Iterable<ClientRegistration> clientRegistrations = null;
////        ResolvableType type = ResolvableType.forInstance(clientRegistrationRepository)
////                .as(Iterable.class);
////        if (type != ResolvableType.NONE &&
////                ClientRegistration.class.isAssignableFrom(type.resolveGenerics()[0])) {
////            clientRegistrations = (Iterable<ClientRegistration>) clientRegistrationRepository;
////        }
//        Map<String, String> oauth2AuthenticationUrls = new HashMap<>();
//        oauth2AuthenticationUrls.put("google", googleAuthorizationRequestUrl);
//        oauth2AuthenticationUrls.put("facebook", facebookAuthorizationRequestUrl);
//        model.addAttribute("urls", oauth2AuthenticationUrls);
//        return "oauth_login";
//    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("")
    public LoginResponse loginJwtRequest(@RequestBody LoginRequest loginRequest) {
        return userServiceImpl.authenticate(loginRequest);
    }

    /* Lấy all thông tin sau khi được xác thực thành công */
    @GetMapping("/success")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponse oAuth2LoginSuccessHandler(OAuth2AuthenticationToken authentication,
                                                   HttpSession httpSession) {
        return loginService.handleOAuth2LoginSuccess(authentication);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @GetMapping("/failure")
    public Map<String, String> failureLoginHandler() {
        Map<String, String> errMap = new HashMap<>();
        errMap.put("error", "login failed.");
        return errMap;
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @GetMapping("/unauthenticated")
    public Map<String, String> unAuthenticated() {
        Map<String, String> errMap = new HashMap<>();
        errMap.put("error", "No Token");
        return errMap;
    }

}
