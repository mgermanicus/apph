package com.viseo.apph.config;

import com.viseo.apph.dao.RoleDao;
import com.viseo.apph.dao.SettingDao;
import com.viseo.apph.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.MultipartConfigElement;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Component
public class DataBaseConfig {
    @PersistenceContext
    EntityManager em;

    @Autowired
    RoleDao roleDao;

    @Autowired
    SettingDao settingDao;

    @Value("${init-database}")
    boolean init;

    PasswordEncoder encoder = new BCryptPasswordEncoder();

    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent(ContextRefreshedEvent event) {
        event.getApplicationContext().getBean(DataBaseConfig.class).initializeRole();
        if (this.init) {
            event.getApplicationContext().getBean(DataBaseConfig.class).initializeAdmin();
            event.getApplicationContext().getBean(DataBaseConfig.class).initialize();
        }
    }

    @Transactional
    public void initializeRole() {
        long count = em.createQuery("SELECT COUNT(role) FROM Role role", Long.class).getSingleResult();
        if (count == 0) {
            Role roleAdmin = new Role(ERole.ROLE_ADMIN);
            Role roleUser = new Role(ERole.ROLE_USER);
            em.persist(roleUser);
            em.persist(roleAdmin);
        }
    }

    @Transactional
    public void initializeAdmin() {
        Role roleAdmin = roleDao.getRole(ERole.ROLE_ADMIN);
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(roleAdmin);
        User admin = new User().setLogin("admin@viseo.com").setPassword(encoder.encode("c1c224b03cd9bc7b6a86d77f5dace40191766c485cd55dc48caf9ac873335d6f"))
                .setFirstname("Admin").setLastname("VISEO").setRoles(roleSet);
        em.persist(admin);
        Folder adminRoot = new Folder().setName("Admin VISEO").setParentFolderId(null).setUser(admin);
        em.persist(adminRoot);
        Setting setting = new Setting().setUploadSize(10).setDownloadSize(15);
        em.persist(setting);
    }

