package pl.cinkus.messages;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendMobileNotificationMessage {
    private String word;
    private String translation;
    private String dateTime;
}
