package tourGuide.service.rewardCentral;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

public class RewardCentralServiceRestTemplate implements IRewardCentralService {
    private final Logger logger = LoggerFactory.getLogger(RewardCentralServiceRestTemplate.class);

    public static final String attractionsRewardsEndpoint = "/rewardsPoints";

    /**
     * Get rewards Points for a user visiting an attraction
     *
     * @param attractionId attraction id
     * @param userId       user id
     * @return number of rewards points
     */
    public int getAttractionRewardPoints(UUID attractionId, UUID userId) {
        logger.debug("Call to rewardCentral.getAttractionRewardPoints(" + attractionId + "," + userId + ")");

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(defaultRewardsCentralRootUrl + attractionsRewardsEndpoint)
                .queryParam("attractionId", attractionId)
                .queryParam("userId", userId);
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Integer> responseEntity = restTemplate.getForEntity(uriComponentsBuilder.toUriString(), Integer.class);
            return responseEntity.getBody();
        } catch (
                RestClientException exception) {
            logger.error("Exception during rewardCentral.getAttractionRewardPoints : " + exception.getMessage());
            return Integer.MIN_VALUE;
        }
    }

}
