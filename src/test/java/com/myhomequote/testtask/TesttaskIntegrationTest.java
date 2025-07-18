package com.myhomequote.testtask;

import com.myhomequote.testtask.dto.ResultDTO;
import com.myhomequote.testtask.dto.SetInfoRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TesttaskIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    @Test
    public void testIntegrationUserLevelEndpoints() {
        sendSetInfo(1, 1, 55);
        sendSetInfo(1, 2, 8);
        sendSetInfo(1, 3, 15);
        sendSetInfo(2, 3, 22);
        sendSetInfo(3, 3, 50);
        sendSetInfo(4, 3, 18);
        sendSetInfo(5, 3, 33);

        ResponseEntity<ResultDTO[]> levelResponse = restTemplate.getForEntity(url("/levelinfo/3"), ResultDTO[].class);
        assertEquals(HttpStatus.OK, levelResponse.getStatusCode());
        ResultDTO[] levelResults = levelResponse.getBody();
        assertNotNull(levelResults);
        assertEquals(5, levelResults.length);
        assertEquals(3, levelResults[0].getLevelId());
        assertEquals(50, levelResults[0].getResult());

        ResponseEntity<ResultDTO[]> userResponse = restTemplate.getForEntity(url("/userinfo/1"), ResultDTO[].class);
        assertEquals(HttpStatus.OK, userResponse.getStatusCode());
        ResultDTO[] userResults = userResponse.getBody();
        assertNotNull(userResults);
        assertEquals(3, userResults.length);
        assertEquals(55, userResults[0].getResult());
        assertEquals(15, userResults[1].getResult());
        assertEquals(8, userResults[2].getResult());
    }

    @Test
    public void testConcurrentSetInfoUpdates() throws InterruptedException {
        int threads = 50;
        int levelId = 99;
        ExecutorService executor = Executors.newFixedThreadPool(10);

        CountDownLatch latch = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            final int userId = i + 1;
            final int result = 100 - i;

            executor.submit(() -> {
                try {
                    sendSetInfo(userId, levelId, result);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        ResponseEntity<ResultDTO[]> response = restTemplate.getForEntity(url("/levelinfo/" + levelId), ResultDTO[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ResultDTO[] topResults = response.getBody();
        assertNotNull(topResults);
        assertEquals(20, topResults.length);

        for (int i = 0; i < topResults.length - 1; i++) {
            int current = topResults[i].getResult();
            int next = topResults[i + 1].getResult();
            assertTrue(current >= next, "Results not sorted correctly");
        }

        System.out.println("Top 20 results for level 99:");
        for (ResultDTO dto : topResults) {
            System.out.printf("User: %d | Result: %d%n", dto.getUserId(), dto.getResult());
        }
    }

    private void sendSetInfo(int userId, int levelId, int result) {
        SetInfoRequest req = new SetInfoRequest();
        req.setUserId(userId);
        req.setLevelId(levelId);
        req.setResult(result);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<SetInfoRequest> entity = new HttpEntity<>(req, headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                url("/setinfo"),
                HttpMethod.PUT,
                entity,
                Void.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
