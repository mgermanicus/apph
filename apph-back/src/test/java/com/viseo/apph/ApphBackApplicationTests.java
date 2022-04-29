package com.viseo.apph;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.Assert.assertThrows;

// Test class added to cover main() invocation
@SpringBootTest(classes = ApphBackApplicationTests.class)
class ApphBackApplicationTests {

    @Test
    void contextShouldNotLoadWhenPropertiesIncorrect() {
        // Given
        assertThrows(UnsatisfiedDependencyException.class, () ->
                ApphBackApplication.main(new String[]{"--spring.profiles.active=incorrect"}));
    }
}