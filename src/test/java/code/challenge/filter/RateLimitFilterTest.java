package code.challenge.filter;

import code.challenge.ratelimt.AcquireResponse;
import code.challenge.ratelimt.RateLimiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static code.challenge.Constants.CLIENT_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

class RateLimitFilterTest {
  @InjectMocks
  private RateLimitFilter rateLimitFilter;

  @Mock
  private RateLimiter mockRateLimiter;

  @Mock
  private HttpServletRequest mockHttpServletRequest;

  @Mock
  private HttpServletResponse mockHttpServletResponse;

  @Mock
  private FilterChain mockFilterChain;

  private String clientId = "user1";

  @BeforeEach
  public void init() {
    initMocks(this);
    when(mockHttpServletRequest.getHeader(CLIENT_ID)).thenReturn(clientId);
  }

  @Test
  public void shouldProceed_ToTheNextFilterChain_OnSuccessful_RateLimitAcquire() throws ServletException, IOException {
    AcquireResponse acquireResponse = new AcquireResponse(true, 2, 0);
    when(mockRateLimiter.acquire(clientId)).thenReturn(acquireResponse);

    rateLimitFilter.doFilterInternal(mockHttpServletRequest, mockHttpServletResponse, mockFilterChain);

    verify(mockFilterChain).doFilter(mockHttpServletRequest, mockHttpServletResponse);
    verifyNoInteractions(mockHttpServletResponse);
  }

  @Test
  public void should_UsePublicUserAsClientId_ForRateLimitAcquire() throws ServletException, IOException {
    String publicClientId = "public_user";
    when(mockHttpServletRequest.getHeader(CLIENT_ID)).thenReturn(null);
    AcquireResponse acquireResponse = new AcquireResponse(true,2, 0);
    when(mockRateLimiter.acquire(publicClientId)).thenReturn(acquireResponse);

    rateLimitFilter.doFilterInternal(mockHttpServletRequest, mockHttpServletResponse, mockFilterChain);

    verify(mockFilterChain).doFilter(mockHttpServletRequest, mockHttpServletResponse);
    verifyNoInteractions(mockHttpServletResponse);
  }

  @Test
  public void should_ReturnErrorResponse_IfRateLimitReached() throws ServletException, IOException {
    AcquireResponse acquireResponse = new AcquireResponse(false,0, 5);
    when(mockRateLimiter.acquire(clientId)).thenReturn(acquireResponse);
    MockHttpServletResponse mockResponse = new MockHttpServletResponse();

    rateLimitFilter.doFilterInternal(mockHttpServletRequest, mockResponse, mockFilterChain);

    verifyNoInteractions(mockFilterChain);
    assertEquals(429, mockResponse.getStatus());
    assertEquals("Rate limit exceeded. Try again in 5 seconds.", mockResponse.getContentAsString());
  }
}