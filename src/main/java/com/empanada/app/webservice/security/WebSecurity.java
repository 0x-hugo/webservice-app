package com.empanada.app.webservice.security;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.empanada.app.webservice.service.UserService;

@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter{

	private final UserService userService;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	
	public WebSecurity (UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.userService = userService;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}
	 
	@Override
	protected void configure(HttpSecurity http) throws Exception { 
		http.cors().and().csrf().disable().authorizeRequests()
		.antMatchers(HttpMethod.POST, SecurityConstants.SIGN_UP_URL)
		.permitAll()
		.antMatchers(HttpMethod.GET, SecurityConstants.EMAIL_VERIFICATION_URL)
		.permitAll()
//		.anyRequest()
//		.authenticated()
		.and()
		//New AuthenticationFilter set up /login as default login url. It can also get updated by override getAuthenticationFilter
		.addFilter(new AuthenticationFilter(authenticationManager()))
		
		//TODO: Setup tokens using filters
		.addFilter(new AuthorizationFilter(authenticationManager()))
		
		//Set stateless to avoid potential security issues (and reauth every request)
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); 
		

	}
	
	@Override
	public void configure (AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
	}
}
