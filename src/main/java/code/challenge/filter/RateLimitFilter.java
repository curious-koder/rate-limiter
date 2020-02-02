package code.challenge.filter;

import code.challenge.ratelimt.AcquireResponse;
import code.challenge.ratelimt.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static code.challenge.Constants.CLIENT_ID;
import static code.challenge.Constants.PUBLIC_USER;
import static java.util.Optional.ofNullable;

@Component
public class RateLimitFilter extends OncePerRequestFilter {
  private final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private RateLimiter rateLimiter;

  @Override
  protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                  HttpServletResponse httpServletResponse,
                                  FilterChain filterChain) throws ServletException, IOException {
    String clientId = getClientId(httpServletRequest);
    AcquireResponse acquireResponse = rateLimiter.acquire(clientId);
    logger.info("RateLimit acquire response  : {} : {}", clientId, acquireResponse);

    if (acquireResponse.isSuccessful()) {
      filterChain.doFilter(httpServletRequest, httpServletResponse);
      return;
    }

    logger.info("RateLimit exceeded for clientId : {}. Request terminated.", clientId);
    String message = String.format("Rate limit exceeded. Try again in %d seconds.", acquireResponse.getAvailableInSecs());
    httpServletResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
    httpServletResponse.getWriter().print(message);
  }

  private String getClientId(HttpServletRequest httpServletRequest) {
    return ofNullable(httpServletRequest.getHeader(CLIENT_ID)).orElse(PUBLIC_USER);
  }
}
