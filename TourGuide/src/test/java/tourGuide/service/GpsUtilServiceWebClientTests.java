package tourGuide.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tourGuide.model.AttractionTourGuide;
import tourGuide.model.LocationTourGuide;
import tourGuide.model.VisitedLocationTourGuide;
import tourGuide.service.gpsUtil.GpsUtilServiceWebClient;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.assertEquals;

public class GpsUtilServiceWebClientTests {

    public static MockWebServer mockBackEnd;
    private GpsUtilServiceWebClient gpsUtilServiceWebClient;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @BeforeEach
    public void setUpEachMethod()
    {
        String baseUrl = String.format("http://localhost:%s",
                mockBackEnd.getPort());

        gpsUtilServiceWebClient = new GpsUtilServiceWebClient(baseUrl);

    }

    @Test
    void getUserLocationTest() throws Exception
    {

        UUID userId = UUID.randomUUID();
        VisitedLocationTourGuide visitedLocation = new VisitedLocationTourGuide(userId,new LocationTourGuide(20d,25d), Date.from(Instant.now()));

        mockBackEnd.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(visitedLocation))
                .addHeader("Content-Type", "application/json"));

        VisitedLocationTourGuide visitedLocationTourGuide = gpsUtilServiceWebClient.getUserLocation(userId);

        assertThat(visitedLocationTourGuide).isNotNull();
        assertThat(visitedLocationTourGuide.getUserId()).isEqualTo(userId);

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();

        assertEquals("GET", recordedRequest.getMethod());
        assertEquals(GpsUtilServiceWebClient.userLocationEndpoint + "?userId="+userId, recordedRequest.getPath());
    }

    @Test
    public void getAllAttractions() throws Exception
    {
        List<AttractionTourGuide> attractionList = new ArrayList<>();
        AttractionTourGuide attraction1 = new AttractionTourGuide("attraction1","city1","state1",10d,15d);
        AttractionTourGuide attraction2 = new AttractionTourGuide("attraction2","city2","state2",25d,35d);

        attractionList.add(attraction1);
        attractionList.add(attraction2);

        mockBackEnd.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(attractionList))
                .addHeader("Content-Type", "application/json"));

        List<AttractionTourGuide> attractionTourGuideList = gpsUtilServiceWebClient.getAttractions();

        assertThat(attractionTourGuideList).isNotEmpty();

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();

        assertEquals("GET", recordedRequest.getMethod());
        assertEquals( GpsUtilServiceWebClient.attractionsEndpoint, recordedRequest.getPath());

    }
}
