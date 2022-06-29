package com.viseo.apph.dao;

import com.viseo.apph.domain.Setting;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class SettingDao {
    @PersistenceContext
    EntityManager em;

    public Setting getSetting() {
        return em.createQuery("SELECT setting from Setting setting", Setting.class).getSingleResult();
    }
}
