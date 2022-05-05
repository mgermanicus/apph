package com.viseo.apph.config;

import com.viseo.apph.domain.Folder;
import com.viseo.apph.domain.Photo;
import com.viseo.apph.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Component
public class DataBaseConfig {
    @PersistenceContext
    EntityManager em;

    @Value("${init-database}")
    boolean init;

    PasswordEncoder encoder = new BCryptPasswordEncoder();

    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (this.init) {
            event.getApplicationContext().getBean(DataBaseConfig.class).initialize();
        }
    }

    @Transactional
    public void initialize() {
        this.init = false;
        //User
        User alexandre = new User().setLogin("Alexandre@viseo.com").setPassword(encoder.encode("13e15721c9d4ad58d34983344dfba265a90d80f63db77c2eb3804379d9608889"))
                .setFirstname("Alexandre").setLastname("HU");
        User baptiste = new User().setLogin("Baptiste@viseo.com").setPassword(encoder.encode("15cc3b2994423d897d1e1ba43a670870fda7c4d62548416603a8ddddf7b9e06e"))
                .setFirstname("Baptiste").setLastname("MONFRAY");
        User wassim = new User().setLogin("Wassim@viseo.com").setPassword(encoder.encode("789e40562e8ad7d8e789a970cf432f52ab355fb45267f1116f597d08b1f7455f"))
                .setFirstname("Wassim").setLastname("BOUHTOUT");
        User min = new User().setLogin("Min@viseo.com").setPassword(encoder.encode("dea79332147ffe1fb2a81cf9a5bdf0066ddcc625699996ede0ce140e5cb004b1"))
                .setFirstname("Min").setLastname("SUN");
        User elie = new User().setLogin("Elie@viseo.com").setPassword(encoder.encode("281dc093e8ea1bd931774d7a28dccb50e3a307756b7ff07bad897f10a56bfde0"))
                .setFirstname("Elie").setLastname("RAVASSE");
        User larbi = new User().setLogin("Larbi@viseo.com").setPassword(encoder.encode("d1261e513fd80525a4fd227bfa52718ba27beb58fc1e52f49283a349a146b5c5"))
                .setFirstname("Larbi").setLastname("AIT MOHAMED");
        User doryan = new User().setLogin("Doryan@viseo.com").setPassword(encoder.encode("f44f6a73eb463fbddf089f93385b3b743cfc6397e3aa30a73e89d29dbef1ef1c"))
                .setFirstname("Doryan").setLastname("DAHON");
        User yunan = new User().setLogin("Yunan@viseo.com").setPassword(encoder.encode("04eefe3817d9a64aa70efbd3144ff239f26729fc0bfc98c4f669f14fed0786a7"))
                .setFirstname("Yunan").setLastname("HOU");
        User manon = new User().setLogin("Manon@viseo.com").setPassword(encoder.encode("190987e7c02f48b91221bc4914ee26eaec486dab371296a53a5e7e583cdfc663"))
                .setFirstname("Manon").setLastname("GERMANICUS");
        em.persist(alexandre);
        em.persist(baptiste);
        em.persist(wassim);
        em.persist(min);
        em.persist(elie);
        em.persist(larbi);
        em.persist(doryan);
        em.persist(yunan);
        em.persist(manon);
        //Elie's folders
        Folder elieRoot = new Folder().setName("Elie_root").setParentFolderId(null).setUser(elie);
        em.persist(elieRoot);
        Folder elieChild1 = new Folder().setName("Elie_child_1").setParentFolderId(elieRoot.getId()).setUser(elie);
        Folder elieChild2 = new Folder().setName("Elie_child_2").setParentFolderId(elieRoot.getId()).setUser(elie);
        em.persist(elieChild1);
        em.persist(elieChild2);
        Folder elieGrandchildOf1 = new Folder().setName("Elie_grandchild_of_1").setParentFolderId(elieChild1.getId()).setUser(elie);
        Folder elieGrandchildOf2 = new Folder().setName("Elie_grandchild_of_2").setParentFolderId(elieChild2.getId()).setUser(elie);
        em.persist(elieGrandchildOf1);
        em.persist(elieGrandchildOf2);
        //Yunan's folders
        Folder yunanRoot = new Folder().setName("Yunan_root").setParentFolderId(null).setUser(yunan);
        em.persist(yunanRoot);
        Folder yunanChild1 = new Folder().setName("Yunan_child_1").setParentFolderId(yunanRoot.getId()).setUser(yunan);
        Folder yunanChild2 = new Folder().setName("Yunan_child_2").setParentFolderId(yunanRoot.getId()).setUser(yunan);
        em.persist(yunanChild1);
        em.persist(yunanChild2);
        //Other's root folder
        Folder alexandreRoot = new Folder().setName("Alexandre_root").setParentFolderId(null).setUser(alexandre);
        Folder baptisteRoot = new Folder().setName("Baptiste_root").setParentFolderId(null).setUser(baptiste);
        Folder wassimRoot = new Folder().setName("Wassim_root").setParentFolderId(null).setUser(wassim);
        Folder minRoot = new Folder().setName("Min_root").setParentFolderId(null).setUser(min);
        Folder larbiRoot = new Folder().setName("Larbi_root").setParentFolderId(null).setUser(larbi);
        Folder doryanRoot = new Folder().setName("Doryan_root").setParentFolderId(null).setUser(doryan);
        Folder manonRoot = new Folder().setName("Manon_root").setParentFolderId(null).setUser(manon);
        em.persist(alexandreRoot);
        em.persist(baptisteRoot);
        em.persist(wassimRoot);
        em.persist(minRoot);
        em.persist(larbiRoot);
        em.persist(doryanRoot);
        em.persist(manonRoot);
        //Photo
        Photo photo1 = new Photo().setFormat(".jpeg").setUser(alexandre).setSize(1200).setTags(Collections.singleton("img")).setTitle("photo").setDescription("photo test").setCreationDate(new Date()).setShootingDate(new Date());
        // NOTE : you need a file named 1.jpeg in your S3 folder with this photo in the database
        Photo photo2 = new Photo().setFormat(".png").setUser(alexandre).setSize(1300).setTags(Collections.singleton("img2")).setTitle("photo2").setDescription("photo test 2").setCreationDate(new Date(129538983)).setShootingDate(new Date());
        // NOTE : you need a file named 2.png in your S3 folder with this photo in the database
        em.persist(photo1);
        em.persist(photo2);
        //Elie's Photo
        Photo eliePhoto1 = new Photo().setIdUser(elie.getId()).setSize(1300).setTags("img1").setTitle("photo1").setDescription("photo test 1").setCreationDate(new Date()).setShootingDate(new Date());
        Photo eliePhoto2 = new Photo().setIdUser(elie.getId()).setSize(1300).setTags("img2").setTitle("photo2").setDescription("photo test 2").setCreationDate(new Date()).setShootingDate(new Date());
        Photo eliePhoto3 = new Photo().setIdUser(elie.getId()).setSize(1300).setTags("img3").setTitle("photo3").setDescription("photo test 3").setCreationDate(new Date()).setShootingDate(new Date());
        Photo eliePhoto4 = new Photo().setIdUser(elie.getId()).setSize(1300).setTags("img4").setTitle("photo4").setDescription("photo test 4").setCreationDate(new Date()).setShootingDate(new Date());
        Photo eliePhoto5 = new Photo().setIdUser(elie.getId()).setSize(1300).setTags("img5").setTitle("photo5").setDescription("photo test 5").setCreationDate(new Date()).setShootingDate(new Date());
        Photo eliePhoto6 = new Photo().setIdUser(elie.getId()).setSize(1300).setTags("img6").setTitle("photo6").setDescription("photo test 6").setCreationDate(new Date()).setShootingDate(new Date());
        Photo eliePhoto7 = new Photo().setIdUser(elie.getId()).setSize(1300).setTags("img7").setTitle("photo7").setDescription("photo test 7").setCreationDate(new Date()).setShootingDate(new Date());
        Photo eliePhoto8 = new Photo().setIdUser(elie.getId()).setSize(1300).setTags("img8").setTitle("photo8").setDescription("photo test 8").setCreationDate(new Date()).setShootingDate(new Date());
        Photo eliePhoto9 = new Photo().setIdUser(elie.getId()).setSize(1300).setTags("img9").setTitle("photo9").setDescription("photo test 9").setCreationDate(new Date()).setShootingDate(new Date());
        Photo eliePhoto10 = new Photo().setIdUser(elie.getId()).setSize(1300).setTags("img10").setTitle("photo10").setDescription("photo test 10").setCreationDate(new Date()).setShootingDate(new Date());
        Photo eliePhoto11 = new Photo().setIdUser(elie.getId()).setSize(1300).setTags("img11").setTitle("photo11").setDescription("photo test 11").setCreationDate(new Date()).setShootingDate(new Date());
        Photo eliePhoto12 = new Photo().setIdUser(elie.getId()).setSize(1300).setTags("img12").setTitle("photo12").setDescription("photo test 12").setCreationDate(new Date()).setShootingDate(new Date());
        em.persist(eliePhoto1);
        em.persist(eliePhoto2);
        em.persist(eliePhoto3);
        em.persist(eliePhoto4);
        em.persist(eliePhoto5);
        em.persist(eliePhoto6);
        em.persist(eliePhoto7);
        em.persist(eliePhoto8);
        em.persist(eliePhoto9);
        em.persist(eliePhoto10);
        em.persist(eliePhoto11);
        em.persist(eliePhoto12);
    }
}
