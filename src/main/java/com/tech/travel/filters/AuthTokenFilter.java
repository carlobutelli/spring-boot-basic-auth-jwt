package com.tech.travel.filters;

import com.tech.travel.filters.wrapper.HeaderRequestWrapper;
import com.tech.travel.security.JwtTokenService;
import com.tech.travel.services.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtTokenService jwtUtils;

    @Autowired
    private UserServiceImpl userDetailsService;

    private static final Logger log = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        final String transactionId = generateTransactionId();

        HeaderRequestWrapper wrapperRequest = new HeaderRequestWrapper(request);
        wrapperRequest.addHeader("X-Transaction-Id", transactionId);

        try {
            if((request).getMethod().equals("OPTIONS")) {
                filterChain.doFilter(wrapperRequest, response);
                return;
            }
            logInfoWithTransactionId(transactionId, "processing filter");
            String jwt = parseJwt(request);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (UsernameNotFoundException e) {
            logErrorWithTransactionId(transactionId,"user not found");
            throw new UsernameNotFoundException("user not found");
        } catch (Exception e) {
            logErrorWithTransactionId(transactionId,"Internal Server Error on set user authentication");
            throw new RuntimeException(e);
        }

        filterChain.doFilter(wrapperRequest, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.split(" ")[1];
        }

        return null;
    }

    private void logInfoWithTransactionId(String transactionId, String message) {
        log.info(String.format("[FILTER] %s: %s", transactionId, message));
    }

    private void logErrorWithTransactionId(String transactionId, String message) {
        log.error(String.format("[FILTER] %s: %s", transactionId, message));
    }

    private String generateTransactionId() {
        return UUID.randomUUID().toString();
    }
}
