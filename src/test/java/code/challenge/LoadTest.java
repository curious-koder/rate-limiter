package code.challenge;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(print = MockMvcPrint.SYSTEM_ERR)
public class  LoadTest {
  @Autowired
  private MockMvc mockMvc;

  @Test
  public void Should_FailRequest_AfterRateLimitExceeded() throws Exception {
    String clientId = "user1";
    String clientId2 = "user2";

    // First hundred is successful
    for (int i = 0; i < 100; i++) {
      this.mockMvc.perform(
          get("/greeting").header("client_id", clientId)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Hello user1!"));
    }

    // Next 100 calls fail
    for (int i = 0; i < 100; i++) {
      this.mockMvc.perform(
          get("/greeting").header("client_id", clientId)
        )
        .andExpect(status().isTooManyRequests())
        .andExpect(content().string(
          Matchers.containsString("Rate limit exceeded.")
        ));
    }

    // Calls for other clientId is successful
    this.mockMvc.perform(
        get("/greeting").header("client_id", clientId2)
      )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.message").value("Hello user2!"));
  }
}
