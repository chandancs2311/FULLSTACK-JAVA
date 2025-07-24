package com.example.ShopEase.exception;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException)
            throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");

        String json = String.format("""
            {
              "timestamp": "%s",
              "status": 403,
              "error": "Forbidden",
              "message": "%s",
              "path": "%s"
            }
            """,
                LocalDateTime.now(),
                accessDeniedException.getMessage(),
                request.getRequestURI()
        );

        response.getWriter().write(json);
    }
}
