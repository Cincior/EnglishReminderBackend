package pl.cinkus.IdentityService.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import pl.cinkus.IdentityService.service.IdentityService;
import pl.cinkus.backend.codegen.types.UserData;

import java.util.List;

@Controller
@RequiredArgsConstructor

public class IdentityDataFetcher {
    private final IdentityService identityService;

    @MutationMapping
    public boolean createUser(@Argument UserData userData) {
        identityService.createUser(userData);
        return true;
    }

    @QueryMapping
    public List<String> getAllUsers() {
        return identityService.getAllUsers();
    }
}
