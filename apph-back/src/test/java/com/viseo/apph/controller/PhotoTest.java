package com.viseo.apph.controller;
/*

@RunWith(MockitoJUnitRunner.Silent.class)
public class PhotoTest {

PhotoController photoController;
@Mock
EntityManager em ;
@Mock
TokenManager tokenManager;
@Mock
TypedQuery typedQuery;
    private void createPhotoController() {
        PhotoDao photoDao = new PhotoDao();
        inject(photoDao,"em",em);
        PhotoService photoService = new PhotoService();
        inject(photoService,"photoDao",photoDao);
        photoController = new PhotoController();
        inject(photoController,"photoService",photoService);
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
    public void testGetInfos()
    {
        //GIVEN
        createPhotoController();
        String token = "token";
        List<Photo> listPhoto = new ArrayList<>();
        listPhoto.add(new Photo());
        PhotoController.tokenManager = tokenManager;

        when(em.createQuery("SELECT p FROM Photo p WHERE p.idUser=:idUser", Photo.class)).thenReturn(typedQuery);
        when(typedQuery.setParameter("idUser", 1L)).thenReturn(typedQuery);
        when(typedQuery.getResultList()).thenReturn(listPhoto);
        when(tokenManager.getIdOfToken("token")).thenReturn(1);
        //WHEN
        ResponseEntity responseEntity = photoController.getUserPhotos(token);
        //THEN
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
    }

}
*/