package tourGuide.service.tripPricer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import tourGuide.model.Provider;

import java.util.List;
import java.util.UUID;

/**
 * TripPricer Service class
 */
public class TripPricerServiceRestTemplate implements ITripPricerService {

    private final Logger logger = LoggerFactory.getLogger(TripPricerServiceRestTemplate.class);

    public static final String priceEndpoint = "/price";

    public static final String providerNameEndpoint = "/providerName";

    /**
     * get provider list of trips with calculated price for given parameters
     *
     * @param apiKey        apiKey
     * @param attractionId  attraction id
     * @param adults        number of adults
     * @param children      number of children
     * @param nightsStay    number of nights stayed
     * @param rewardsPoints number of rewardsPoints
     * @return provider list
     */
    public List<Provider> getPrice(String apiKey, UUID attractionId, int adults, int children, int nightsStay, int rewardsPoints) {
        logger.debug("Call to tripPricer.getPrice");

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(defaultTripPricerRootUrl + priceEndpoint)
                .queryParam("apiKey", apiKey)
                .queryParam("attractionId", attractionId)
                .queryParam("adults", adults)
                .queryParam("children", children)
                .queryParam("nightsStay", nightsStay)
                .queryParam("rewardsPoints", rewardsPoints);

        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<List<Provider>> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<Provider>>() {
                    });
            return response.getBody();
        } catch (
                RestClientException exception) {
            logger.error("Exception during tripPricer.getPrice : " + exception.getMessage());
            return null;
        }

    }

    /**
     * getProvider name
     *
     * @param apiKey apiKey
     * @param adults number of Adults
     * @return provider name
     */
    public String getProviderName(String apiKey, int adults) {
        logger.debug("Call to tripPricer.getProviderName");

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(defaultTripPricerRootUrl + providerNameEndpoint)
                .queryParam("apiKey", apiKey)
                .queryParam("adults", adults);

        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(builder.toUriString(), String.class);
            return response.getBody();
        } catch (
                RestClientException exception) {
            logger.error("Exception during tripPricer.getProviderName : " + exception.getMessage());
            return null;
        }
    }

}
