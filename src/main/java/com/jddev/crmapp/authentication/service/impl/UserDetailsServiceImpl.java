package com.jddev.crmapp.authentication.service.impl;

import com.jddev.crmapp.authentication.model.AppUser;
import com.jddev.crmapp.authentication.model.UserPrincipal;
import com.jddev.crmapp.authentication.repository.UserRepository;
import com.jddev.crmapp.exception.UserNotFoundException;
import com.jddev.crmapp.utility.db.DbService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final DbService dbService;

    public UserDetailsServiceImpl(DbService dbService) {
        this.dbService = dbService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = dbService.executeCustomMethod(UserRepository.class, repo -> ((UserRepository) repo).findByEmail(username))
                .orElseThrow(() -> new UserNotFoundException("Nem található felhasználó!"));

        return new UserPrincipal(user);
    }
}
