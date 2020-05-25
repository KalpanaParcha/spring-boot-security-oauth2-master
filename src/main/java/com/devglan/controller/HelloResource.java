package com.devglan.controller;

import javax.annotation.Resource;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.devglan.service.impl.UserServiceImpl;

import util.JwtUtil;

@RestController
@ComponentScan({"util"})
public class HelloResource {
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@Resource(name = "userService")
    private UserServiceImpl userDetailsService;

	@GetMapping(path="/hello")
	public String hello() {
		return "hello";
	}

	@PostMapping(path="/authenticate/username/{username}/password/{password}")
	public ResponseEntity<Object> createAuthenticationToken(@PathVariable("username") @NotEmpty String username,@PathVariable("password") @NotNull String password) throws Exception {
		try{
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		}catch (BadCredentialsException e) {
			// TODO: handle exception
			throw new Exception("Incorrect username or password",e);
		}
		final UserDetails userDetails=userDetailsService.loadUserByUsername(username);
		
		final String jwt =jwtUtil.generateToken(userDetails);
		
		return ResponseEntity.ok((jwt));
				
		
		
		
	}
	
}
