package tourGuide.service.gpsUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import tourGuide.model.AttractionTourGuide;
import tourGuide.model.VisitedLocationTourGuide;

import java.util.List;
import java.util.UUID;

/**
 * GpsUtil Service class
 */
public class GpsUtilServiceRestTemplate implements IGpsUtilService {
    private final Logger logger = LoggerFactory.getLogger(GpsUtilServiceRestTemplate.class);

    public static final String attractionsEndpoint = "/attractions";

    public static final String userLocationEndpoint = "/userLocation";

    /**
     * getUserLocation for a given user
     *
     * @param userId userId
     * @return get VisitedLocation for given user
     */
    public VisitedLocationTourGuide getUserLocation(UUID userId) {
        logger.debug("Call to gpsUtilService.getUserLocation(" + userId + ")");
        RestTemplate restTemplate = new RestTemplate();
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(defaultGpsUtilRootUrl + userLocationEndpoint).
                queryParam("userId", userId);

        try {
            ResponseEntity<VisitedLocationTourGuide> responseEntity = restTemplate.getForEntity(uriComponentsBuilder.toUriString(), VisitedLocationTourGuide.class);

            return responseEntity.getBody();
        } catch (RestClientException exception) {
            logger.error("Exception during gpsUtilService.getUserLocation : " + exception.getMessage());
            return null;
        }

    }

    /**
     * get list of all Attractions
     *
     * @return list of attractions
     */
    public List<AttractionTourGuide> getAttractions() {

        logger.debug("Call to gpsUtilService.getAttractions()");

        StringBuilder str = new StringBuilder();
        str.append(defaultGpsUtilRootUrl);
        str.append(attractionsEndpoint);
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<List<AttractionTourGuide>> responseEntity = restTemplate.exchange(str.toString(), HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<AttractionTourGuide>>() {
                    });

            return responseEntity.getBody();
        } catch (RestClientException exception) {
            logger.error("Exception during gpsUtilService.getAttractions : " + exception.getMessage());
            return null;
        }
    }
}
