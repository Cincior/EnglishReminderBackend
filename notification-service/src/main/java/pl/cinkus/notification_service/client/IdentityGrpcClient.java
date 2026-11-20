package pl.cinkus.notification_service.client;

import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.stereotype.Component;
import pl.cinkus.grpc.GetUserFcmTokenRequest;
import pl.cinkus.grpc.GetUserFcmTokenResponse;
import pl.cinkus.grpc.UserFcmServiceGrpc;

import java.util.UUID;

@Component
public class IdentityGrpcClient {
    private final UserFcmServiceGrpc.UserFcmServiceBlockingStub identityStub;

    public IdentityGrpcClient(GrpcChannelFactory channelFactory) {
        this.identityStub = UserFcmServiceGrpc.newBlockingStub(
                channelFactory.createChannel("identity")
        );
    }

    public String getFcmToken(UUID userId) {
        GetUserFcmTokenResponse response =
                identityStub.getUserFcmToken(
                        GetUserFcmTokenRequest.newBuilder()
                                .setUserId(userId.toString())
                                .build()
                );

        return response.getFcmToken();
    }
}
