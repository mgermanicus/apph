package com.viseo.apph.domain;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PhotoTest {

    @Test
    public void testPhotoEntity() {
        //GIVEN
        Photo photo = new Photo().setName("Photo");
        //THEN
        Assert.assertEquals("Photo", photo.getName());
    }
}
