package tourGuide.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tourGuide.model.AttractionClosestDto;
import tourGuide.model.AttractionTourGuide;
import tourGuide.model.NearbyAttractionDto;
import tourGuide.model.Provider;
import tourGuide.model.UserCurrentLocationDto;
import tourGuide.model.VisitedLocationTourGuide;
import tourGuide.service.gpsUtil.IGpsUtilService;
import tourGuide.service.tripPricer.ITripPricerService;
import tourGuide.tracker.Tracker;
import tourGuide.user.User;
import tourGuide.user.UserPreferences;
import tourGuide.user.UserReward;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * TourGuide Service
 */
public class TourGuideService {
	private final Logger logger = LoggerFactory.getLogger(TourGuideService.class);
	private final IGpsUtilService gpsUtilService;
	private final RewardsService rewardsService;
	private final ITripPricerService tripPricerService;
	public final Tracker tracker;
	boolean testMode = true;

	private static final String TRIP_PRICER_API_KEY = "test-server-api-key";
	// Database connection will be used for external users, but for testing purposes internal users are provided and stored in memory
	private Map<String, User> internalUserMap = new HashMap<>();


	/**
	 * Constructor with necessary service
	 *
	 * @param gpsUtilService    gpsUtil Service
	 * @param rewardsService    rewards Service
	 * @param tripPricerService tripPricer service
	 */
	public TourGuideService(IGpsUtilService gpsUtilService, RewardsService rewardsService, ITripPricerService tripPricerService, InitializationService initializationService) {
		this.gpsUtilService = gpsUtilService;
		this.rewardsService = rewardsService;
		this.tripPricerService = tripPricerService;
		Locale.setDefault(Locale.US);
		if (testMode) {
			logger.info("TestMode enabled");
			logger.debug("Initializing users");
			internalUserMap = initializationService.initializeInternalUsers();
			logger.debug("Finished initializing users");
		}
		tracker = new Tracker(this);

		addShutDownHook();
	}

	/**
	 * Get userReward list
	 *
	 * @param username user
	 * @return list of userReward
	 */
	public List<UserReward> getUserRewards(String username) {
		return this.getUser(username).getUserRewards();
	}

	/**
	 * get last user visited location
	 *
	 * @param username username
	 * @return last user visited location
	 */
	public VisitedLocationTourGuide getUserLocation(String username) {
		User user = this.getUser(username);
		return (!user.getVisitedLocations().isEmpty()) ?
				user.getLastVisitedLocation() :
				trackUserLocation(user);
	}

	/**
	 * get user by username
	 *
	 * @param userName username
	 * @return user
	 */
	public User getUser(String userName) {
		return internalUserMap.get(userName);
	}

	/**
	 * get ALl users
	 *
	 * @return list of users
	 */
	public List<User> getAllUsers() {
		return internalUserMap.values().stream().collect(Collectors.toList());
	}

	/**
	 * add user
	 *
	 * @param user user to add
	 */
	public void addUser(User user) {
		if (!internalUserMap.containsKey(user.getUserName())) {
			internalUserMap.put(user.getUserName(), user);
		}
	}

	/**
	 * set UserPreferences to given user
	 *
	 * @param username        username
	 * @param userPreferences userPreferences
	 * @return userPreferences or null if user doesn't exist
	 */
	public UserPreferences setUserPreferences(String username, UserPreferences userPreferences) {
		User user = getUser(username);

		if (user != null) {
			user.setUserPreferences(userPreferences);
			return user.getUserPreferences();
		}
		return null;

	}

	/**
	 * get trip deals for user
	 *
	 * @param username username
	 * @return provider list for trip deals
	 */
	public List<Provider> getTripDeals(String username) {

		User user = this.getUser(username);

		int cumulativeRewardPoints = user.getUserRewards().stream().mapToInt(UserReward::getRewardPoints).sum();

		List<Provider> providers = tripPricerService.getPrice(TRIP_PRICER_API_KEY, user.getUserId(), user.getUserPreferences().getNumberOfAdults(),
				user.getUserPreferences().getNumberOfChildren(), user.getUserPreferences().getTripDuration(), cumulativeRewardPoints);
		user.setTripDeals(providers);
		return providers;

	}

