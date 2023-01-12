package com.example.bt.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.bt.app.entity.Role;

/**
 * @author : HauPV
 * repository cho table role
 */
public interface IRoleRepository extends JpaRepository<Role, Integer>{
	 
}
