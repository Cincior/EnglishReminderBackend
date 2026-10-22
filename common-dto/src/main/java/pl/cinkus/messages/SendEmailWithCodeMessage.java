package pl.cinkus.messages;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendEmailWithCodeMessage {
    private String email;
    private EmailType emailType;
    private String code;
}
