package com.esmartdie.EsmartCafeteriaApi.controller.user;


import com.esmartdie.EsmartCafeteriaApi.model.user.User;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IUserLogsRepository;
import com.esmartdie.EsmartCafeteriaApi.repository.user.IUserRepository;
import com.esmartdie.EsmartCafeteriaApi.service.user.IUserLogsService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class LogoutController{

    @Autowired
    private IUserLogsService userLogsService;

    @Autowired
    private IUserRepository userRepository;

    @PostMapping("/logout")
    public ResponseEntity<?> logout(Authentication authentication) {
        if (authentication != null) {
            User user = userRepository.findByEmail(String.valueOf(authentication.getPrincipal())).get();
            userLogsService.createUserLogoutLog(user);
            SecurityContextHolder.clearContext();
            return ResponseEntity.ok().body("User logged out successfully.");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No authenticated user to log out.");
    }
}
