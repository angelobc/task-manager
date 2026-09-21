package com.taskmanager.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RateLimitFilterTest {

    private RateLimitFilter filter;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        filter = new RateLimitFilter(new ObjectMapper());
        filterChain = mock(FilterChain.class);
    }

    @Test
    void allowsRequestsUnderTheLimitAndBlocksAfter() throws Exception {
        HttpServletRequest request = requestFor("/api/v1/auth/login", "10.0.0.1");

        for (int i = 0; i < 5; i++) {
            filter.doFilterInternal(request, new MockHttpServletResponse(), filterChain);
        }
        verify(filterChain, times(5)).doFilter(eq(request), any());

        MockHttpServletResponse blockedResponse = new MockHttpServletResponse();
        filter.doFilterInternal(request, blockedResponse, filterChain);

        verify(filterChain, times(5)).doFilter(eq(request), any());
        assertThat(blockedResponse.getStatus()).isEqualTo(429);
        assertThat(blockedResponse.getContentAsString()).contains("Too many requests");
    }

    @Test
    void tracksLimitsIndependentlyPerClientIp() throws Exception {
        HttpServletRequest requestIp1 = requestFor("/api/v1/auth/login", "10.0.0.2");
        HttpServletRequest requestIp2 = requestFor("/api/v1/auth/login", "10.0.0.3");

        for (int i = 0; i < 5; i++) {
            filter.doFilterInternal(requestIp1, new MockHttpServletResponse(), filterChain);
        }
        MockHttpServletResponse blockedForIp1 = new MockHttpServletResponse();
        filter.doFilterInternal(requestIp1, blockedForIp1, filterChain);
        assertThat(blockedForIp1.getStatus()).isEqualTo(429);

        MockHttpServletResponse okForIp2 = new MockHttpServletResponse();
        filter.doFilterInternal(requestIp2, okForIp2, filterChain);

        verify(filterChain, times(1)).doFilter(eq(requestIp2), any());
    }

    @Test
    void doesNotLimitPathsOutsideLoginAndRegister() throws Exception {
        HttpServletRequest request = requestFor("/api/v1/auth/refresh", "10.0.0.4");

        for (int i = 0; i < 20; i++) {
            filter.doFilterInternal(request, new MockHttpServletResponse(), filterChain);
        }

        verify(filterChain, times(20)).doFilter(eq(request), any());
    }

    private HttpServletRequest requestFor(String uri, String ip) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn(uri);
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn(ip);
        return request;
    }
}
