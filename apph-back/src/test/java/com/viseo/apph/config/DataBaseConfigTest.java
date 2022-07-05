package com.viseo.apph.config;

import com.viseo.apph.dao.RoleDao;
import com.viseo.apph.domain.Photo;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.massindexing.MassIndexer;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import static org.mockito.Mockito.*;

public class DataBaseConfigTest {
    DataBaseConfig dataBaseConfig = new DataBaseConfig();

    @Test
    public void testOnApplicationEvent() throws InterruptedException {
        //GIVEN
        dataBaseConfig.init = true;
        EntityManager em = mock(EntityManager.class);
        dataBaseConfig.em = em;
        dataBaseConfig.roleDao = mock(RoleDao.class);
        TypedQuery<Long> typedQuery = mock(TypedQuery.class);
        ContextRefreshedEvent eventMock = mock(ContextRefreshedEvent.class);
        ApplicationContext applicationContextMock = mock(ApplicationContext.class);
        SearchSession searchSession = mock(SearchSession.class);
        MassIndexer indexer = mock(MassIndexer.class);
        when(eventMock.getApplicationContext()).thenReturn(applicationContextMock);
        when(applicationContextMock.getBean(DataBaseConfig.class)).thenReturn(dataBaseConfig);
        when(em.createQuery(anyString(), eq(Long.class))).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(0L);
        try (MockedStatic<Search> search = Mockito.mockStatic(Search.class)) {
            search.when(() -> Search.session(em)).thenReturn(searchSession);
            when(searchSession.massIndexer(Photo.class)).thenReturn(indexer);
            when(indexer.threadsToLoadObjects(anyInt())).thenReturn(indexer);
            //WHEN
            dataBaseConfig.onApplicationEvent(eventMock);
        }
        //THEN
    }
}
