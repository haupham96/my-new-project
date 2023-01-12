package com.example.bt.app.service.user;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.example.bt.app.entity.AppUser;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * @author : HauPV
 * service cho user
 */
public interface IAppUserService extends UserDetailsService {
	
	AppUser findByUsername(String username) throws UsernameNotFoundException;
}
