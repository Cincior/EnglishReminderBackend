package pl.cinkus.IdentityService.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import pl.cinkus.IdentityService.mapper.UserDataMapper;
import pl.cinkus.IdentityService.service.IdentityService;
import pl.cinkus.backend.codegen.types.LoginResult;
import pl.cinkus.backend.codegen.types.NewUserData;
import pl.cinkus.backend.codegen.types.UserLoginData;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor

public class IdentityDataFetcher {
    private final IdentityService identityService;
    private final UserDataMapper userDataMapper;

    @MutationMapping
    public boolean register(@Argument NewUserData userData) {
        identityService.registerUser(userData);
        return true;
    }

    @MutationMapping
    public LoginResult login(@Argument UserLoginData userLoginData) {
        return identityService.login(userLoginData);
    }

    @MutationMapping
    public LoginResult refresh(@Argument String refreshToken) {
        LoginResult lr = identityService.refreshToken(refreshToken);
        log.info("lr: {}", lr);
        return lr;
    }

    @QueryMapping
    public List<String> getAllUsers() {
        return identityService.getAllUsers();
    }
}
