package pl.cinkus.notification_service.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.cinkus.messages.SendMobileNotificationMessage;
import pl.cinkus.notification_service.client.IdentityGrpcClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class MobileNotificationService {
    private final FirebaseMessaging firebaseMessaging;
    private final IdentityGrpcClient identityGrpcClient;

    public void sendNotification(SendMobileNotificationMessage message) {
        try {
            String fcmToken = identityGrpcClient.getFcmToken(message.getUserId());
            log.warn("FCM:{}", fcmToken);
            String responseId = firebaseMessaging.send(
                    createMessage(message, fcmToken)
            );
            log.info("Notification sent: {}", responseId);
        } catch (FirebaseMessagingException exception) {
            log.error("Firebase notification failed", exception);
        } catch (Exception exception) {
            log.error("Unknown notification exception", exception);
        }
    }

    private Message createMessage(SendMobileNotificationMessage message, String fcmToken) {
        return Message.builder()
                .setFid(fcmToken)
                .setNotification(
                        Notification.builder()
                                .setTitle(message.getWord())
                                .setBody(message.getTranslation())
                                .build()
                )
                .putData("wordId", message.getWordId().toString())
                .build();
    }
}
