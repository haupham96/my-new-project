
package com.example.bt.app.repository;

import com.example.bt.app.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * @author : HauPV
 * repository cho table user
 */

public interface IAppUserRepository extends JpaRepository<AppUser, Integer> {

//	Lấy ra user theo username
	@Query( value = " select * from user where username = ? ", nativeQuery = true )
	AppUser findByUsername( String username );
	
}