	/**
	 * Track user location
	 *
	 * @param user user to track
	 * @return last visitedLocation
	 */
	public VisitedLocationTourGuide trackUserLocation(User user) {

		VisitedLocationTourGuide visitedLocationTourGuide = gpsUtilService.getUserLocation(user.getUserId());

		if (visitedLocationTourGuide != null) {
			user.addToVisitedLocations(visitedLocationTourGuide);
			rewardsService.calculateRewards(user);
		}
		return visitedLocationTourGuide;
	}

	/**
	 * Track user location for list of users
	 *
	 * @param userList list of user to track
	 */
	public void trackUserLocationForUserList(List<User> userList) {
		logger.debug("Track user location for user list : nbUsers = " + userList.size());
		ExecutorService trackLocationExecutorService = Executors.newFixedThreadPool(1500);

		userList.stream().forEach(user -> {
			Runnable runnableTask = () -> {

				trackUserLocation(user);
			};
			trackLocationExecutorService.submit(runnableTask);
		});

		trackLocationExecutorService.shutdown();

		try {
			boolean executorHasFinished = trackLocationExecutorService.awaitTermination(15, TimeUnit.MINUTES);
			if (!executorHasFinished) {
				logger.error("trackUserLocationforUserList does not finish in 15 minutes elapsed time");
				trackLocationExecutorService.shutdownNow();
			} else {
				logger.debug("trackUserLocationforUserList finished before the 15 minutes elapsed time");
			}
		} catch (InterruptedException interruptedException) {
			logger.error("executorService was Interrupted : " + interruptedException.getLocalizedMessage());
			trackLocationExecutorService.shutdownNow();
		}
	}

	/**
	 * Return Top 5 attractions nearest to user
	 *
	 * @param visitedLocationTourGuide visited location
	 * @return top 5 attractions nearest to user last visited location
	 */
	public NearbyAttractionDto getNearByAttractions(VisitedLocationTourGuide visitedLocationTourGuide) {
		List<AttractionClosestDto> attractionClosestDtoList = new ArrayList<>();
		List<AttractionTourGuide> attractionTourGuideList = gpsUtilService.getAttractions();
		if (attractionTourGuideList!=null) {
			attractionTourGuideList.stream().forEach(attraction ->
					{
						AttractionClosestDto attractionClosestDto = new AttractionClosestDto(attraction.getAttractionName(), attraction,
								rewardsService.getDistance(attraction, visitedLocationTourGuide.getLocationTourGuide()), rewardsService.getRewardPoints(attraction, visitedLocationTourGuide.getUserId()));
						attractionClosestDtoList.add(attractionClosestDto);

					}
			);

			NearbyAttractionDto nearbyAttractionDto = new NearbyAttractionDto();
			nearbyAttractionDto.setUserLocationTourGuide(visitedLocationTourGuide.getLocationTourGuide());
			nearbyAttractionDto.setClosestAttractionsList(attractionClosestDtoList.stream().sorted(Comparator.comparingDouble(AttractionClosestDto::getDistanceUserToAttraction)).limit(5).collect(Collectors.toList()));

			return nearbyAttractionDto;
		}
		else
		{
			return null;
		}
	}

	/**
	 * Get a list of every user's most recent location
	 *
	 * @return list of all user's last visited location
	 */
	public List<UserCurrentLocationDto> getAllUsersCurrentLocation() {
		List<User> userList = this.getAllUsers();

		List<UserCurrentLocationDto> userCurrentLocationDtoList = new CopyOnWriteArrayList<>();
		userList.stream().forEach(user -> {
			userCurrentLocationDtoList.add(new UserCurrentLocationDto(user.getUserId().toString(), user.getLastVisitedLocation().getLocationTourGuide()));
		});

		return userCurrentLocationDtoList;
	}

	private void addShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				tracker.stopTracking();
			}
		});
	}
}
