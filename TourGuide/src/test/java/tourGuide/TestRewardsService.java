package tourGuide;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import tourGuide.model.AttractionTourGuide;
import tourGuide.model.VisitedLocationTourGuide;
import tourGuide.service.gpsUtil.IGpsUtilService;
import tourGuide.service.rewardCentral.IRewardCentralService;
import tourGuide.service.RewardsService;
import tourGuide.user.User;
import tourGuide.user.UserReward;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class TestRewardsService {

	private RewardsService rewardsService;

	private IRewardCentralService rewardCentralServiceMock = Mockito.mock(IRewardCentralService.class);

	private IGpsUtilService gpsUtilServiceMock = Mockito.mock(IGpsUtilService.class);

	@BeforeEach
	public void setUp() {
		rewardsService = new RewardsService(gpsUtilServiceMock, rewardCentralServiceMock);
	}

	@Test
	public void userGetRewards() {

		AttractionTourGuide attractionTourGuide1 = new AttractionTourGuide("att1", "city", "", 15.5, 20.5);
		AttractionTourGuide attractionTourGuide2 = new AttractionTourGuide("att2", "city", "", 25.5, 40.5);


		List<AttractionTourGuide> attractionTourGuideList = new ArrayList<>();
		attractionTourGuideList.add(attractionTourGuide1);
		attractionTourGuideList.add(attractionTourGuide2);

		when(gpsUtilServiceMock.getAttractions()).thenReturn(attractionTourGuideList);
		when(rewardCentralServiceMock.getAttractionRewardPoints(any(), any())).thenReturn(25);

		User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		AttractionTourGuide attractionTourGuide = gpsUtilServiceMock.getAttractions().get(0);
		user.addToVisitedLocations(new VisitedLocationTourGuide(user.getUserId(), attractionTourGuide, new Date()));

		rewardsService.setProximityBuffer(10);
		rewardsService.calculateRewards(user);

		List<UserReward> userRewards = user.getUserRewards();

		assertTrue(userRewards.size() == 1);
		assertEquals(userRewards.get(0).getRewardPoints(),25);
	}

	@Test
	public void getRewardsForUserList() {

		AttractionTourGuide attractionTourGuide1 = new AttractionTourGuide("att1", "city", "", 15.5, 20.5);
		AttractionTourGuide attractionTourGuide2 = new AttractionTourGuide("att2", "city", "", 25.5, 40.5);

		List<AttractionTourGuide> attractionTourGuideList = new ArrayList<>();
		attractionTourGuideList.add(attractionTourGuide1);
		attractionTourGuideList.add(attractionTourGuide2);

		when(gpsUtilServiceMock.getAttractions()).thenReturn(attractionTourGuideList);
		when(rewardCentralServiceMock.getAttractionRewardPoints(any(), any())).thenReturn(25);

		User user1 = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		AttractionTourGuide attractionTourGuide_user1 = gpsUtilServiceMock.getAttractions().get(0);
		user1.addToVisitedLocations(new VisitedLocationTourGuide(user1.getUserId(), attractionTourGuide_user1, new Date()));

		User user2 = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
		AttractionTourGuide attractionTourGuide_user2 = gpsUtilServiceMock.getAttractions().get(1);
		user2.addToVisitedLocations(new VisitedLocationTourGuide(user2.getUserId(), attractionTourGuide_user2, new Date()));

		List<User> userList = new ArrayList<>();
		userList.add(user1);
		userList.add(user2);

		rewardsService.setProximityBuffer(10);
		rewardsService.calculateRewardsForUserList(userList);

		userList.stream().forEach(user ->
				{
					assertTrue(user.getUserRewards().size() == 1);
					assertEquals(user.getUserRewards().get(0).getRewardPoints(),25);
				}
		);
	}

	@Test
	public void isWithinAttractionProximity() {
		AttractionTourGuide attractionTourGuide = new AttractionTourGuide("attractionName", "city", "state", 15.5, 25.5);
		assertTrue(rewardsService.isWithinAttractionProximity(attractionTourGuide, attractionTourGuide));
	}

}
