package com.ecom.config;

import com.ecom.entity.UserDtls;
import com.ecom.repository.UserReposistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private UserReposistory userReposistory;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDtls userDtls = userReposistory.findByEmail(username);
        if (userDtls == null) {
            throw new UsernameNotFoundException("User not Fount");
        }
        return new CustomUser(userDtls) ;
    }



}
