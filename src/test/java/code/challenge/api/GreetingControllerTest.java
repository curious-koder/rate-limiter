package code.challenge.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GreetingController.class)
public class GreetingControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void ShouldReturnGreetingMessage_ForClientIdInHeader() throws Exception {
		this.mockMvc.perform(
			get("/greeting").header("client_id", "user1")
		)
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.message").value("Hello user1!"));
	}

	@Test
	public void ShouldReturnGreetingMessage_ForPublicUser() throws Exception {
		this.mockMvc.perform(
			get("/greeting")
		)
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.message").value("Hello public_user!"));
	}
}
