package com.ecom.service;

import com.ecom.entity.UserDtls;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public interface UserService {
    UserDtls saveUser(UserDtls userDtls);
    UserDtls saveAdmin(UserDtls userDtls);
    UserDtls getUserByEmail(String email);
    List<UserDtls> getUsers();
    List<UserDtls> getAdmins();
    Boolean updateAccountStatus(Integer id, Boolean status);

    void increaseFailedAttempt(UserDtls user);
    void userAccountLock(UserDtls user);
    boolean unlockAccountTimeExpired(UserDtls user);
    void resetAttempt(int userId);
    void updateUserResetToken(String email, String verificationCode);
    UserDtls getUserByToken(String token);
    UserDtls updateUserProfile(UserDtls userDtls, MultipartFile file, HttpSession httpSession);
    Boolean existsByEmail(String email);
}
