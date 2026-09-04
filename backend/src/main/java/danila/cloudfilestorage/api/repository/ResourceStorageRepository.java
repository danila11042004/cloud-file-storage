package danila.cloudfilestorage.api.repository;

import danila.cloudfilestorage.model.StorageResource;

import java.io.InputStream;
import java.util.List;

public interface ResourceStorageRepository {
    void uploadResource(String fullResourcePath, String contentType, InputStream inputStream, long size);

    boolean isExist(String fullResourcePath);

    void createDirectory(String fullResourcePath);

    List<StorageResource> getContentDirectory(String fullResourcePath);

    StorageResource getResourceInfo(String fullResourcePath);

    void deleteResource(String fullResourcePath);

    List<String> getResourceNameList(String fullResourcePath);

    InputStream getResourceInputStream(String fullResourcePath);

    void renameResource(String oldFullResourcePath, String newFullResourcePath);

    List<StorageResource> search(String userRootPath, String query);
}
