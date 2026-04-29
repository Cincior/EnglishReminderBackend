package pl.cinkus.IdentityService.mapper;

import org.springframework.stereotype.Component;
import pl.cinkus.IdentityService.dto.UserDataDTO;
import pl.cinkus.backend.codegen.types.UserData;

@Component
public class UserDataMapper {
    public UserDataDTO toUserDataDTO(UserData userData) {
        return UserDataDTO.builder()
                .name(userData.getName())
                .surname(userData.getSurname())
                .nickName(userData.getNickName())
                .email(userData.getEmail())
                .password(userData.getPassword())
                .build();
    }
}
