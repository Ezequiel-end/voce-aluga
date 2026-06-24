package com.vocealuga;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles({"api", "web"})
class VoceAlugaApplicationTests {

	@Test
	void contextLoads() {
	}

}
