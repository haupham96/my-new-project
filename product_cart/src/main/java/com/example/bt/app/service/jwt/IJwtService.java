package com.example.bt.app.service.jwt;

import com.example.bt.app.dto.login.LoginRequest;
import com.example.bt.app.dto.login.LoginResponse;

/**
 * @author : HauPV
 * service cho JWT
 */
public interface IJwtService {

	LoginResponse authenticate( LoginRequest loginRequest );
	
}
