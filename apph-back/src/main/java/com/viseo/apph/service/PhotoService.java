package com.viseo.apph.service;

import com.google.gson.GsonBuilder;
import com.viseo.apph.dao.*;
import com.viseo.apph.domain.*;
import com.viseo.apph.dto.*;
import com.viseo.apph.exception.*;
import org.hibernate.search.engine.search.query.SearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    @Autowired
    SettingDao settingDao;

    Logger logger = LoggerFactory.getLogger(PhotoService.class);

    @Transactional
    public String addPhoto(User user, PhotoRequest photoRequest) throws InvalidFileException, IOException, NotFoundException, UnauthorizedException, ConflictException, ParseException {
        Folder folder = null;
        if (photoRequest.getTitle().length() > 255 || photoRequest.getDescription().length() > 255) {
            logger.error("title or description over than 255");
            throw new IllegalArgumentException("photo.error.titleOrDescriptionOverChar");
        }
        if (photoRequest.getFolderId() >= 0) {
            folder = folderDao.getFolderById(photoRequest.getFolderId());
            if (folder == null) {
                logger.error("Folder with id: " + photoRequest.getFolderId() + " do not exist");
                throw new NotFoundException("folder.error.notExist");
            }
            if (folder.getUser().getId() != user.getId()) {
                logger.error("User cannot access to the folder");
                throw new UnauthorizedException("folder.error.accessDenied");
            }
        }
        if (photoDao.existNameInFolder(folder, photoRequest.getTitle(), getFormat(photoRequest.getFile()))) {
            logger.error("Folder with name: " + photoRequest.getTitle() + " already exist");
            throw new ConflictException("folder.error.titleAlreadyUsed");
        }
        Set<Tag> allTags = tagService.createListTags(photoRequest.getTags(), user);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/d/yyyy");
        LocalDate shootingDate = LocalDate.parse(photoRequest.getShootingDate(), formatter);
        Location location = new GsonBuilder().create().fromJson(photoRequest.getLocation(), Location.class);
        Photo photo = new Photo()
                .setTitle(photoRequest.getTitle())
                .setFormat(getFormat(photoRequest.getFile()))
                .setUser(user)
                .setSize((photoRequest.getFile().getSize() + .0F) / 1024)
                .setDescription(photoRequest.getDescription())
                .setCreationDate(LocalDate.now())
                .setModificationDate(LocalDate.now())
                .setShootingDate(shootingDate)
                .setTags(allTags)
                .setFolder(folder)
                .setAddress(location.getAddress())
                .setLat(location.getPosition().getLat())
                .setLng(location.getPosition().getLng());
        photo = photoDao.addPhoto(photo);
        return s3Dao.upload(photoRequest.getFile(), photo);
    }

    @Transactional
    public String editPhotoInfos(User user, PhotoRequest photoRequest) throws NotFoundException, ConflictException {
        if (photoRequest.getShootingDate().equals("\"Invalid Date\""))
            throw new IllegalArgumentException("photo.error.invalidDate");
        Photo photo = photoDao.getPhoto(photoRequest.getId());
        if (photo == null)
            throw new NotFoundException("photo.error.notFound");
        if (!photo.getTitle().equals(photoRequest.getTitle()) && photo.getFolder() != null && photoDao.existNameInFolder(photo.getFolder(), photoRequest.getTitle(), photo.getFormat()))
            throw new ConflictException("photo.error.nameExistInFolder");
        Set<Tag> newTags = tagService.createListTags(photoRequest.getTags(), user);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/d/yyyy");
        LocalDate shootingDate = LocalDate.parse(photoRequest.getShootingDate(), formatter);
        Location location = new GsonBuilder().create().fromJson(photoRequest.getLocation(), Location.class);
        photo.setTitle(photoRequest.getTitle())
                .setDescription(photoRequest.getDescription())
                .setShootingDate(shootingDate)
                .setTags(newTags)
                .setAddress(location.getAddress())
                .setLat(location.getPosition().getLat())
                .setLng(location.getPosition().getLng())
                .setModificationDate(LocalDate.now());
        return "photo.edited";
    }

    public String getFormat(MultipartFile file) throws InvalidFileException {
        String contentType = file.getContentType();
        if (contentType != null && contentType.startsWith("image/")) {
            String[] types = contentType.split("/");
            return "." + types[1];
        } else {
            logger.error("Wrong format");
            throw new InvalidFileException("upload.error.wrongFormat");
        }
    }

    @Transactional
    public PaginationResponse getUserPhotos(User user, FilterRequest filterRequest) throws InvalidObjectException {
        FilterQuery filterQuery = createFilterQuery(filterRequest.getFilters());
        String sortQuery = createSortQuery(filterRequest.getSortModel());
        List<Photo> userPhotos = photoDao.getUserPhotos(user, filterQuery.query + sortQuery, filterQuery.argQueue);
        return getPaginationResponse(filterRequest.getPageSize(), filterRequest.getPage(), userPhotos);
    }

    public PhotoResponse download(Long userId, PhotoRequest photoRequest) throws FileNotFoundException, UnauthorizedException {
        Photo photo = photoDao.getPhoto(photoRequest.getId());
        if (photo == null) {
            logger.error("No photo with id: " + photoRequest.getId());
            throw new FileNotFoundException();
        }
        if (userId != photo.getUser().getId()) {
            logger.error("User: " + userId + " cannot download the photo");
            throw new UnauthorizedException("download.error.accessDenied");
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
        if (folder == null) {
            logger.error("Folder with id: " + folderId + " do not exist");
            throw new NotFoundException("folder.error.notExist");
        }
        if (folder.getUser().getId() != user.getId()) {
            logger.error(user.getFirstname() + " " + user.getLastname() + " cannot access to the folder");
            throw new UnauthorizedException("folder.error.accessDenied");
        }
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
    public PhotoListResponse getPhotosByIds(List<Long> ids, User user) throws NotFoundException {
        if (ids.size() == 0) {
            logger.error("ids empty");
            throw new NotFoundException("photo.maySelected");
        }
        List<Photo> urlList = photoDao.getPhotosByUserAndIds(ids, user);
        PhotoListResponse response = new PhotoListResponse();
        urlList.forEach(photo -> response.addPhoto(new PhotoResponse()
                .setId(photo.getId())
                .setTitle(photo.getTitle())
                .setUrl(s3Dao.getPhotoUrl(photo))
        ));
        return response;
    }

    @Transactional
    public MessageListResponse movePhotosToFolder(User user, PhotosRequest request) throws NotFoundException, UnauthorizedException {
        Folder folder = folderDao.getFolderById(request.getFolderId());
        if (folder == null) {
            logger.error("Folder with id: " + request.getFolderId() + " do not exist");
            throw new NotFoundException("folder.error.notExist");
        }
        if (folder.getUser().getId() != user.getId()) {
            logger.error(user.getFirstname() + " " + user.getLastname() + " cannot access to the folder");
            throw new UnauthorizedException("folder.error.accessDenied");
        }
        MessageListResponse response = new MessageListResponse();
        for (long id : request.getIds()) {
            Photo photo = photoDao.getPhoto(id);
            if (photo == null) {
                response.addMessage("error: folder.error.oneOf.photoNotExist");
            } else if (photo.getUser().getId() != user.getId()) {
                response.addMessage("error: folder.error.oneOf.notBelongUser");
            } else if (photo.getFolder() != null && photo.getFolder().getId() == folder.getId()) {
                response.addMessage("warning: folder.error.oneOf.alreadyExist");
            } else if (photoDao.existNameInFolder(folder, photo.getTitle(), photo.getFormat())) {
                response.addMessage("error: folder.error.oneOf.existingName");
            } else {
                photo.setFolder(folder).setModificationDate(LocalDate.now());
            }
        }
        response.addMessage("success: photo.successMove");
        return response;
    }

    private FilterQuery createFilterQuery(FilterDto[] filters) throws InvalidObjectException {
        if (filters == null)
            return new FilterQuery("SELECT p FROM Photo p WHERE p.user = :user", new LinkedList<>());
        Queue<String> argQueue = new LinkedList<>();
        StringBuilder query = new StringBuilder("SELECT p FROM Photo p JOIN p.tags t WHERE p.user = :user");
        List<FilterDto> filterDtoList = Arrays.asList(filters);
        filterDtoList.sort(FilterDto::compareTo);
        String lastField = "first";
        for (FilterDto filter : filterDtoList) {
            if (!Objects.equals(filter.getField(), lastField)) {
                if (!lastField.equals("first")) {
                    query.append(") AND");
                }
                if (lastField.equals("first")) {
                    query.append(" AND");
                }
                query.append(" (");
                lastField = filter.getField();
            } else {
                query.append(" OR ");
            }
            query.append(filter.getFieldToSql(argQueue)).append(" ");
            query.append(filter.getOperatorToSql()).append(" ");
            query.append(filter.getValueToSql(argQueue)).append(" ");
        }
        if (!filterDtoList.isEmpty()) {
            query.append(")");
        }
        query.append(" GROUP BY p.id");
        return new FilterQuery(query.toString(), argQueue);
    }

    private String createSortQuery(SortDto sortModel) {
        if (sortModel == null) {
            return " ORDER BY p.id DESC";
        }
        StringBuilder query = new StringBuilder(" ORDER BY ");
        switch (sortModel.getField()) {
            case "title":
                query.append("p.title");
                break;
            case "description":
                query.append("p.description");
                break;
            case "creationDate":
                query.append("p.creationDate");
                break;
            case "shootingDate":
                query.append("p.shootingDate");
                break;
            case "size":
                query.append("p.size");
                break;
            default:
                query.append("p.id");
        }
        if (sortModel.getSort().equals("asc")) {
            query.append(" ASC");
        } else {
            query.append(" DESC");
        }
        return query.toString();
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
                        .setLocation(new Location().setAddress(photo.getAddress()).setPosition(new Position().setLat(photo.getLat()).setLng(photo.getLng())))
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
                logger.error("Photo with id: " + id + " do not exist");
                throw new FileNotFoundException();
            }
            if (user.getId() != photo.getUser().getId()) {
                logger.error(user.getFirstname() + " " + user.getLastname() + " cannot download the photo");
                throw new UnauthorizedException("download.accessDenied");
            }
            byte[] photoByte = s3Dao.download(photo);
            String name = photo.getTitle() + photo.getFormat();
            for (int i = 1; !names.add(name); i++)
                name = photo.getTitle() + "_" + i + photo.getFormat();
            ZipEntry zipEntry = new ZipEntry(name);
            zipEntry.setSize(photoByte.length);
            zipOut.putNextEntry(zipEntry);
            zipOut.write(photoByte);
            long zipMaxSize = settingDao.getSetting().getDownloadSize();
            if (bos.size() > zipMaxSize * 1024 * 1024) {
                logger.error("Attachment too big");
                throw new MaxSizeExceededException("download.error.oversize");
            }
        }
        zipOut.closeEntry();
        zipOut.close();
        return new PhotoResponse().setData(bos.toByteArray()).setTitle("APPH-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date())).setFormat(".zip");
    }

    @Transactional
    public String changePhotoFile(Long userId, PhotoRequest photoRequest) throws IOException, UnauthorizedException, InvalidFileException {
        Photo photo = photoDao.getPhoto(photoRequest.getId());
        if (photo == null) {
            logger.error("Photo with id: " + photoRequest.getId() + " do not exist");
            throw new FileNotFoundException("download.error.fileNotExist");
        }
        if (userId != photo.getUser().getId()) {
            logger.error(userId + " cannot modify the file");
            throw new UnauthorizedException("download.error.accessDenied");
        }
        s3Dao.delete(photo);
        photo.setFormat(getFormat(photoRequest.getFile())).setSize((photoRequest.getFile().getSize() + .0F) / 1024).setModificationDate(LocalDate.now());
        return s3Dao.upload(photoRequest.getFile(), photo);
    }

    @Transactional
    public MessageResponse updatePhotoFolder(User user, PhotoRequest photoRequest) throws UnauthorizedException, NotFoundException {
        Photo photo = photoDao.getPhoto(photoRequest.getId());
        if (photo == null) {
            logger.error("Photo with id: " + photoRequest.getId() + " do not exist");
            throw new NotFoundException("photo.error.notExist");
        }
        if (photo.getUser().getId() != user.getId()) {
            logger.error(user.getFirstname() + " " + user.getLastname() + " cannot access to the photo");
            throw new UnauthorizedException("action.forbidden");
        }
        Folder folder = folderDao.getFolderById(photoRequest.getFolderId());
        photo.setFolder(folder);
        return new MessageResponse("photo.successDelete");
    }

    @Transactional
    public void updatePhotoList(User user, PhotosRequest photosRequest) {
        if (photosRequest.getShootingDate() != null && photosRequest.getTags() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/d/yyyy");
            LocalDate shootingDate = LocalDate.parse(photosRequest.getShootingDate(), formatter);
            Set<Tag> newTags = photosRequest.getTags() != null ? tagService.createListTags(photosRequest.getTags(), user) : null;
            for (long id : photosRequest.getIds()) {
                Photo photo = photoDao.getPhoto(id);
                photo.setShootingDate(shootingDate).setTags(newTags);
            }
        } else if (photosRequest.getShootingDate() == null && photosRequest.getTags() != null) {
            Set<Tag> newTags = photosRequest.getTags() != null ? tagService.createListTags(photosRequest.getTags(), user) : null;
            for (long id : photosRequest.getIds()) {
                Photo photo = photoDao.getPhoto(id);
                photo.setTags(newTags);
            }
        } else if (photosRequest.getShootingDate() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/d/yyyy");
            LocalDate shootingDate = LocalDate.parse(photosRequest.getShootingDate(), formatter);
            for (long id : photosRequest.getIds()) {
                Photo photo = photoDao.getPhoto(id);
                photo.setShootingDate(shootingDate);
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Transactional
    public PhotoListResponse search(FilterRequest filterRequest, User user) {
        SearchResult<Photo> res = photoDao.searchPhotoByTargetAndUser(filterRequest, user);
        long totalHits = res.total().hitCount();
        List<Photo> userPhotos = res.hits();
        PhotoListResponse response = new PhotoListResponse();
        userPhotos.forEach(photo ->
                response.addPhoto(new PhotoResponse(photo)
                        .setUrl(s3Dao.getPhotoUrl(photo))
                )
        );
        response.setTotal(totalHits);
        return response;
    }


    @Transactional
    public String processPhoto(User user, PhotoRequest photoRequest) throws NotFoundException, IOException {
        Photo photo = photoDao.getPhoto(photoRequest.getId());
        if (photo == null)
            throw new NotFoundException("photo.error.notFound");
        Photo ProcessedPhoto = PhotoDao.processPhoto(photo)
                .setTitle(photo.getTitle())
                .setFormat(photo.getFormat())
                .setUser(photo.getUser())
                //TODO  .setSize((photoRequest.getFile().getSize() + .0F) / 1024)
                .setDescription(photo.getDescription())
                .setCreationDate(photo.getCreationDate())
                .setModificationDate(LocalDate.now())
                .setShootingDate(photo.getShootingDate())
                .setTags(photo.getTags())
                .setFolder(photo.getFolder())
                .setAddress(photo.getAddress())
                .setLat(photo.getLat())
                .setLng(photo.getLng());
        ProcessedPhoto = photoDao.addPhoto(ProcessedPhoto);
        return s3Dao.upload(photoRequest.getFile(), ProcessedPhoto);
    }

    private class FilterQuery {
        public String query;
        public Queue<String> argQueue;

        public FilterQuery(String query, Queue<String> argQueue) {
            this.query = query;
            this.argQueue = argQueue;
        }
    }
}
