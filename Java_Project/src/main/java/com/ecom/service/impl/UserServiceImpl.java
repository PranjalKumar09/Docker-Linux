package com.ecom.service.impl;

import com.ecom.entity.UserDtls;
import com.ecom.repository.UserReposistory;
import com.ecom.service.UserService;
import com.ecom.util.AppConstant;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserReposistory userReposistory;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDtls saveUser(UserDtls userDtls) {
        userDtls.setRole("ROLE_USER");
        userDtls.setIsEnable(true);
        userDtls.setAccountNonLocked(true);
        userDtls.setFailedAttempt(0);

        userDtls.setPassword(passwordEncoder.encode(userDtls.getPassword()));
        return userReposistory.save(userDtls);
    }

    @Override
    public UserDtls saveAdmin(UserDtls userDtls) {
        userDtls.setRole("ROLE_ADMIN");
        userDtls.setIsEnable(true);
        userDtls.setAccountNonLocked(true);
        userDtls.setFailedAttempt(0);

        userDtls.setPassword(passwordEncoder.encode(userDtls.getPassword()));
        return userReposistory.save(userDtls);
    }

    @Override
    public UserDtls getUserByEmail(String email) {
        return userReposistory.findByEmail(email);
    }

    @Override
    public List<UserDtls> getUsers() {
        return userReposistory.findByRole("ROLE_USER");
    }
    @Override
    public List<UserDtls> getAdmins() {
        return userReposistory.findByRole("ROLE_ADMIN");
    }

    @Override
    public Boolean updateAccountStatus(Integer id, Boolean status) {
        UserDtls userDtls = userReposistory.findById(id).orElse(null);

        if (userDtls != null) {
            userDtls.setIsEnable(status);
            userReposistory.save(userDtls);
            return true;
        }

        return false;


    }

    @Override
    public void updateUserResetToken(String email, String verificationCode) {
        UserDtls findByEmail = userReposistory.findByEmail(email);
        findByEmail.setVerificationCode(verificationCode);
        userReposistory.save(findByEmail);
    }

    @Override
    public UserDtls getUserByToken(String token) {
        return userReposistory.findByVerificationCode(token);
    }

    @Override
    public UserDtls updateUserProfile(UserDtls userDtls, MultipartFile file, HttpSession httpSession) {
        UserDtls existUserDtls = userReposistory.findById(userDtls.getId()).orElse(null);

        if (existUserDtls != null) {
            try {
                // Update basic user details
                existUserDtls.setFullName(userDtls.getFullName());
                existUserDtls.setMobile(userDtls.getMobile());
                existUserDtls.setAddress(userDtls.getAddress());
                existUserDtls.setCity(userDtls.getCity());
                existUserDtls.setState(userDtls.getState());
                existUserDtls.setPincode(userDtls.getPincode());

                // Handle file upload if provided
                if (!file.isEmpty()) {
                    String newFileName = file.getOriginalFilename();
                    existUserDtls.setProfileImage(newFileName);

                    // Save the file to the server
                    File saveFile = new File("src/main/resources/static/img/profile_img/" + newFileName);

                    Files.copy(file.getInputStream(), Paths.get(saveFile.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING);
                }

                // Save the updated user details in the database
                userReposistory.save(existUserDtls);

                // Set a success message in the HttpSession
                httpSession.setAttribute("succMsg", "Profile updated successfully!");
                return existUserDtls;
            } catch (IOException e) {
                e.printStackTrace();
                httpSession.setAttribute("errorMsg", "An error occurred while updating the profile.");
            }
        } else {
            // Set an error message if the user doesn't exist
            httpSession.setAttribute("errorMsg", "User not found.");
        }

        return null; // Return null if update fails
    }

    @Override
    public Boolean existsByEmail(String email) {
        return userReposistory.existsByEmail(email);
    }


    @Override
    public void increaseFailedAttempt(UserDtls user) {
        int attempt = user.getFailedAttempt() + 1;
        user.setFailedAttempt(attempt);
        userReposistory.save(user);
    }

    @Override
    public void userAccountLock(UserDtls user) {
        user.setAccountNonLocked(false);
        user.setLockTime(new Date());
        userReposistory.save(user);

    }
    @Override
    public boolean unlockAccountTimeExpired(UserDtls user) {
        if (user.getLockTime() == null) return true;

        long lockTime = user.getLockTime().getTime();
        long currentTime = System.currentTimeMillis();

        if (lockTime + AppConstant.UNLOCK_DURATION_TIME < currentTime) {
            user.setLockTime(null);
            user.setAccountNonLocked(true);
            user.setFailedAttempt(0);
            userReposistory.save(user);
            return true;
        }

        return false;
    }


    @Override
    public void resetAttempt(int userId) {
        UserDtls userDtls = userReposistory.findById(userId).orElse(null);
        if (userDtls != null) {
            userDtls.setFailedAttempt(0);
        }

    }
}
