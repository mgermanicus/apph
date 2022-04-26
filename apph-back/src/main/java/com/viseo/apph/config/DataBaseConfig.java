package com.viseo.apph.config;

import com.viseo.apph.domain.Photo;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Date;

@Component
public class DataBaseConfig {

    @PersistenceContext
    EntityManager em;

    boolean init = false;

    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (!this.init) {
            event.getApplicationContext().getBean(DataBaseConfig.class).initialize();
        }
    }

    @Transactional
    public void initialize()
    {
        this.init=true;
        Photo photo1 = new Photo().setIdUser(1).setSize(1200).setTags("img").setTitle("photo").setDescription("photo test").setCreationDate(new Date()).setShootingDate(new Date());
        Photo photo2 = new Photo().setIdUser(1).setSize(1300).setTags("img2").setTitle("photo2").setDescription("photo test 2").setCreationDate(new Date()).setShootingDate(new Date());
        em.persist(photo1);
        em.persist(photo2);
    }
}