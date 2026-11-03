package pl.cinkus.notification_service.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.cinkus.messages.SendMobileNotificationMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class MobileNotificationService {
    private final FirebaseMessaging firebaseMessaging;

    public void sendNotification(SendMobileNotificationMessage message) {
        try {
            String res = firebaseMessaging.send(createMessage(message));
            log.info("Not::: {}", res);
        } catch (FirebaseMessagingException e ) {
            log.error("errNot", e);
        } catch (Exception e) {
            log.error("Unknown exception in notification");
        }
    }

    private Message createMessage(SendMobileNotificationMessage message) {
        return Message.builder()
                .setToken(message.getFcmToken())
                .setNotification(
                        Notification.builder()
                                .setTitle(message.getWord())
                                .setBody(message.getTranslation())
                                .build()
                )
                .build();
    }
}
