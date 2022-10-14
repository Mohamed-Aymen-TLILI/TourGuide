package tourGuide.service.tripPricer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import tourGuide.model.Provider;

import java.util.List;
import java.util.UUID;

/**
 * TripPricer Service class
 */
public class TripPricerServiceWebClient implements ITripPricerService {

    private final Logger logger = LoggerFactory.getLogger(TripPricerServiceWebClient.class);

    public static final String priceEndpoint = "/price";

    public static final String providerNameEndpoint = "/providerName";

    private final WebClient webClient;

    /**
     * Constructor of TripPricer Service
     */
    public TripPricerServiceWebClient()
    {
        this(defaultTripPricerRootUrl);
    }

    public TripPricerServiceWebClient(String tripPricerRootUrl)
    {
        webClient = WebClient.builder().baseUrl(tripPricerRootUrl).
                defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).
                build();
    }

    /**
     * get provider list of trips with calculated price for given parameters
     * @param apiKey apiKey
     * @param attractionId attraction id
     * @param adults number of adults
     * @param children number of children
     * @param nightsStay number of nights stayed
     * @param rewardsPoints number of rewardsPoints
     * @return provider list
     */
    public List<Provider> getPrice(String apiKey, UUID attractionId, int adults, int children, int nightsStay, int rewardsPoints)
    {
        logger.debug("Call to tripPricer.getPrice");

        return  webClient.method(HttpMethod.GET).uri(uriBuilder ->
                uriBuilder.path(priceEndpoint).queryParam("apiKey",apiKey)
                        .queryParam("adults",adults)
                        .queryParam("children",children)
                        .queryParam("attractionId",attractionId)
                        .queryParam("nightsStay", nightsStay)
                        .queryParam("rewardsPoints",rewardsPoints)
                        .build()).retrieve().bodyToFlux(Provider.class).collectList().block();
    }

    /**
     * getProvider name
     * @param apiKey apiKey
     * @param adults number of Adults
     * @return provider name
     */
    public String getProviderName(String apiKey, int adults)
    {
        logger.debug("Call to tripPricer.getProviderName");
        return webClient.get().uri(uriBuilder ->
                uriBuilder.path(providerNameEndpoint).
                        queryParam("apiKey",apiKey).
                        queryParam("adults",adults).build()).retrieve().bodyToMono(String.class).block();
    }

}
