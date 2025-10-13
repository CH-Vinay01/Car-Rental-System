package in.chapparapuvinay.carrentalsystem.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CarResponse {
    private String id;
    private String name;
    private String model;
    private double price;
    private int seats;
    private String imageUrl;
}
