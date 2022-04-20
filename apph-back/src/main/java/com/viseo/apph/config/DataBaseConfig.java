package com.viseo.apph.config;

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
    }

}