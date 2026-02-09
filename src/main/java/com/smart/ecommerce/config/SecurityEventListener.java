package com.smart.ecommerce.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

@Component
public class SecurityEventListener {

    private static final Logger logger = LoggerFactory.getLogger(SecurityEventListener.class);

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event) {
        String username = event.getAuthentication().getName();
        String ip = ((WebAuthenticationDetails) event.getAuthentication().getDetails()).getRemoteAddress();
        logger.info("LOGIN SUCCESS: user={} ip={}", username, ip);
    }

    @EventListener
    public void onFailure(AbstractAuthenticationFailureEvent event) {
        String username = event.getAuthentication().getName();
        String ip = ((WebAuthenticationDetails) event.getAuthentication().getDetails()).getRemoteAddress();
        logger.warn("LOGIN FAILURE: user={} ip={} reason={}", username, ip, event.getException().getMessage());
    }
}
