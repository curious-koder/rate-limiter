package code.challenge.api;

import code.challenge.model.Greeting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import static code.challenge.Constants.CLIENT_ID;
import static code.challenge.Constants.PUBLIC_USER;

@RestController
public class GreetingController {
	private static final String template = "Hello %s!";
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@GetMapping("/greeting")
	public Greeting greeting(@RequestHeader(value = CLIENT_ID, defaultValue = PUBLIC_USER) String clientId) {
		logger.info("Greeting request for clientId: {}", clientId);
		return new Greeting(String.format(template, clientId));
	}
}
