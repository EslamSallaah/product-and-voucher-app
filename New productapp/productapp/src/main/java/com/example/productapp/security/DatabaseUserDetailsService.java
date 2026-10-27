package com.example.productapp.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseUserDetailsService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        List<UserRoleRow> rows = jdbcTemplate.query(
            """
                SELECT u.email, u.password, r.name AS role_name
                FROM `user` u
                JOIN user_role ur ON ur.user_id = u.id
                JOIN role r ON r.id = ur.role_id
                WHERE LOWER(u.email) = LOWER(?)
                """,
            (resultSet, rowNumber) -> new UserRoleRow(
                resultSet.getString("email"),
                resultSet.getString("password"),
                resultSet.getString("role_name")),
            email.trim());

        if (rows.isEmpty()) {
            throw new UsernameNotFoundException("No account found for this email");
        }

        UserRoleRow user = rows.get(0);
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (UserRoleRow row : rows) {
            authorities.add(new SimpleGrantedAuthority(normalizeRole(row.roleName())));
        }

        return new org.springframework.security.core.userdetails.User(
            user.email(),
            user.password(),
            authorities);
    }

    private String normalizeRole(String role) {
        String normalized = role.trim().toUpperCase(Locale.ROOT);
        return normalized.startsWith("ROLE_") ? normalized : "ROLE_" + normalized;
    }

    private record UserRoleRow(String email, String password, String roleName) {
    }
}
