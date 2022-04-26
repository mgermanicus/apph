package com.viseo.apph.config;

import com.viseo.apph.domain.Photo;
import com.viseo.apph.domain.User;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    PasswordEncoder encoder = new BCryptPasswordEncoder();

    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (!this.init) {
            event.getApplicationContext().getBean(DataBaseConfig.class).initialize();
        }
    }

    @Transactional
    public void initialize() {
        this.init = true;
        User alexandre = new User().setLogin("Alexandre").setPassword(encoder.encode("13e15721c9d4ad58d34983344dfba265a90d80f63db77c2eb3804379d9608889"));
        User baptiste = new User().setLogin("Baptiste").setPassword(encoder.encode("15cc3b2994423d897d1e1ba43a670870fda7c4d62548416603a8ddddf7b9e06e"));
        User wassim = new User().setLogin("Wassim").setPassword(encoder.encode("789e40562e8ad7d8e789a970cf432f52ab355fb45267f1116f597d08b1f7455f"));
        User min = new User().setLogin("Min").setPassword(encoder.encode("dea79332147ffe1fb2a81cf9a5bdf0066ddcc625699996ede0ce140e5cb004b1"));
        User elie = new User().setLogin("Elie").setPassword(encoder.encode("281dc093e8ea1bd931774d7a28dccb50e3a307756b7ff07bad897f10a56bfde0"));
        User larbi = new User().setLogin("Larbi").setPassword(encoder.encode("d1261e513fd80525a4fd227bfa52718ba27beb58fc1e52f49283a349a146b5c5"));
        User doryan = new User().setLogin("Doryan").setPassword(encoder.encode("f44f6a73eb463fbddf089f93385b3b743cfc6397e3aa30a73e89d29dbef1ef1c"));
        User yunan = new User().setLogin("Yunan").setPassword(encoder.encode("04eefe3817d9a64aa70efbd3144ff239f26729fc0bfc98c4f669f14fed0786a7"));
        User manon = new User().setLogin("Manon").setPassword(encoder.encode("190987e7c02f48b91221bc4914ee26eaec486dab371296a53a5e7e583cdfc663"));
        Photo photo1 = new Photo().setIdUser(1).setSize(1200).setTags("img").setTitle("photo").setDescription("photo test").setCreationDate(new Date()).setShootingDate(new Date());
        Photo photo2 = new Photo().setIdUser(1).setSize(1300).setTags("img2").setTitle("photo2").setDescription("photo test 2").setCreationDate(new Date()).setShootingDate(new Date());
        em.persist(alexandre);
        em.persist(baptiste);
        em.persist(wassim);
        em.persist(min);
        em.persist(elie);
        em.persist(larbi);
        em.persist(doryan);
        em.persist(yunan);
        em.persist(manon);
        em.persist(photo1);
        em.persist(photo2);
    }
}
