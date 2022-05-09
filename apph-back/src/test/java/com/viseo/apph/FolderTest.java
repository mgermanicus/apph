package com.viseo.apph;

/*
@RunWith(MockitoJUnitRunner.class)
public class FolderTest {
    @Mock
    EntityManager em;

    @Mock
    TypedQuery typedQuery;

    FolderService folderService;
    FolderController folderController;

    private void createFolderController() {
        FolderDAO folderDAO = new FolderDAO();
        inject(folderDAO, "em", em);
        UserDAO userDAO = new UserDAO();
        inject(userDAO, "em", em);
        folderService = new FolderService();
        inject(folderService, "folderDAO", folderDAO);
        inject(folderService, "userDAO", userDAO);
        folderController = new FolderController();
        inject(folderController, "folderService", folderService);
    }

    void inject(Object component,String field, Object injected) {
        try {
            Field compField = component.getClass().getDeclaredField(field);
            compField.setAccessible(true);
            compField.set(component,injected);
        }
        catch(IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetFoldersByUser() {
        //GIVEN
        createFolderController();
        User robert = (User) new User().setLogin("Robert").setPassword("P@ssw0rd").setId(1).setVersion(0);
        Folder robertRoot = (Folder) new Folder().setName("Robert Root").setParentFolderId(null).setUser(robert).setId(1);
        Folder robertChild1 = (Folder) new Folder().setName("Robert Child 1").setParentFolderId(1L).setUser(robert).setId(2);
        Folder robertChild2 = (Folder) new Folder().setName("Robert Child 2").setParentFolderId(1L).setUser(robert).setId(3);
        robert.addFolder(robertRoot).addFolder(robertChild1).addFolder(robertChild2);
        String jws = Jwts.builder().claim("login", robert.getLogin()).setExpiration(new Date(System.currentTimeMillis() + 20000)).signWith(JwtConfig.getKey()).compact();
        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("login", "Robert")).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(robert);
        when(em.createQuery("SELECT folder from Folder folder WHERE folder.user.id = :userId", Folder.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("userId", 1L)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(robert.getFolders());
        //WHEN
        ResponseEntity responseEntity = folderController.getFoldersByUser(jws);
        //THEN
        Assert.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        FolderResponse folderResponse = (FolderResponse) responseEntity.getBody();
        Assert.assertEquals("Robert Root", folderResponse.getName());
        Assert.assertEquals(1, folderResponse.getId());
        Assert.assertEquals(0, folderResponse.getVersion());
        Assert.assertEquals(2, folderResponse.getChildrenFolders().size());
        Assert.assertNull(folderResponse.getParentFolderId());
    }

    @Test
    public void testGetFoldersByUserNoUser() {
        //GIVEN
        createFolderController();
        String jws = Jwts.builder().claim("login", "Not a User").setExpiration(new Date(System.currentTimeMillis() + 20000)).signWith(JwtConfig.getKey()).compact();
        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("login", "Not a User")).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenThrow(new NoResultException());
        //WHEN
        ResponseEntity responseEntity = folderController.getFoldersByUser(jws);
        //THEN
        Assert.assertEquals(responseEntity.getStatusCode(), HttpStatus.NOT_FOUND);
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        Assert.assertEquals("L'utilisateur n'existe pas.", messageResponse.getMessage());
    }

    @Test
    public void testGetFoldersByUserNoParentFolder() {
        //GIVEN
        createFolderController();
        User robert = (User) new User().setLogin("Robert").setPassword("P@ssw0rd").setId(1).setVersion(0);
        Folder folder = (Folder) new Folder().setName("Folder").setParentFolderId(1L).setId(1);
        List<Folder> folders = new ArrayList<>();
        folders.add(folder);
        String jws = Jwts.builder().claim("login", robert.getLogin()).setExpiration(new Date(System.currentTimeMillis() + 20000)).signWith(JwtConfig.getKey()).compact();
        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("login", "Robert")).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(robert);
        when(em.createQuery("SELECT folder from Folder folder WHERE folder.user.id = :userId", Folder.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("userId", 1L)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(folders);
        //WHEN
        ResponseEntity responseEntity = folderController.getFoldersByUser(jws);
        //THEN
        Assert.assertEquals(responseEntity.getStatusCode(), HttpStatus.NOT_FOUND);
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        Assert.assertEquals("Dossier parent introuvable.", messageResponse.getMessage());
    }

    @Test
    public void testCreateFolder() {
        //GIVEN
        createFolderController();
        FolderRequest request = new FolderRequest().setName("Robert Child").setParentFolderId(1L);
        User robert = (User) new User().setLogin("Robert").setPassword("P@ssw0rd").setId(1).setVersion(0);
        Folder robertRoot = (Folder) new Folder().setName("Robert Root").setParentFolderId(null).setUser(robert).setId(1);
        robert.addFolder(robertRoot);
        String jws = Jwts.builder().claim("login", robert.getLogin()).setExpiration(new Date(System.currentTimeMillis() + 20000)).signWith(JwtConfig.getKey()).compact();
        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("login", "Robert")).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(robert);
        when(em.find(Folder.class, 1L)).thenReturn(robertRoot);
        when(em.createQuery("SELECT folder from Folder folder WHERE folder.user.id = :userId", Folder.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("userId", 1L)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(robert.getFolders());
        //WHEN
        ResponseEntity responseEntity = folderController.createFolder(jws, request);
        //THEN
        Assert.assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        FolderResponse folderResponse = (FolderResponse) responseEntity.getBody();
        Assert.assertEquals("Robert Root", folderResponse.getName());
    }

    @Test
    public void testCreateFolderWithExistingFolder() {
        //GIVEN
        createFolderController();
        FolderRequest request = new FolderRequest().setName("Robert Child").setParentFolderId(1L);
        User robert = (User) new User().setLogin("Robert").setPassword("P@ssw0rd").setId(1).setVersion(0);
        Folder robertRoot = (Folder) new Folder().setName("Robert Root").setParentFolderId(null).setUser(robert).setId(1);
        Folder robertChild = (Folder) new Folder().setName("Robert Child").setParentFolderId(1L).setUser(robert).setId(2);
        robert.addFolder(robertRoot).addFolder(robertChild);
        String jws = Jwts.builder().claim("login", robert.getLogin()).setExpiration(new Date(System.currentTimeMillis() + 20000)).signWith(JwtConfig.getKey()).compact();
        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("login", "Robert")).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(robert);
        when(em.find(Folder.class, 1L)).thenReturn(robertRoot);
        doThrow(new DataIntegrityViolationException("SQLException")).when(em).persist(any());
        //WHEN
        ResponseEntity responseEntity = folderController.createFolder(jws, request);
        //THEN
        Assert.assertEquals(responseEntity.getStatusCode(), HttpStatus.CONFLICT);
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        Assert.assertEquals("Le dossier existe déjà dans le dossier actuel.", messageResponse.getMessage());
    }

    @Test
    public void testCreateFolderWithoutParentFolder() {
        //GIVEN
        createFolderController();
        FolderRequest request = new FolderRequest().setName("Robert Child").setParentFolderId(null);
        User robert = (User) new User().setLogin("Robert").setPassword("P@ssw0rd").setId(1).setVersion(0);
        String jws = Jwts.builder().claim("login", robert.getLogin()).setExpiration(new Date(System.currentTimeMillis() + 20000)).signWith(JwtConfig.getKey()).compact();
        //WHEN
        ResponseEntity responseEntity = folderController.createFolder(jws, request);
        //THEN
        Assert.assertEquals(responseEntity.getStatusCode(), HttpStatus.UNAUTHORIZED);
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        Assert.assertEquals("Impossible de créer un dossier racine.", messageResponse.getMessage());
    }

    @Test
    public void testCreateFolderWithNonExistingParentFolder() {
        //GIVEN
        createFolderController();
        FolderRequest request = new FolderRequest().setName("Robert Child").setParentFolderId(1L);
        User robert = (User) new User().setLogin("Robert").setPassword("P@ssw0rd").setId(1).setVersion(0);
        Folder robertRoot = (Folder) new Folder().setName("Robert Root").setParentFolderId(null).setUser(robert).setId(1);
        Folder robertChild = (Folder) new Folder().setName("Robert Child").setParentFolderId(1L).setUser(robert).setId(2);
        robert.addFolder(robertRoot).addFolder(robertChild);
        String jws = Jwts.builder().claim("login", robert.getLogin()).setExpiration(new Date(System.currentTimeMillis() + 20000)).signWith(JwtConfig.getKey()).compact();
        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("login", "Robert")).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(robert);
        when(em.find(Folder.class, 1L)).thenReturn(null);
        //WHEN
        ResponseEntity responseEntity = folderController.createFolder(jws, request);
        //THEN
        Assert.assertEquals(responseEntity.getStatusCode(), HttpStatus.NOT_FOUND);
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        Assert.assertEquals("Dossier parent introuvable.", messageResponse.getMessage());
    }

    @Test
    public void testCreateFolderDifferentUser() {
        //GIVEN
        createFolderController();
        FolderRequest request = new FolderRequest().setName("Chris Child").setParentFolderId(1L);
        User robert = (User) new User().setLogin("Robert").setPassword("P@ssw0rd").setId(1).setVersion(0);
        User chris = (User) new User().setLogin("Chris").setPassword("P@ssw0rd").setId(2).setVersion(0);
        Folder robertRoot = (Folder) new Folder().setName("Robert Root").setParentFolderId(null).setUser(robert).setId(1);
        robert.addFolder(robertRoot);
        String jws = Jwts.builder().claim("login", chris.getLogin()).setExpiration(new Date(System.currentTimeMillis() + 20000)).signWith(JwtConfig.getKey()).compact();
        when(em.createQuery("SELECT u FROM User u WHERE u.login=:login", User.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("login", "Chris")).thenReturn(typedQuery);
        when(typedQuery.getSingleResult()).thenReturn(chris);
        when(em.find(Folder.class, 1L)).thenReturn(robertRoot);
        //WHEN
        ResponseEntity responseEntity = folderController.createFolder(jws, request);
        //THEN
        Assert.assertEquals(responseEntity.getStatusCode(), HttpStatus.UNAUTHORIZED);
        MessageResponse messageResponse = (MessageResponse) responseEntity.getBody();
        Assert.assertEquals("L'utilisateur n'a pas accès à ce dossier.", messageResponse.getMessage());
    }
}
*/