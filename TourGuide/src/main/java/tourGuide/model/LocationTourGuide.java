package tourGuide.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Location POJO
 */
@Getter
@Setter
public class LocationTourGuide {

    private double longitude;
    private double latitude;

    public LocationTourGuide()
    {

    }
    public LocationTourGuide(double latitude, double longitude) {
        this.setLatitude(latitude);
        this.setLongitude(longitude);
    }
}

