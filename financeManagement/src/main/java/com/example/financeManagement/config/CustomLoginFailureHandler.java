package com.example.financeManagement.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

public class CustomLoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException, ServletException {

        String errorMessage = "Invalid username or password";

        if (exception instanceof BadCredentialsException) {
            errorMessage = "Incorrect username or password";
        }

        // Redirect to /login with an error parameter
        response.sendRedirect("/login?error=" + errorMessage.replaceAll(" ", "%20"));
    }
}

