package com.viseo.apph;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.Assert.assertThrows;

// Test class added to cover main() invocation
@SpringBootTest
class ApphBackApplicationTests {

    @Test
    void contextShouldNotLoadWhenPropertiesIncorrect() {
        assertThrows(UnsatisfiedDependencyException.class, () ->
            ApphBackApplication.main(new String[] {"--spring.profiles.active=incorrect"}));
    }

}
