package com.roimarai.ticket_booking_ticket_service;

import org.springframework.web.bind.annotation.ResponseStatus;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ConcurrencyTest {
    public static void main(String[] args) throws InterruptedException{
        int numberOfThreads = 20;
        Long eventId = 6L;

        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(numberOfThreads);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger conflictCount = new AtomicInteger(0);

        HttpClient client = HttpClient.newHttpClient();

        for (int i = 0; i < numberOfThreads; i++){
            executor.submit(() ->
                    {
                        try {
                            startLatch.await();

                            HttpRequest request = HttpRequest.newBuilder()
                                    .uri(URI.create("http://localhost:8082/events/" + eventId + "/reserve"))
                                    .header("Content-Type", "application/json")
                                    .POST(HttpRequest.BodyPublishers.ofString("{\"seatCount\": 1}"))
                                    .build();

                            HttpResponse<String> response = client.send(request,HttpResponse.BodyHandlers.ofString());

                            if (response.statusCode() == 201) {
                                successCount.incrementAndGet();
                            } else if (response.statusCode() == 409) {
                                conflictCount.incrementAndGet();
                            } else {
                                System.out.println("Unexpected status: " + response.statusCode());
                            }
                        }catch (Exception e) {
                            e.printStackTrace();
                        }finally {
                            doneLatch.countDown();
                        }
                    });
        }

        System.out.println("Starting");
        startLatch.countDown();
        doneLatch.await();

        executor.shutdown();

        System.out.println("Successful reservations: " + successCount.get());
        System.out.println("Rejected (409): " + conflictCount.get());
        System.out.println("Expected successful if no race condition: 10");
    }
}
