package com.viseo.apph;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// Test class added to cover main() invocation
@SpringBootTest
class ApphBackApplicationTests {

    @Test
    void applicationContextTest() {
        ApphBackApplication.main(new String[] {});
    }

}
