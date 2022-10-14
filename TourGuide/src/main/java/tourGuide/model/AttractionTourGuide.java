package tourGuide.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Attraction POJO
 */
@Getter
@Setter
public class AttractionTourGuide extends LocationTourGuide {
    private String attractionName;
    private String city;
    private String state;
    private UUID attractionId;

    public AttractionTourGuide()
    {

    }

    public AttractionTourGuide(String attractionName, String city, String state, double latitude, double longitude) {
        super(latitude, longitude);
        this.attractionName = attractionName;
        this.city = city;
        this.state = state;
        this.attractionId = UUID.randomUUID();
    }
}
