package com.rinhack.Wrapper_quick_reports.services;

import com.rinhack.Wrapper_quick_reports.models.User;
import com.rinhack.Wrapper_quick_reports.models.UserDetailsImpl;
import com.rinhack.Wrapper_quick_reports.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository repository){
        this.userRepository = repository;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("User not found with username: " + username)
        );
        return UserDetailsImpl.build(user);
    }
}
