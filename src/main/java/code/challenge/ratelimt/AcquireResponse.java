package code.challenge.ratelimt;

public class AcquireResponse {
  private final boolean isSuccessful;
  private final long remaining;
  private final long availableInSecs;

  public AcquireResponse(boolean isSuccessful, long remaining, long availableInSecs) {
    this.isSuccessful = isSuccessful;
    this.remaining = remaining;
    this.availableInSecs = availableInSecs;
  }

  public long getAvailableInSecs() {
    return availableInSecs;
  }

  public long getRemaining() {
    return remaining;
  }

  public boolean isSuccessful() {
    return isSuccessful;
  }

  @Override
  public String toString() {
    return "AcquireResponse{" +
      "isSuccessful=" + isSuccessful +
      ", remaining=" + remaining +
      ", availableInSecs=" + availableInSecs +
      '}';
  }
}
