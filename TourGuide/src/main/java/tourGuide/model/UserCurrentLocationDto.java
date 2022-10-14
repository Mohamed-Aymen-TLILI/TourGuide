package tourGuide.model;

/**
 * DTO regroupant les données des localisations récentes d'un utilisateur
 */
public class UserCurrentLocationDto {
    private String userId;

    private LocationTourGuide userLocationTourGuide;

    /**
     * Construction with properties
     * @param userId user id
     * @param locationTourGuide location of user
     */
    public UserCurrentLocationDto(String userId, LocationTourGuide locationTourGuide) {
        this.userId = userId;
        this.userLocationTourGuide = locationTourGuide;
    }
}
