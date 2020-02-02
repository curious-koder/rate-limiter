package code.challenge.ratelimt;

public interface RateLimiter {
  AcquireResponse acquire(String id);
}
