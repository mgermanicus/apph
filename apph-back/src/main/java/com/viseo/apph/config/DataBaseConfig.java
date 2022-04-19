package com.viseo.apph.config;

import com.viseo.apph.domain.User;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

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
        User alexandre = new User().setLogin("Alexandre").setPassword("Alexandre");
        User baptiste = new User().setLogin("Baptiste").setPassword("Baptiste");
        User wassim = new User().setLogin("Wassim").setPassword("Wassim");
        User min = new User().setLogin("Alexandre").setPassword("Alexandre");
        User elie = new User().setLogin("Elie").setPassword("Elie");
        User larbi = new User().setLogin("Larbi").setPassword("Larbi");
        User doryan = new User().setLogin("Doryan").setPassword("Doryan");
        User yunan = new User().setLogin("Yunan").setPassword("Yunan");
        User manon = new User().setLogin("Manon").setPassword("Manon");
        em.persist(alexandre);
        em.persist(baptiste);
        em.persist(wassim);
        em.persist(min);
        em.persist(elie);
        em.persist(larbi);
        em.persist(doryan);
        em.persist(yunan);
        em.persist(manon);

    }

}
