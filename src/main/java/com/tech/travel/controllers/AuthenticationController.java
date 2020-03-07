package com.tech.travel.controllers;

import com.tech.travel.api.requests.LoginRequest;
import com.tech.travel.api.requests.SignupRequest;
import com.tech.travel.api.responses.JwtResponse;
import com.tech.travel.api.responses.MessageResponse;
import com.tech.travel.models.Role;
import com.tech.travel.models.User;
import com.tech.travel.repository.RoleRepository;
import com.tech.travel.repository.UserRepository;
import com.tech.travel.security.JwtTokenService;
import com.tech.travel.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final Logger log =  LoggerFactory.getLogger(AuthenticationController.class);

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenService jwtTokenService;

    @PostMapping("/sign-up")
    public ResponseEntity<?> registerUser(HttpServletRequest request,
                                          @Valid @RequestBody SignupRequest signUpRequest) {
        final String transactionId = request.getHeader("X-Transaction-Id");

        logInfoWithTransactionId(transactionId,"got new request to sign-up user");
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        logInfoWithTransactionId(transactionId,"creating new user");
        User user = new User(
                signUpRequest.getUsername(),
                passwordEncoder.encode(signUpRequest.getPassword()),
                true
        );

        Set<String> strRoles = signUpRequest.getRoles();
        logInfoWithTransactionId(transactionId, String.format("save roles: %s", strRoles));
        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            Role userRole = roleRepository.findByName(Role.ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
            logInfoWithTransactionId(transactionId,String.format("user got role: %s", userRole.getName()));
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(Role.ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        logInfoWithTransactionId(transactionId,"user got role ADMIN");
                        break;
                    case "operations":
                        Role opsRole = roleRepository.findByName(Role.ERole.ROLE_OPS)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(opsRole);
                        logInfoWithTransactionId(transactionId,"user got role OPS");
                        break;
                    default:
                        Role userRole = roleRepository.findByName(Role.ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                        logInfoWithTransactionId(transactionId,"user got role USER");
                }
            });
        }
        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(HttpServletRequest request,
                                              @Valid @RequestBody LoginRequest loginRequest) {
        final String transactionId = request.getHeader("X-Transaction-Id");
        logInfoWithTransactionId(
                transactionId,
                String.format("got new request to login user %s, %s", loginRequest.getUsername(), loginRequest.getPassword())
        );

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserService userDetails = (UserService) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        logInfoWithTransactionId(transactionId, "user authenticated, generating access token");
        final String jwt = jwtTokenService.generateToken(userDetails, roles, true);

        return ResponseEntity.ok(
                new JwtResponse(
                        jwt,
                        userDetails.getId(),
                        userDetails.getUsername(),
                        roles
                )
        );
    }

    private void logInfoWithTransactionId(String transactionId, String message) {
        log.info(String.format("[LOCATIONS] %s: %s", transactionId, message));
    }

}
