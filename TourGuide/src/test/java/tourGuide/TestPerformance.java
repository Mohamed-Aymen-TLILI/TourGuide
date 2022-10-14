package tourGuide;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.AttractionTourGuide;
import tourGuide.model.VisitedLocationTourGuide;
import tourGuide.service.InitializationService;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.service.gpsUtil.GpsUtilServiceRestTemplate;
import tourGuide.service.gpsUtil.IGpsUtilService;
import tourGuide.service.rewardCentral.IRewardCentralService;
import tourGuide.service.rewardCentral.RewardCentralServiceRestTemplate;
import tourGuide.service.tripPricer.ITripPricerService;
import tourGuide.service.tripPricer.TripPricerServiceRestTemplate;
import tourGuide.user.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.assertTrue;

public class TestPerformance {

	/*
	 * A note on performance improvements:
	 *
	 *     The number of users generated for the high volume tests can be easily adjusted via this method:
	 *
	 *     		InternalTestHelper.setInternalUserNumber(100000);
	 *
	 *
	 *     These tests can be modified to suit new solutions, just as long as the performance metrics
	 *     at the end of the tests remains consistent.
	 *
	 *     These are performance metrics that we are trying to hit:
	 *
	 *     highVolumeTrackLocation: 100,000 users within 15 minutes:
	 *     		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	 *
	 *     highVolumeGetRewards: 100,000 users within 20 minutes:
	 *          assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	 */
	private RewardsService rewardsService;

	private TourGuideService tourGuideService;

	private IGpsUtilService gpsUtilService;

	private ITripPricerService tripPricerService;

	private IRewardCentralService rewardCentralService;

	private InitializationService initializationService;

	@BeforeEach
	public void setUp() {
		gpsUtilService = new GpsUtilServiceRestTemplate();
		tripPricerService = new TripPricerServiceRestTemplate();
		rewardCentralService = new RewardCentralServiceRestTemplate();
		initializationService = new InitializationService();
		InternalTestHelper.setInternalUserNumber(100);

	}

	@Test
	public void highVolumeTrackLocationNewPerf() {
		// Users should be incremented up to 100,000, and test finishes within 15 minutes

		rewardsService = new RewardsService(gpsUtilService, rewardCentralService);
		tourGuideService = new TourGuideService(gpsUtilService, rewardsService, tripPricerService, initializationService);
		tourGuideService.tracker.stopTracking();

		List<User> allUsers = new ArrayList<>();
		allUsers = tourGuideService.getAllUsers();

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		tourGuideService.trackUserLocationForUserList(allUsers);

		stopWatch.stop();

		System.out.println("highVolumeTrackLocationNewPerf: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds for " + allUsers.size() + " users.");

		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	}

	@Test
	public void highVolumeGetRewardsNewPerf() {
		rewardsService = new RewardsService(gpsUtilService, rewardCentralService);
		tourGuideService = new TourGuideService(gpsUtilService, rewardsService, tripPricerService, initializationService);
		tourGuideService.tracker.stopTracking();

		// Users should be incremented up to 100,000, and test finishes within 20 minutes
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		System.out.println("Start StopWatch");

		AttractionTourGuide attractionTourGuide = gpsUtilService.getAttractions().get(0);
		List<User> allUsers = tourGuideService.getAllUsers();

		allUsers.stream().forEach(u -> u.addToVisitedLocations(new VisitedLocationTourGuide(u.getUserId(), attractionTourGuide, new Date())));

		rewardsService.calculateRewardsForUserList(allUsers);

		allUsers.stream().forEach(user -> {
			try {
				System.out.println(new ObjectMapper().writeValueAsString(user));
				assertThat(user.getUserRewards().size()).isGreaterThan(0);
			} catch (Exception exception) {
			}
		});

		stopWatch.stop();

		System.out.println("highVolumeGetRewards: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds for " + allUsers.size() + " users.");
		assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	}
}
