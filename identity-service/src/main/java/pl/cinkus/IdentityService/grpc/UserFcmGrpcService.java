package pl.cinkus.IdentityService.grpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import org.springframework.grpc.server.service.GrpcService;
import pl.cinkus.IdentityService.service.IdentityService;
import pl.cinkus.grpc.GetUserFcmTokenResponse;
import pl.cinkus.grpc.GetUserFcmTokenRequest;
import pl.cinkus.grpc.UserFcmServiceGrpc;

import java.util.UUID;

@GrpcService
@RequiredArgsConstructor
public class UserFcmGrpcService extends UserFcmServiceGrpc.UserFcmServiceImplBase {
    private final IdentityService identityService;

    @Override
    public void getUserFcmToken(
            GetUserFcmTokenRequest request,
            StreamObserver<GetUserFcmTokenResponse> responseObserver
    ) {
        try {
            UUID userId = UUID.fromString(request.getUserId());
            String fcmToken = identityService.getInstallationId(userId);

            if (fcmToken == null || fcmToken.isBlank()) {
                responseObserver.onError(
                        Status.NOT_FOUND.asRuntimeException()
                );
                return;
            }

            responseObserver.onNext(
                    GetUserFcmTokenResponse.newBuilder()
                            .setFcmToken(fcmToken)
                            .build());
            responseObserver.onCompleted();
        } catch (IllegalArgumentException exception) {
            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription("Invalid user ID")
                            .asRuntimeException()
            );
        }
    }
}