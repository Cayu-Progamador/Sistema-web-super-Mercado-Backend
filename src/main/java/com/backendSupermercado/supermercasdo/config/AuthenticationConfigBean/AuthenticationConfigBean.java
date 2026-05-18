package com.backendSupermercado.supermercasdo.config.AuthenticationConfigBean;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

@Configuration
public class AuthenticationConfigBean {

    public AuthenticationManager authenticationManager(
        AuthenticationConfiguration authConfig)
    throws Exception{
        return authConfig.getAuthenticationManager();
    }
}
