package code.challenge;

import code.challenge.ratelimt.LocalSlidingWindowRateLimiter;
import code.challenge.ratelimt.RateLimiter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.IntStream;

// Main class to manual test the RateLimiter concurrency with executor service
public class TestMainClass {
  public static void main(String[] args) throws InterruptedException {
    RateLimiter rateLimiter = new LocalSlidingWindowRateLimiter(5, 2);
    ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);

    Runnable command = () ->
      IntStream.range(0, 2).forEach(i -> {
          String clientId = "user" + i;
          System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
            + " : " + clientId + " : " + rateLimiter.acquire(clientId));
        }
      );

    while (true) {
      IntStream.range(0, 5).forEach(i -> executorService.execute(command));
      Thread.sleep(1000);
    }
  }
}
