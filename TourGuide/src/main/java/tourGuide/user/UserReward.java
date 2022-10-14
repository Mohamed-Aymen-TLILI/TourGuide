package tourGuide.user;

import lombok.Data;
import tourGuide.model.AttractionTourGuide;
import tourGuide.model.VisitedLocationTourGuide;

@Data
public class UserReward {

	private VisitedLocationTourGuide visitedLocationTourGuide;
	private AttractionTourGuide attractionTourGuide;
	private int rewardPoints;

	public UserReward(VisitedLocationTourGuide visitedLocationTourGuide, AttractionTourGuide attractionTourGuide, int rewardPoints) {
		this.visitedLocationTourGuide = visitedLocationTourGuide;
		this.attractionTourGuide = attractionTourGuide;
		this.rewardPoints = rewardPoints;
	}

	public UserReward(VisitedLocationTourGuide visitedLocationTourGuide, AttractionTourGuide attractionTourGuide) {
		this.visitedLocationTourGuide = visitedLocationTourGuide;
		this.attractionTourGuide = attractionTourGuide;
	}

}
