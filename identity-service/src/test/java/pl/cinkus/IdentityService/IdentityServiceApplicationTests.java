package pl.cinkus.IdentityService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import pl.cinkus.IdentityService.entity.RefreshToken;
import pl.cinkus.IdentityService.service.RefreshTokenService;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class IdentityServiceApplicationTests {
	@Autowired
	RefreshTokenService refreshTokenService;

	@Test
	void contextLoads() {
	}

//	Assert that refresh token is built properly
	@Test
	void generateAndCheckRefreshToken() {
		RefreshToken refreshToken = refreshTokenService.generateRefreshToken(UUID.randomUUID());

		System.out.println(refreshToken);
		assertNotNull(refreshToken);
		assertNotNull(refreshToken.getRefreshToken());
		assertNotNull(refreshToken.getExpirationDate());
		assertNotNull(refreshToken.getRefreshTokenId());
		assertNotNull(refreshToken.getOwnerId());

	}

}
