package com.viseo.apph.domain;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(MockitoJUnitRunner.class)
public class BaseTest {

    @Test
    public void testBaseEntity() {
        // Given
        BaseEntity baseEntity = new BaseEntity().setId(1L).setVersion(2L);
        // Then
        assertEquals(1L, baseEntity.getId());
        assertEquals(2L, baseEntity.getVersion());
        assertEquals(32, baseEntity.hashCode());
    }

    @Test
    public void testEquals() {
        // Given
        BaseEntity baseEntity = new BaseEntity().setId(1L).setVersion(2L);
        BaseEntity baseEntityCopy = new BaseEntity().setId(1L).setVersion(2L);
        // Then
        assertEquals(baseEntity, baseEntityCopy);
        assertEquals(baseEntity, baseEntity);
        assertNotEquals(baseEntity, null);
        assertNotEquals(baseEntity, this);
        assertNotEquals(baseEntity, baseEntityCopy.setId(3L));
    }
}
