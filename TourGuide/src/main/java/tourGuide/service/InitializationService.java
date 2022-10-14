package tourGuide.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.LocationTourGuide;
import tourGuide.model.VisitedLocationTourGuide;
import tourGuide.user.User;
import tourGuide.user.UserPreferences;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

/**
 * Class for internal testing initialization
 */
public class InitializationService {

    private final Logger logger = LoggerFactory.getLogger(InitializationService.class);

    /**
     * Initialize internal users for testing purposes
     */
    public Map<String, User> initializeInternalUsers() {
        Map<String, User> internalUserMap = new HashMap<>();

        IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
            String userName = "internalUser" + i;
            String phone = "000";
            String email = userName + "@tourGuide.com";
            User user = new User(UUID.randomUUID(), userName, phone, email);
            generateUserLocationHistory(user);
            generateUserPreference(user);

            internalUserMap.put(userName, user);
        });
        logger.debug("Created " + InternalTestHelper.getInternalUserNumber() + " internal test users.");

        return internalUserMap;
    }

    /**
     * Generate userLocation history for testing purposes
     *
     * @param user
     */
    private void generateUserLocationHistory(User user) {
        IntStream.range(0, 3).forEach(i -> {
            user.addToVisitedLocations(new VisitedLocationTourGuide(user.getUserId(), new LocationTourGuide(generateRandomLatitude(), generateRandomLongitude()), getRandomTime()));
        });
    }

    private void generateUserPreference(User user) {
        user.setUserPreferences(new UserPreferences(1, "USD", 5, 2, 2, 3));
    }

    /**
     * Generate Random Longitude
     *
     * @return longitude
     */
    private double generateRandomLongitude() {
        double leftLimit = -180;
        double rightLimit = 180;
        return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
    }

    /**
     * Generate Random Latitude
     *
     * @return latitude
     */
    private double generateRandomLatitude() {
        double leftLimit = -85.05112878;
        double rightLimit = 85.05112878;
        return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
    }

    /**
     * Generate Random Time
     *
     * @return time
     */
    private Date getRandomTime() {
        LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
        return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
    }
}
