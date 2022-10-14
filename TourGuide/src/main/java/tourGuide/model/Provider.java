package tourGuide.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Provider {
    private String name;
    private double price;
    private UUID tripId;

    public Provider()
    {}

    public Provider(UUID tripId, String name, double price) {
        this.name = name;
        this.tripId = tripId;
        this.price = price;
    }
}
