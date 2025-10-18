package in.chapparapuvinay.carrentalsystem.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerResponse {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String dob;
    private String phno;
    private String aadharno;
    private String image;
    private String dlURL;
}