    @Transactional
    public void initialize() {
        this.init = false;
        //Role
        Role roleUser = roleDao.getRole(ERole.ROLE_USER);
        //User
        Set<Role> set = new HashSet<>();
        set.add(roleUser);
        User alexandre = new User().setLogin("alexandre@viseo.com").setPassword(encoder.encode("13e15721c9d4ad58d34983344dfba265a90d80f63db77c2eb3804379d9608889"))
                .setFirstname("Alexandre").setLastname("HU").setRoles(set);
        User baptiste = new User().setLogin("baptiste@viseo.com").setPassword(encoder.encode("15cc3b2994423d897d1e1ba43a670870fda7c4d62548416603a8ddddf7b9e06e"))
                .setFirstname("Baptiste").setLastname("MONFRAY").setRoles(set);
        User wassim = new User().setLogin("wassim@viseo.com").setPassword(encoder.encode("789e40562e8ad7d8e789a970cf432f52ab355fb45267f1116f597d08b1f7455f"))
                .setFirstname("Wassim").setLastname("BOUHTOUT").setRoles(set);
        User min = new User().setLogin("min@viseo.com").setPassword(encoder.encode("dea79332147ffe1fb2a81cf9a5bdf0066ddcc625699996ede0ce140e5cb004b1"))
                .setFirstname("Min").setLastname("SUN").setRoles(set);
        User elie = new User().setLogin("elie@viseo.com").setPassword(encoder.encode("281dc093e8ea1bd931774d7a28dccb50e3a307756b7ff07bad897f10a56bfde0"))
                .setFirstname("Elie").setLastname("RAVASSE").setRoles(set);
        User larbi = new User().setLogin("larbi@viseo.com").setPassword(encoder.encode("d1261e513fd80525a4fd227bfa52718ba27beb58fc1e52f49283a349a146b5c5"))
                .setFirstname("Larbi").setLastname("AIT MOHAMED").setRoles(set);
        User doryan = new User().setLogin("doryan@viseo.com").setPassword(encoder.encode("f44f6a73eb463fbddf089f93385b3b743cfc6397e3aa30a73e89d29dbef1ef1c"))
                .setFirstname("Doryan").setLastname("DAHON").setRoles(set);
        User yunan = new User().setLogin("yunan@viseo.com").setPassword(encoder.encode("04eefe3817d9a64aa70efbd3144ff239f26729fc0bfc98c4f669f14fed0786a7"))
                .setFirstname("Yunan").setLastname("HOU").setRoles(set);
        User manon = new User().setLogin("manon@viseo.com").setPassword(encoder.encode("190987e7c02f48b91221bc4914ee26eaec486dab371296a53a5e7e583cdfc663"))
                .setFirstname("Manon").setLastname("GERMANICUS").setRoles(set);
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
        //Elie's tags
        Tag elieT1 = new Tag().setName("Elie_T1").setUser(elie);
        Tag elieT2 = new Tag().setName("Elie_T2").setUser(elie);
        em.persist(elieT1);
        em.persist(elieT2);
        //Photo
        Photo photo1 = new Photo().setFormat(".jpeg").setUser(alexandre).setSize(1200).addTag(elieT1).setTitle("photo").setDescription("photo test").setCreationDate(new Date()).setModificationDate(new Date()).setShootingDate(new Date()).setFolder(alexandreRoot);
        // NOTE : you need a file named 1.jpeg in your S3 folder with this photo in the database
        Photo photo2 = new Photo().setFormat(".png").setUser(alexandre).setSize(1300).addTag(elieT1).setTitle("photo2").setDescription("photo test 2").setCreationDate(new Date(129538983)).setModificationDate(new Date(129538983)).setShootingDate(new Date()).setFolder(alexandreRoot);
        // NOTE : you need a file named 2.png in your S3 folder with this photo in the database
        em.persist(photo1);
        em.persist(photo2);
        //Elie's Photo
        Photo eliePhoto1 = new Photo().setUser(elie).setSize(1300).addTag(elieT1).setTitle("photo1").setDescription("photo test 1 dans root").setCreationDate(new Date()).setModificationDate(new Date()).setShootingDate(new Date()).setFolder(elieRoot);
        Photo eliePhoto2 = new Photo().setUser(elie).setSize(1300).addTag(elieT1).setTitle("photo2").setDescription("photo test 2 dans root").setCreationDate(new Date()).setModificationDate(new Date()).setShootingDate(new Date()).setFolder(elieRoot);
        Photo eliePhoto3 = new Photo().setUser(elie).setSize(1300).addTag(elieT1).setTitle("photo3").setDescription("photo test 3 dans root").setCreationDate(new Date()).setModificationDate(new Date()).setShootingDate(new Date()).setFolder(elieRoot);
        Photo eliePhoto4 = new Photo().setUser(elie).setSize(1300).addTag(elieT1).setTitle("photo4").setDescription("photo test 4 dans root").setCreationDate(new Date()).setModificationDate(new Date()).setShootingDate(new Date()).setFolder(elieRoot);
        Photo eliePhoto5 = new Photo().setUser(elie).setSize(1300).addTag(elieT1).setTitle("photo5").setDescription("photo test 5 dans root").setCreationDate(new Date()).setModificationDate(new Date()).setShootingDate(new Date()).setFolder(elieRoot);
        Photo eliePhoto6 = new Photo().setUser(elie).setSize(1300).addTag(elieT1).setTitle("photo6").setDescription("photo test 6 dans root").setCreationDate(new Date()).setModificationDate(new Date()).setShootingDate(new Date()).setFolder(elieRoot);
        Photo eliePhoto7 = new Photo().setUser(elie).setSize(1300).addTag(elieT1).setTitle("photo7").setDescription("photo test 7 dans child 1").setCreationDate(new Date()).setModificationDate(new Date()).setShootingDate(new Date()).setFolder(elieChild1);
        Photo eliePhoto8 = new Photo().setUser(elie).setSize(1300).addTag(elieT1).setTitle("photo8").setDescription("photo test 8 dans child 1").setCreationDate(new Date()).setModificationDate(new Date()).setShootingDate(new Date()).setFolder(elieChild1);
        Photo eliePhoto9 = new Photo().setUser(elie).setSize(1300).addTag(elieT1).setTitle("photo9").setDescription("photo test 9 dans child 1").setCreationDate(new Date()).setModificationDate(new Date()).setShootingDate(new Date()).setFolder(elieChild1);
        Photo eliePhoto10 = new Photo().setUser(elie).setSize(1300).addTag(elieT2).setTitle("photo10").setDescription("photo test 10 dans child 1").setCreationDate(new Date()).setModificationDate(new Date()).setShootingDate(new Date()).setFolder(elieChild1);
        Photo eliePhoto11 = new Photo().setUser(elie).setSize(1300).addTag(elieT2).setTitle("photo11").setDescription("photo test 11 dans child 1").setCreationDate(new Date()).setModificationDate(new Date()).setShootingDate(new Date()).setFolder(elieChild1);
        Photo eliePhoto12 = new Photo().setUser(elie).setSize(1300).addTag(elieT2).setTitle("photo12").setDescription("photo test 12 dans child 1").setCreationDate(new Date()).setModificationDate(new Date()).setShootingDate(new Date()).setFolder(elieChild1);
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
