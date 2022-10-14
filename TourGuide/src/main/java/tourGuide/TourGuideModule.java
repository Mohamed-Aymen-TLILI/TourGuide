package tourGuide;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tourGuide.service.InitializationService;
import tourGuide.service.gpsUtil.GpsUtilServiceRestTemplate;
import tourGuide.service.gpsUtil.IGpsUtilService;
import tourGuide.service.rewardCentral.IRewardCentralService;
import tourGuide.service.tripPricer.ITripPricerService;
import tourGuide.service.rewardCentral.RewardCentralServiceRestTemplate;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.service.tripPricer.TripPricerServiceRestTemplate;

@Configuration
public class TourGuideModule {

	@Bean
	public InitializationService getInitializationService()
	{
		return new InitializationService();
	}


	@Bean
	public ITripPricerService getTripPricerService()
	{
		return  new TripPricerServiceRestTemplate();
	}

	@Bean
	public IGpsUtilService getGpsUtilService() {
		return new GpsUtilServiceRestTemplate();
	}

	@Bean
	public IRewardCentralService getRewardCentralService() {
		return new RewardCentralServiceRestTemplate();
	}


	@Bean
	public RewardsService getRewardsService() {
		return new RewardsService(getGpsUtilService(), getRewardCentralService());
	}


	@Bean
	public TourGuideService getTourGuideService()
	{
		return new TourGuideService(getGpsUtilService(),getRewardsService(), getTripPricerService(), getInitializationService());
	}


}
