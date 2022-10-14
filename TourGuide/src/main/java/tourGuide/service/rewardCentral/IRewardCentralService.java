package tourGuide.service.rewardCentral;

import java.util.UUID;

public interface IRewardCentralService {

    final String defaultRewardsCentralRootUrl = "http://localhost:8102";

    /**
     * Get rewards Points for a user visiting an attraction
     * @param attractionId attraction id
     * @param userId user id
     * @return number of rewards points
     */
    int getAttractionRewardPoints(UUID attractionId, UUID userId);
}
