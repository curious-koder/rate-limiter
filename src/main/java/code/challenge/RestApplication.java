package code.challenge;

import code.challenge.ratelimt.RateLimiter;
import code.challenge.ratelimt.LocalSlidingWindowRateLimiter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RestApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestApplication.class, args);
    }

    @Bean
    public RateLimiter rateLimiter() {
        return new LocalSlidingWindowRateLimiter(100, 3600);
    }
}
