package com.viseo.apph;

/*
@RunWith(MockitoJUnitRunner.Silent.class)
public class UserTest {
    @Mock
    EntityManager em;
    @Mock
    TypedQuery typedQuery;

    UserService userService;
    UserController userController;

    private void createUserController() {
        UserDAO userDAO = new UserDAO();
        inject(userDAO, "em", em);
        userService = new UserService();
        inject(userService, "userDAO", userDAO);
        userController = new UserController();
        inject(userController, "userService", userService);
    }

    void inject(Object component, String field, Object injected) {
        try {
            Field compField = component.getClass().getDeclaredField(field);
            compField.setAccessible(true);
            compField.set(component, injected);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetUserInfo() {
        //GIVEN
        createUserController();
        User user = new User().setLogin("toto").setPassword("password");
        String jws = Jwts.builder().claim("login", user.getLogin()).setExpiration(new Date(System.currentTimeMillis() + 20000)).signWith(JwtUtils.getKey()).compact();
        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(new User().setLogin("toto").setPassword("password").setFirstname("firstname").setLastname("lastname"));
        when(typedQuery.setParameter("login", "toto")).thenReturn(typedQuery);
        //WHEN
        ResponseEntity responseEntity = userController.getUserInfo(jws);
        //THEN
        Assert.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void testFailUserNotFind() {
        //GIVEN
        createUserController();
        User user = new User().setLogin("toto").setPassword("password");
        String jws = Jwts.builder().claim("login", "dumb_toto").setExpiration(new Date(System.currentTimeMillis() + 20000)).signWith(JwtUtils.getKey()).compact();
        //WHEN
        ResponseEntity responseEntity = userController.getUserInfo(jws);
        //THEN
        Assert.assertEquals(responseEntity.getStatusCode(), HttpStatus.FORBIDDEN);
    }

    @Test
    public void testFailTokenExpired() {
        //GIVEN
        createUserController();
        User user = new User().setLogin("toto").setPassword("password");
        String jws = Jwts.builder().claim("login", user.getLogin()).setExpiration(new Date(System.currentTimeMillis())).signWith(JwtUtils.getKey()).compact();
        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(new User().setLogin("toto").setPassword("password").setFirstname("firstname").setLastname("lastname"));
        //WHEN
        ResponseEntity responseEntity = userController.getUserInfo(jws);
        //THEN
        Assert.assertEquals(responseEntity.getStatusCode(), HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void testFailWrongSignature() {
        //GIVEN
        createUserController();
        User user = new User().setLogin("toto").setPassword("password");
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String jws = Jwts.builder().claim("login", user.getLogin()).setExpiration(new Date(System.currentTimeMillis())).signWith(key).compact();
        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(new User().setLogin("toto").setPassword("password").setFirstname("firstname").setLastname("lastname"));
        //WHEN
        ResponseEntity responseEntity = userController.getUserInfo(jws);
        //THEN
        Assert.assertEquals(responseEntity.getStatusCode(), HttpStatus.UNAUTHORIZED);
    }
}
*/