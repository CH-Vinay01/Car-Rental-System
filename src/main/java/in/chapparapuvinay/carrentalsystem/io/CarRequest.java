package in.chapparapuvinay.carrentalsystem.io;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarRequest {

    private String name;
    private String model;
    private double price;
    private int seats;
}
