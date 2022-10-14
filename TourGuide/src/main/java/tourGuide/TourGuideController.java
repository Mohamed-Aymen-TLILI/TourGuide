package tourGuide;

import com.jsoniter.output.JsonStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tourGuide.model.Provider;
import tourGuide.model.VisitedLocationTourGuide;
import tourGuide.service.TourGuideService;
import tourGuide.user.User;
import tourGuide.user.UserPreferences;

import java.util.List;

/**
 * Tour Guide Controller
 */
@RestController
public class TourGuideController {

    @Autowired
    TourGuideService tourGuideService;

    /**
     * Home endpoint for TourGuide Application
     *
     * @return
     */
    @RequestMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }

    /**
     * Get location for specific user
     *
     * @param userName username
     * @return Last visited location for user
     * @throws Exception
     */
    @RequestMapping("/getLocation")
    public String getLocation(@RequestParam String userName) {
        VisitedLocationTourGuide visitedLocationTourGuide = tourGuideService.getUserLocation(userName);
        return JsonStream.serialize(visitedLocationTourGuide.getLocationTourGuide());
    }

    /**
     * Get the closest five touristics attractions to the user - no matter how far away they are.
     *
     * @param userName username
     * @return the closest five tourist attractions to the user - no matter how far away they are.
     */
    @RequestMapping("/getNearbyAttractions")
    public String getNearbyAttractions(@RequestParam String userName) {
        VisitedLocationTourGuide visitedLocationTourGuide = tourGuideService.getUserLocation(userName);
        return JsonStream.serialize(tourGuideService.getNearByAttractions(visitedLocationTourGuide));
    }

    /**
     * Get rewards list for specific user
     *
     * @param userName username
     * @return list of rewards
     */
    @RequestMapping("/getRewards")
    public String getRewards(@RequestParam String userName) {
        return JsonStream.serialize(tourGuideService.getUserRewards(userName));
    }

    /**
     * Get all current locations for all users
     *
     * @return List of current locations for all users
     */
    @RequestMapping("/getAllCurrentLocations")
    public String getAllCurrentLocations() {

        //- Note: does not use gpsUtil to query for their current location,
        //        but rather gathers the user's current location from their stored location history.
        //
        // Return object should be the just a JSON mapping of userId to Locations similar to:
        //     {
        //        "019b04a9-067a-4c76-8817-ee75088c3822": {"longitude":-48.188821,"latitude":74.84371}
        //        ...
        //     }

        return JsonStream.serialize(tourGuideService.getAllUsersCurrentLocation());
    }

    /**
     * get Trip Deals for specific user
     *
     * @param userName username
     * @return trip deals for this user
     */
    @RequestMapping("/getTripDeals")
    public String getTripDeals(@RequestParam String userName) {
        List<Provider> providers = tourGuideService.getTripDeals(userName);
        return JsonStream.serialize(providers);
    }

    @RequestMapping("/users")
    public List<User> getAllUsers()
    {
        return tourGuideService.getAllUsers();
    }

    @PostMapping("/userPreferences")
    public UserPreferences setUSerPreferences(@RequestParam String userName, @RequestBody UserPreferences userPreferences)
    {
        return tourGuideService.setUserPreferences(userName,userPreferences);
    }
}
