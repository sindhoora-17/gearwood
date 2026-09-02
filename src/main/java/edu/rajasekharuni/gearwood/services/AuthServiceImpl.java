package edu.rajasekharuni.gearwood.services;

import edu.rajasekharuni.gearwood.dtos.RegisterDto;
import edu.rajasekharuni.gearwood.entities.Role;
import edu.rajasekharuni.gearwood.entities.User;
import edu.rajasekharuni.gearwood.repositories.RoleRepository;
import edu.rajasekharuni.gearwood.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @PreAuthorize("permitAll()")
    @Override
    public void registerUser(RegisterDto registerDto) {

        if (userRepository.findByEmail(registerDto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        if (!registerDto.getPassword().equals(registerDto.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        Role customerRole = roleRepository.findByName("ROLE_CUSTOMER").orElseThrow();

        User user = User.builder().firstName(registerDto.getFirstName()).lastName(registerDto.getLastName()).email(registerDto.getEmail()).password(passwordEncoder.encode(registerDto.getPassword())).role(customerRole).build();

        userRepository.save(user);
    }
}