package tourGuide.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tourGuide.service.rewardCentral.RewardCentralServiceWebClient;

import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.assertEquals;

public class RewardCentralServiceWebClientTest {

    public static MockWebServer mockBackEnd;
    private RewardCentralServiceWebClient rewardCentralService;
    private final ObjectMapper objectMapper = new ObjectMapper();

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

        rewardCentralService = new RewardCentralServiceWebClient(baseUrl);

    }

    @Test
    void getAttractionRewardsTest() throws Exception
    {
        UUID userId = UUID.randomUUID();
        UUID attractionId = UUID.randomUUID();

        mockBackEnd.enqueue(new MockResponse()
                .setBody(objectMapper.writeValueAsString(50))
                .addHeader("Content-Type", "application/json"));

        int rewardsPoints = rewardCentralService.getAttractionRewardPoints(attractionId,userId);

        assertThat(rewardsPoints).isEqualTo(50);

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();

        assertEquals("GET", recordedRequest.getMethod());
        assertEquals(RewardCentralServiceWebClient.attractionsRewardsEndpoint + "?attractionId="+attractionId.toString()+"&userId="+userId, recordedRequest.getPath());
    }
}
