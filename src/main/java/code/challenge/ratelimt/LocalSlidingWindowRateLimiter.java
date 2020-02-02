package code.challenge.ratelimt;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Rate limiting using Local in-memory Sliding window algorithm
 * - For every id, keep track of all request timestamps and lock to handle multi thread
 * - For every request, truncate old timestamps outside the window and check the size of queue
 * - If the queue size is less than limit, return success
 * - Else return limit reached and calculate the time to try again
 */
public class LocalSlidingWindowRateLimiter implements RateLimiter {
  private final long limit;
  private final long windowInSecs;
  private Clock clock = new Clock();

  // Map of queue to store the timestamps of requests for every id
  private final Map<String, Queue<Long>> timestampMap = new HashMap<>();

  // Map to hold id level locks. So that synchronization can be at id level.
  private final Map<String, Object> lockMap = new ConcurrentHashMap<>();

  public LocalSlidingWindowRateLimiter(long limit, long windowInSecs) {
    if (limit < 1) throw new IllegalArgumentException("limit can not be less than 1");
    if (windowInSecs < 1) throw new IllegalArgumentException("windowInSecs can not be less than 1");

    this.limit = limit;
    this.windowInSecs = windowInSecs;
  }

  // For Unit testing
  LocalSlidingWindowRateLimiter(long limit, long windowInSecs, Clock clock) {
    this(limit, windowInSecs);
    this.clock = clock;
  }

  public AcquireResponse acquire(String id) {
    synchronized (lockMap.computeIfAbsent(id, k -> new Object())) {
      long currentSecs = this.clock.getCurrentSecs();
      long windowStart = currentSecs - windowInSecs;

      var timestampQueue = timestampMap.computeIfAbsent(id, k -> new ArrayDeque<>());
      var newQueue = timestampQueue.stream()
        .dropWhile(t -> t <= windowStart)
        .collect(Collectors.toCollection(ArrayDeque::new));
      timestampMap.put(id, newQueue);

      if (newQueue.size() < limit) {
        newQueue.offer(currentSecs);
        return new AcquireResponse(true, Math.max(0, limit - newQueue.size()), 0);
      }

      long availableInSecs =  Math.max(1, newQueue.peek() - windowStart);
      return new AcquireResponse(false, Math.max(0, limit - newQueue.size()), availableInSecs);
    }
  }
}

// Extracted out to mock in unit test
class Clock {
  public long getCurrentSecs() {
    return System.currentTimeMillis() / 1000;
  }
}
