package com.viseo.apph;

import com.viseo.apph.dto.IResponseDTO;
import com.viseo.apph.exception.GlobalExceptionHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class GlobalExceptionHandlerTest {
    @InjectMocks
    GlobalExceptionHandler globalExceptionHandler;

    @Mock
    MaxUploadSizeExceededException maxUploadSizeExceededException;

    @Test
    public void testHandleMultipartException() {
        ResponseEntity<IResponseDto> responseEntity = globalExceptionHandler.handleMultipartException(maxUploadSizeExceededException);
        assertEquals(HttpStatus.PAYLOAD_TOO_LARGE, responseEntity.getStatusCode());
    }
}
