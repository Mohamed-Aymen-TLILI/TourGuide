package tourGuide.service.gpsUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import tourGuide.model.AttractionTourGuide;
import tourGuide.model.VisitedLocationTourGuide;

import java.util.List;
import java.util.UUID;

/**
 * GpsUtil Service class
 */
public class GpsUtilServiceWebClient implements IGpsUtilService {
    private final Logger logger = LoggerFactory.getLogger(GpsUtilServiceWebClient.class);

    public static final String attractionsEndpoint = "/attractions";

    public static final String userLocationEndpoint= "/userLocation";


    private WebClient webClient;

    /**
     * Constructor that initialize a new gpsUtil class
     */
    public GpsUtilServiceWebClient()
    {
        this(defaultGpsUtilRootUrl);
    }

    /**
     * Constructor with gopsUtilRoot url in param
     * @param gpsUtilRootUrl rootUrl for gpsUtil api
     */
    public GpsUtilServiceWebClient(String gpsUtilRootUrl)
    {
        webClient = WebClient.builder().baseUrl(gpsUtilRootUrl).
                defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).
                build();
    }

    /**
     * getUserLocation for a given user
     * @param userId userId
     * @return get VisitedLocation for given user
     */
    public VisitedLocationTourGuide getUserLocation(UUID userId)
    {
        logger.debug("Call to gpsUtilService.getUserLocation(" + userId + ")");
        return webClient.get().uri(uriBuilder ->
                uriBuilder.path(userLocationEndpoint).
                        queryParam("userId",userId.toString()).build()).retrieve().bodyToMono(VisitedLocationTourGuide.class).block();
    }

    /**
     * get list of all Attractions
     * @return list of attractions
     */
    public List<AttractionTourGuide> getAttractions()
    {
        logger.debug("Call to gpsUtilService.getAttractions()");

        return webClient.get().uri(uriBuilder ->
                uriBuilder.path(attractionsEndpoint).build()).retrieve().
                bodyToFlux(AttractionTourGuide.class).collectList().block();

    }
}
