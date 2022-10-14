package tourGuide.service.rewardCentral;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class RewardCentralServiceWebClient implements IRewardCentralService {
    private final Logger logger = LoggerFactory.getLogger(RewardCentralServiceWebClient.class);

    public static final String attractionsRewardsEndpoint = "/rewardsPoints";

    private final WebClient webClient;

    /**
     * Constructor of service
     */
    public RewardCentralServiceWebClient()
    {
        this(defaultRewardsCentralRootUrl);
    }

    public RewardCentralServiceWebClient(String rewardsCentralRootUrl)
    {
        webClient = WebClient.builder().baseUrl(rewardsCentralRootUrl).
                defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).
                build();
    }

    /**
     * Get rewards Points for a user visiting an attraction
     * @param attractionId attraction id
     * @param userId user id
     * @return number of rewards points
     */
    public int getAttractionRewardPoints(UUID attractionId, UUID userId)
    {
        logger.debug("Call to rewardCentral.getAttractionRewardPoints("+ attractionId + "," + userId+ ")");

        Mono<Integer> integerMono = webClient.get().uri(uriBuilder ->
                uriBuilder.path(attractionsRewardsEndpoint).
                        queryParam("attractionId",attractionId.toString()).
                        queryParam("userId",userId.toString()).build()).retrieve().bodyToMono(Integer.class);


        return integerMono.blockOptional().orElse(Integer.MIN_VALUE);
    }

}
