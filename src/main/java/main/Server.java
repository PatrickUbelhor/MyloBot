package main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class Server {

	private static final Logger logger = LogManager.getLogger(Server.class);

	@GetMapping(value = "/hello")
	public ResponseEntity<String> hello() {
		return ResponseEntity.ok("Hello World");
	}

	@GetMapping(value = "/music/queue")
	public ResponseEntity<String> getMusicQueue() {
		// TODO: Do something
		return ResponseEntity.ok(">>> Nothing");
	}

	@PostMapping(value = "/music/play", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> addToEndOfMusicQueue(
		@RequestBody String link
	) {
		// TODO: Do something
		return ResponseEntity.accepted().build();
	}

	@PostMapping(value = "/music/playNext", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> addToStartOfMusicQueue(
		@RequestBody String link
	) {
		// TODO: Do something
		return ResponseEntity.accepted().build();
	}

}
