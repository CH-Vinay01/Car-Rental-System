package in.chapparapuvinay.carrentalsystem.io;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SmsVerifyRequest {
    private String phoneNumber;
    private String otp;

}
