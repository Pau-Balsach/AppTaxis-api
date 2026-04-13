package com.apptaxis.api.security;

import com.apptaxis.api.model.ApiKey;
import com.apptaxis.api.repository.ApiKeyRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    public static final String ADMIN_ID_ATTR = "adminId";
    private static final String HEADER       = "X-API-Key";

    private final ApiKeyRepository apiKeyRepository;

    public ApiKeyFilter(ApiKeyRepository apiKeyRepository) {
        this.apiKeyRepository = apiKeyRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Rutas públicas — no requieren API key
        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        String rawKey = request.getHeader(HEADER);
        if (rawKey == null || rawKey.isBlank()) {
            reject(response, "Header X-API-Key requerido.");
            return;
        }

        String hash = SecurityUtils.generarHash(rawKey.trim());
        Optional<ApiKey> apiKey = apiKeyRepository.findByKeyHashAndActivaTrue(hash);

        if (apiKey.isEmpty()) {
            reject(response, "API Key inválida o inactiva.");
            return;
        }

        request.setAttribute(ADMIN_ID_ATTR, apiKey.get().getAdminId());
        chain.doFilter(request, response);
    }

    private boolean isPublicPath(String path) {
        return path.startsWith("/swagger-ui")
            || path.startsWith("/api-docs")
            || path.startsWith("/v3/api-docs")
            || path.equals("/swagger-ui.html")
            || path.equals("/favicon.ico");
    }

    private void reject(HttpServletResponse response, String mensaje) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(
            "{\"success\":false,\"error\":\"" + mensaje + "\"}"
        );
    }
}