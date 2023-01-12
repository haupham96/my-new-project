
package com.example.bt.app.service.user;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.bt.app.entity.AppUser;
import com.example.bt.app.repository.IAppUserRepository;

import lombok.RequiredArgsConstructor;

/**
 * @author : HauPV
 * service cho user
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements IAppUserService {

    private final IAppUserRepository iAppUserRepository;

    //	load thông tin xác thực của user theo username
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("class - AppUserServiceImpl");
        log.info("method - loadUserByUsername()");
        AppUser appUser = iAppUserRepository.findByUsername(username);
        if (appUser == null) {
//			Nếu không tìm thấy user phù hợp -> Throw Exception
            log.info("khối if : appUser == null");
            log.info("kết thúc khối if : appUser == null -> throw UsernameNotFoundException");
            throw new UsernameNotFoundException("Not found user : " + username);
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(appUser.getRole().getRoleName()));

        UserDetails userDetails = new User(appUser.getUsername(), appUser.getPassword(), authorities);

        log.info("kết thúc method - loadUserByUsername()");
        return userDetails;

    }

    //	Tìm user theo username trong db
    @Override
    public AppUser findByUsername(String username) throws UsernameNotFoundException {
        log.info("class - AppUserServiceImpl");
        log.info("method - findByUsername()");
        AppUser appUser = iAppUserRepository.findByUsername(username);
        if (appUser != null) {
//          Trường hợp tìm thấy user -> return
            log.info("khối if : appUser != null");
            log.info("kết thúc khối if : appUser != null");
            return appUser;
        } else {
//          Trường hợp không tìm thấy user -> throw Exception
            log.info("khối else : appUser == null");
            log.info("kết thúc khối else : appUser == null");
            log.info("kết thúc method - findByUsername() -> Throw UsernameNotFoundException");
            throw new UsernameNotFoundException("Not found user : " + username);
        }

    }

}
