package com.viseo.apph.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.GsonBuilder;
import com.viseo.apph.dao.FolderDao;
import com.viseo.apph.dao.PhotoDao;
import com.viseo.apph.dao.S3Dao;
import com.viseo.apph.dao.UserDao;
import com.viseo.apph.domain.Folder;
import com.viseo.apph.domain.Photo;
import com.viseo.apph.domain.Tag;
import com.viseo.apph.domain.User;
import com.viseo.apph.dto.*;
import com.viseo.apph.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class PhotoService {

    @Autowired
    PhotoDao photoDao;
    @Autowired
    UserDao userDao;
    @Autowired
    TagService tagService;
    @Autowired
    FolderDao folderDao;
    @Autowired
    S3Dao s3Dao;
    @Value("${max-zip-size-mb}")
    public long zipMaxSize;

    @Transactional
    public String addPhoto(User user, PhotoRequest photoRequest) throws InvalidFileException, IOException, NotFoundException, UnauthorizedException, ConflictException {
        Folder folder = null;
        if (photoRequest.getTitle().length() > 255 || photoRequest.getDescription().length() > 255)
            throw new IllegalArgumentException("Le titre ou la description ne peuvent pas dépasser les 255 caractères.");
        if (photoRequest.getFolderId() >= 0) {
            folder = folderDao.getFolderById(photoRequest.getFolderId());
            if (folder == null)
                throw new NotFoundException("Le dossier n'existe pas.");
            if (folder.getUser().getId() != user.getId())
                throw new UnauthorizedException("L'utilisateur n'a pas accès à ce dossier.");
        }
        if (photoDao.existNameInFolder(folder, photoRequest.getTitle(), getFormat(photoRequest.getFile())))
            throw new ConflictException("Titre déjà utilisé dans le dossier.");
        Set<Tag> allTags = tagService.createListTags(photoRequest.getTags(), user);
        Date shootingDate = photoRequest.getShootingDate() != null ? new GsonBuilder().setDateFormat("dd/MM/yyyy, hh:mm:ss").create().fromJson(photoRequest.getShootingDate(), Date.class) : new Date();
        Photo photo = new Photo()
                .setTitle(photoRequest.getTitle())
                .setFormat(getFormat(photoRequest.getFile()))
                .setUser(user)
                .setSize((photoRequest.getFile().getSize() + .0F) / 1024)
                .setDescription(photoRequest.getDescription())
                .setCreationDate(new Date())
                .setModificationDate(new Date())
                .setShootingDate(shootingDate)
                .setTags(allTags)
                .setFolder(folder);
        photo = photoDao.addPhoto(photo);
        return s3Dao.upload(photoRequest.getFile(), photo);
    }

    @Transactional
    public String editPhotoInfos(User user, PhotoRequest photoRequest) throws NotFoundException {
        Photo photo = photoDao.getPhoto(photoRequest.getId());
        if (photo == null) throw new NotFoundException("photo not found");
        Set<Tag> newTags = tagService.createListTags(photoRequest.getTags(), user);
        Date shootingDate = photoRequest.getShootingDate() != null ? new GsonBuilder().setDateFormat("dd/MM/yyyy, hh:mm:ss").create().fromJson(photoRequest.getShootingDate(), Date.class) : new Date();
        photo.setTitle(photoRequest.getTitle())
             .setDescription(photoRequest.getDescription())
             .setShootingDate(shootingDate)
             .setTags(newTags);
        return "Photo édité";
    }

    public String getFormat(MultipartFile file) throws InvalidFileException {
        String contentType = file.getContentType();
        if (contentType != null && contentType.startsWith("image/")) {
            String[] types = contentType.split("/");
            return "." + types[1];
        } else {
            throw new InvalidFileException("Wrong file format");
        }
    }

    @Transactional
    public PaginationResponse getUserPhotos(User user, FilterRequest filterRequest) {
        List<Photo> userPhotos = photoDao.getUserPhotos(user);
        return getPaginationResponse(filterRequest.getPageSize(), filterRequest.getPage(), userPhotos);
    }

    public PhotoResponse download(Long userId, PhotoRequest photoRequest) throws FileNotFoundException, UnauthorizedException {
        Photo photo = photoDao.getPhoto(photoRequest.getId());
        if (photo == null) {
            throw new FileNotFoundException();
        }
        if (userId != photo.getUser().getId()) {
            throw new UnauthorizedException("L'utilisateur n'est pas autorisé à accéder à la ressource demandée");
        }
        byte[] photoByte = s3Dao.download(photo);
        return new PhotoResponse().setData(photoByte).setTitle(photo.getTitle()).setFormat(photo.getFormat());
    }

    @Transactional
    public void deletePhotos(User user, long[] ids) {
        for (long id : ids) {
            Photo photo = photoDao.getPhoto(id);
            if (photo != null && photo.getUser().getId() == user.getId()) {
                s3Dao.delete(photo);
                photoDao.deletePhoto(photo);
            }
        }
    }

    @Transactional
    public PhotoListResponse getPhotosByFolder(long folderId, User user) throws NotFoundException, UnauthorizedException {
        Folder folder = folderDao.getFolderById(folderId);
        if (folder == null) throw new NotFoundException("Le dossier n'existe pas.");
        if (folder.getUser().getId() != user.getId())
            throw new UnauthorizedException("L'utilisateur n'a pas accès à ce dossier.");
        List<Photo> photoList = photoDao.getPhotosByFolder(folder);
        PhotoListResponse response = new PhotoListResponse();
        photoList.forEach(photo -> response.addPhoto(new PhotoResponse()
                .setId(photo.getId())
                .setTitle(photo.getTitle())
                .setCreationDate(photo.getCreationDate())
                .setModificationDate(photo.getModificationDate())
                .setSize(photo.getSize())
                .setTags(photo.getTags())
                .setDescription(photo.getDescription())
                .setShootingDate(photo.getShootingDate())
                .setUrl(s3Dao.getPhotoUrl(photo))
                .setFormat(photo.getFormat())
        ));
        return response;
    }

    @Transactional
    public MessageListResponse movePhotosToFolder(User user, PhotosRequest request) throws NotFoundException, UnauthorizedException {
        Folder folder = folderDao.getFolderById(request.getFolderId());
        if (folder == null) throw new NotFoundException("Le dossier n'existe pas.");
        if (folder.getUser().getId() != user.getId())
            throw new UnauthorizedException("L'utilisateur n'a pas accès au dossier.");
        MessageListResponse response = new MessageListResponse();
        for (long id : request.getIds()) {
            Photo photo = photoDao.getPhoto(id);
            if (photo == null) {
                response.addMessage("error: L'une des photos n'existe pas.");
            } else if (photo.getUser().getId() != user.getId()) {
                response.addMessage("error: L'une des photos n'appartient pas à l'utilisateur.");
            } else if (photo.getFolder() != null && photo.getFolder().getId() == folder.getId()) {
                response.addMessage("warning: L'une des photos est déjà dans le dossier.");
            } else if (photoDao.existNameInFolder(folder, photo.getTitle(), photo.getFormat())) {
                response.addMessage("error: L'une des photos comporte un nom existant déjà dans le dossier destinataire.");
            } else {
                photo.setFolder(folder);
                photo.setModificationDate(new Date());
            }
        }
        response.addMessage("success: Le déplacement des photos est terminé.");
        return response;
    }

    private String createFilterQuery(FilterDto[] filters) throws InvalidObjectException {
        StringBuilder query = new StringBuilder("SELECT p FROM Photo p JOIN p.tags t WHERE p.user = :user");
        List<FilterDto> filterDtoList = Arrays.asList(filters);
        filterDtoList.sort(FilterDto::compareTo);
        String lastField = "first";
        for (FilterDto filter : filterDtoList) {
            if (!Objects.equals(filter.getField(), lastField)) {
                if (!lastField.equals("first")) {query.append(") AND");}
                if (lastField.equals("first")) {query.append(" AND");}
                query.append(" (");
                lastField = filter.getField();
            } else {
                query.append(" OR ");
            }
            query.append(filter.getFieldToSql()).append(" ");
            query.append(filter.getOperatorToSql()).append(" ");
            query.append(filter.getValueToSql()).append(" ");
        }
        if (!filterDtoList.isEmpty()) {query.append(")");}
        query.append(" GROUP BY p.id");
        return query.toString();
    }

    @Transactional
    public PaginationResponse getUserFilteredPhotos(User user, FilterRequest filterRequest) throws InvalidObjectException {
        String filterQuery = createFilterQuery(filterRequest.getFilters());
        List<Photo> userPhotos = photoDao.getUserFilteredPhotos(user, filterQuery);
        return getPaginationResponse(filterRequest.getPageSize(), filterRequest.getPage(), userPhotos);
    }

    private PaginationResponse getPaginationResponse(int pageSize, int page, List<Photo> userPhotos) {
        int startIndex = (page - 1) * pageSize;
        int endIndex = page * pageSize;
        PaginationResponse response = new PaginationResponse().setTotalSize(userPhotos.size());
        List<PhotoResponse> responseList = userPhotos.subList(startIndex, Math.min(endIndex, userPhotos.size())).stream()
                .map(photo -> new PhotoResponse()
                        .setId(photo.getId())
                        .setTitle(photo.getTitle())
                        .setCreationDate(photo.getCreationDate())
                        .setModificationDate(photo.getModificationDate())
                        .setSize(photo.getSize())
                        .setTags(photo.getTags())
                        .setDescription(photo.getDescription())
                        .setShootingDate(photo.getShootingDate())
                        .setUrl(s3Dao.getPhotoUrl(photo))
                        .setFormat(photo.getFormat())
                ).collect(Collectors.toList());
        for (PhotoResponse photo : responseList) {
            response.addPhoto(photo);
        }
        return response;
    }

    public PhotoResponse downloadZip(User user, PhotosRequest photosRequest) throws UnauthorizedException, IOException, MaxSizeExceededException {
        long[] ids = photosRequest.getIds();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ZipOutputStream zipOut = new ZipOutputStream(bos);
        Set<String> names = new HashSet<>();
        for (Long id : ids) {
            Photo photo = photoDao.getPhoto(id);
            if (photo == null) {
                throw new FileNotFoundException();
            }
            if (user.getId() != photo.getUser().getId()) {
                throw new UnauthorizedException("L'utilisateur n'est pas autorisé à accéder à la ressource demandée");
            }
            byte[] photoByte = s3Dao.download(photo);
            String name = photo.getTitle() + photo.getFormat();
            for (int i = 1; !names.add(name); i++)
                name = photo.getTitle() + "_" + i + photo.getFormat();
            ZipEntry zipEntry = new ZipEntry(name);
            zipEntry.setSize(photoByte.length);
            zipOut.putNextEntry(zipEntry);
            zipOut.write(photoByte);
            if (bos.size() > zipMaxSize * 1024 * 1024) {
                throw new MaxSizeExceededException("Erreur: Taille maximale du ZIP dépassée.");
            }
        }
        zipOut.closeEntry();
        zipOut.close();
        return new PhotoResponse().setData(bos.toByteArray()).setTitle(photosRequest.getTitleZip()).setFormat(".zip");
    }

    @Transactional
    public String changePhotoFile(Long userId, PhotoRequest photoRequest) throws IOException, UnauthorizedException, InvalidFileException {
        Photo photo = photoDao.getPhoto(photoRequest.getId());
        if (photo == null) {
            throw new FileNotFoundException("Le fichier n'existe pas");
        }
        if (userId != photo.getUser().getId()) {
            throw new UnauthorizedException("L'utilisateur n'est pas autorisé à accéder à la ressource demandée");
        }
        s3Dao.delete(photo);
        photo.setFormat(getFormat(photoRequest.getFile())).setSize((photoRequest.getFile().getSize() + .0F) / 1024);
        return s3Dao.upload(photoRequest.getFile(), photo);
    }
}
