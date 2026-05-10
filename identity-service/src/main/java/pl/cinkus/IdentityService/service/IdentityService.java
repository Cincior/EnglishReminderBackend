package pl.cinkus.IdentityService.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.cinkus.IdentityService.exception.ErrorCode;
import pl.cinkus.IdentityService.exception.IdentityServiceException;
import pl.cinkus.IdentityService.mapper.UserDataMapper;
import pl.cinkus.IdentityService.model.User;
import pl.cinkus.IdentityService.repository.UserRepository;
import pl.cinkus.IdentityService.validation.UserRegistrationValidation;
import pl.cinkus.backend.codegen.types.NewUserData;
import pl.cinkus.backend.codegen.types.UserLoginData;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdentityService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRegistrationValidation validation;
    private final JwtService jwtService;
    private final UserDataMapper userDataMapper;

    @Transactional
    public void registerUser(NewUserData userData) {
        if(userRepository.existsByNickNameOrEmail(userData.getNickName(), userData.getEmail())) {
            throw new IdentityServiceException(ErrorCode.EMAIL_OR_NICKNAME_ALREADY_TAKEN, "User already exists!");
        }

        validation.validate(userData);

        userRepository.save(User.builder()
                .id(UUID.randomUUID())
                .name(userData.getName())
                .surname(userData.getSurname())
                .nickName(userData.getNickName())
                .role(userDataMapper.toUserRole(userData.getRole()))
                .email(userData.getEmail())
                .password(passwordEncoder.encode(userData.getPassword()))
                .build());
    }

    public String login(UserLoginData userLoginData) {
        User user = userRepository.findByEmail(userLoginData.getEmail())
                .orElseThrow(() -> new IdentityServiceException(ErrorCode.USER_NOT_FOUND, "User with given email does not exists!"));

        if (!passwordEncoder.matches(userLoginData.getPassword(), user.getPassword())) {
            throw new IdentityServiceException(ErrorCode.INVALID_PASSWORD, "Invalid password!");
        }

        String token = jwtService.generateToken(user.getId(), user.getRole());

        log.info("Token {}", token);
        return token;

    }

    public List<String> getAllUsers() {
        return userRepository.findAll().stream().map(User::getEmail).toList();
    }
}
