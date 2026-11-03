package pl.cinkus.messages;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendMobileNotificationMessage {
    private UUID outboxId;
    private String fcmToken;
    private String word;
    private String translation;
    private String dateTime;
}
