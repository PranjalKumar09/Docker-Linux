package com.ecom.config;

import com.ecom.entity.UserDtls;
import com.ecom.repository.UserReposistory;
import com.ecom.service.UserService;
import com.ecom.util.AppConstant;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthFailtureHandlerImpl extends SimpleUrlAuthenticationFailureHandler {
    @Autowired
    private UserReposistory userReposistory;
    @Autowired
    private UserService userService;

    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String email = request.getParameter("username");
        UserDtls userDtls = userReposistory.findByEmail(email);

        if (userDtls != null) {
            if (userDtls.getIsEnable()) {
                if (userDtls.getAccountNonLocked()) {
                    if (userDtls.getFailedAttempt() < AppConstant.ATTEMT_TIME - 1) {
                        userService.increaseFailedAttempt(userDtls);
                    } else {
                        userService.userAccountLock(userDtls);
                        exception = new LockedException("Your account is locked after failed attempt " + AppConstant.ATTEMT_TIME);
                    }
                } else {
                    if (userService.unlockAccountTimeExpired(userDtls)) {
                        exception = new LockedException("Your account is unlocked || Please try to login");
                    } else {
                        exception = new LockedException("Your account is Locked || Please try after sometimes");

                    }
                }
            } else {
                exception = new LockedException("Your account is Inactive");
            }
        }
        else {
            exception = new LockedException("Incorrect email");
        }
        super.setDefaultFailureUrl("/sginIn?error");
        super.onAuthenticationFailure(request, response, exception);
    }

}
