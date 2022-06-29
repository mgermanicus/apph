package com.viseo.apph.config;

import com.viseo.apph.dao.RoleDao;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import software.amazon.awssdk.services.s3.S3Client;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

public class DataBaseConfigTest {
    DataBaseConfig dataBaseConfig = new DataBaseConfig();

    @Test
    public void testOnApplicationEvent() {
        //GIVEN
        dataBaseConfig.init = true;
        EntityManager em = mock(EntityManager.class);
        dataBaseConfig.em = em;
        dataBaseConfig.roleDao = mock(RoleDao.class);
        TypedQuery<Long> typedQuery = mock(TypedQuery.class);
        ContextRefreshedEvent eventMock = mock(ContextRefreshedEvent.class);
        ApplicationContext applicationContextMock = mock(ApplicationContext.class);
        when(eventMock.getApplicationContext()).thenReturn(applicationContextMock);
        when(applicationContextMock.getBean(DataBaseConfig.class)).thenReturn(dataBaseConfig);
        when(em.createQuery(anyString(), eq(Long.class))).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(0L);
        //WHEN
        dataBaseConfig.onApplicationEvent(eventMock);
        //THEN
    }
}
