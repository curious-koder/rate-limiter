package code.challenge.ratelimt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

class LocalSlidingWindowRateLimiterTest {
  @Mock
  private Clock mockClock;

  private final long baseTime = 1000L;
  private final String limitId = "test1";
  private final String limitId2 = "test2";

  @BeforeEach
  public void init() {
    initMocks(this);
    when(mockClock.getCurrentSecs()).thenReturn(baseTime);
  }

  @Test
  public void should_Fail_IfLimitIsLessThanOne() {
    assertThrows(IllegalArgumentException.class, () ->{
      new LocalSlidingWindowRateLimiter(0, 5, mockClock);
    });
  }

  @Test
  public void should_Fail_IfWindowSecsIsLessThanOne() {
    assertThrows(IllegalArgumentException.class, () ->{
      new LocalSlidingWindowRateLimiter(4, 0, mockClock);
    });
  }

  @Test
  public void should_SuccessfullyAcquire_ForTheGivenId() {
    RateLimiter rateLimiter = new LocalSlidingWindowRateLimiter(5, 5, mockClock);

    AcquireResponse acquireResponse = rateLimiter.acquire(limitId);

    assertTrue(acquireResponse.isSuccessful());
    assertEquals(4, acquireResponse.getRemaining());
  }

  @Test
  public void should_RejectAcquire_IfTheLimitIsExceeded() {
    RateLimiter rateLimiter = new LocalSlidingWindowRateLimiter(1, 5, mockClock);

    assertTrue(rateLimiter.acquire(limitId).isSuccessful());
    assertFalse(rateLimiter.acquire(limitId).isSuccessful());
  }

  @Test
  public void should_RejectAcquire_AndReturn_TheNumberOfSecondsToTryAgain() {
    RateLimiter rateLimiter = new LocalSlidingWindowRateLimiter(1, 5, mockClock);
    assertTrue(rateLimiter.acquire(limitId).isSuccessful());
    when(mockClock.getCurrentSecs()).thenReturn(baseTime + 2);

    AcquireResponse response = rateLimiter.acquire(limitId);
    assertFalse(response.isSuccessful());
    assertEquals(3, response.getAvailableInSecs());

    when(mockClock.getCurrentSecs()).thenReturn(baseTime + 3);

    AcquireResponse response2 = rateLimiter.acquire(limitId);
    assertFalse(response2.isSuccessful());
    assertEquals(2, response2.getAvailableInSecs());
  }

  @Test
  public void should_SuccessfullyAcquire_IfTheTimeWindowIsMoved() {
    RateLimiter rateLimiter = new LocalSlidingWindowRateLimiter(2, 5, mockClock);

    assertTrue(rateLimiter.acquire(limitId).isSuccessful());
    assertTrue(rateLimiter.acquire(limitId).isSuccessful());
    assertFalse(rateLimiter.acquire(limitId).isSuccessful());

    when(mockClock.getCurrentSecs()).thenReturn(baseTime + 5);
    AcquireResponse acquireResponse = rateLimiter.acquire(limitId);
    assertTrue(acquireResponse.isSuccessful());
    assertEquals(1, acquireResponse.getRemaining());
  }

  @Test
  public void should_SuccessfullyAcquire_ForTheMultipleIds() {
    RateLimiter rateLimiter = new LocalSlidingWindowRateLimiter(5, 5, mockClock);

    assertTrue(rateLimiter.acquire(limitId).isSuccessful());
    assertTrue(rateLimiter.acquire(limitId2).isSuccessful());
  }

  @Test
  public void should_SuccessfullyAcquire_ForNewLimitId() {
    RateLimiter rateLimiter = new LocalSlidingWindowRateLimiter(1, 5, mockClock);

    assertTrue(rateLimiter.acquire(limitId).isSuccessful());
    assertFalse(rateLimiter.acquire(limitId).isSuccessful());

    assertTrue(rateLimiter.acquire(limitId2).isSuccessful());
  }
}