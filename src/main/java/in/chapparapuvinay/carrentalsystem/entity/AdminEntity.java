package in.chapparapuvinay.carrentalsystem.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "AdminLogin")
public class AdminEntity {
    @Id
    private String id;
    private String username;
    private String password;
}
