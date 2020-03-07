package com.tech.travel.controllers;

import com.tech.travel.api.responses.BaseResponse;
import com.tech.travel.models.User;
import com.tech.travel.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Logger log = LoggerFactory.getLogger(AuthenticationController.class);

    @Autowired
    UserRepository userRepository;

    @GetMapping("/all")
    public ResponseEntity allAccess(HttpServletRequest request) {
        final String transactionId = request.getHeader("X-Transaction-Id");
        logInfoWithTransactionId(transactionId, "got new request to get all users");
        List<User> userList = userRepository.findAll();
        return new ResponseEntity<>(userList, HttpStatus.OK);
    }

    @GetMapping("/{username}")
    @PreAuthorize("hasRole('USER') or hasRole('OPS') or hasRole('ADMIN')")
    public ResponseEntity userAccess(HttpServletRequest request,
                                     @PathVariable("username") @NotEmpty @NotNull String username) {
        final String transactionId = request.getHeader("X-Transaction-Id");
        try {
            logInfoWithTransactionId(transactionId, String.format("got new request to fetch user %s", username));
            User user = userRepository
                    .findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("user not found with username: " + username));
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (UsernameNotFoundException e) {
            logErrorWithTransactionId(transactionId, String.format("username %s not found", username));
            return new ResponseEntity<>(
                    new BaseResponse(
                            "ERROR",
                            transactionId,
                            String.format("username %s not found", username),
                            HttpStatus.NOT_FOUND.value()
                    ), HttpStatus.NOT_FOUND
            );
        }
    }

    @GetMapping("/test/ops")
    @PreAuthorize("hasRole('OPS')")
    public String opsAccess(HttpServletRequest request) {
        final String transactionId = request.getHeader("X-Transaction-Id");
        logInfoWithTransactionId(transactionId, "got new request to access ops board");

        return "Ops Board.";
    }

    @GetMapping("/test/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess(HttpServletRequest request) {
        final String transactionId = request.getHeader("X-Transaction-Id");
        logInfoWithTransactionId(transactionId, "got new request to access admin board");
        return "Admin Board.";
    }

    private void logInfoWithTransactionId(String transactionId, String message) {
        log.info(String.format("[USERS] %s: %s", transactionId, message));
    }

    private void logErrorWithTransactionId(String transactionId, String message) {
        log.error(String.format("[USERS] %s: %s", transactionId, message));
    }

}
