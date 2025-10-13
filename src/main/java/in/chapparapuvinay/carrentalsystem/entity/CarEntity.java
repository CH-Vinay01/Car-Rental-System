package in.chapparapuvinay.carrentalsystem.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Cars")
public class CarEntity {
    @Id
    private String id;
    private String name;
    private String model;
    private double price;
    private int seats;
    private String imageUrl;
}
