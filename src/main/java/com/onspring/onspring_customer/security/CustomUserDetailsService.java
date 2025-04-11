package com.onspring.onspring_customer.security;

import com.onspring.onspring_customer.domain.common.entity.PlatformAdmin;
import com.onspring.onspring_customer.domain.common.repository.PlatformAdminRepository;
import com.onspring.onspring_customer.domain.customer.entity.Admin;
import com.onspring.onspring_customer.domain.customer.repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@Log4j2
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;
    private final PlatformAdminRepository platformAdminRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // Admin 탐색
        Admin admin = adminRepository.findByUserName(username);
        log.info(admin);
        if (admin != null) {
            return new CustomUserDetails(
                    admin.getId(),
                    admin.getUserName(),
                    admin.getPassword(),
                    "ROLE_ADMIN",
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
            );
        }

        // PlatformAdmin 먼저 탐색
        PlatformAdmin platformAdmin = platformAdminRepository.findByUserName(username);
        if (platformAdmin != null) {
            return new CustomUserDetails(
                    platformAdmin.getId(),
                    platformAdmin.getUserName(),
                    platformAdmin.getPassword(),
                    "ROLE_PLATFORM_ADMIN",
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_PLATFORM_ADMIN"))
            );
        }

        throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
    }
}