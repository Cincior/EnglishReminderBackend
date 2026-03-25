package pl.cinkus.IdentityService.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.cinkus.IdentityService.exception.ErrorCode;
import pl.cinkus.IdentityService.exception.IdentityServiceException;
import pl.cinkus.IdentityService.model.User;
import pl.cinkus.IdentityService.repository.UserRepository;
import pl.cinkus.backend.codegen.types.UserData;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IdentityService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void createUser(UserData userData) {
        if(userRepository.existsByNickNameOrEmail(userData.getNickName(), userData.getEmail())) {
            throw new IdentityServiceException(ErrorCode.EMAIL_OR_NICKNAME_ALREADY_TAKEN, "User already exists!");
        }

        userRepository.save(User.builder()
                .id(UUID.randomUUID())
                .name(userData.getName())
                .surname(userData.getSurname())
                .nickName(userData.getNickName())
                .email(userData.getEmail())
                .password(passwordEncoder.encode(userData.getPassword()))
                .build());
    }

    public List<String> getAllUsers() {
        return userRepository.findAll().stream().map(User::getEmail).toList();
    }
}
