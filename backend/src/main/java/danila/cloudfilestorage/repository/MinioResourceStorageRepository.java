package danila.cloudfilestorage.repository;

import danila.cloudfilestorage.api.repository.ResourceStorageRepository;
import danila.cloudfilestorage.exception.FileStorageAccessException;
import danila.cloudfilestorage.model.StorageResource;
import danila.cloudfilestorage.util.PathUtil;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Repository
@RequiredArgsConstructor
public class MinioResourceStorageRepository implements ResourceStorageRepository {
    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    public void uploadResource(String fullResourcePath, String contentType,
                               InputStream inputStream, long size) {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fullResourcePath)
                    .contentType(contentType)
                    .stream(inputStream, size, -1)
                    .build());
        } catch (Exception e) {
            throw new FileStorageAccessException(e);
        }
    }

    public void createDirectory(String fullResourcePath) {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fullResourcePath)
                    .stream(new ByteArrayInputStream(new byte[0]), 0, -1)
                    .build());
        } catch (Exception e) {
            throw new FileStorageAccessException(e);
        }
    }

    public boolean isExist(String fullResourcePath) {
        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fullResourcePath)
                    .build()
            );
            return true;
        } catch (ErrorResponseException e) {
            if (e.errorResponse().code().equals("NoSuchKey")) {
                return false;
            }
            throw new FileStorageAccessException(e);
        } catch (Exception e) {
            throw new FileStorageAccessException(e);
        }

    }

    public List<StorageResource> getContentDirectory(String fullResourcePath) {
        try {
            Iterable<Result<Item>> findResults = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucketName)
                            .prefix(fullResourcePath)
                            .recursive(false)
                            .build());
            List<StorageResource> list = new ArrayList<>();
            for (Result<Item> result : findResults) {
                Item item = result.get();
                list.add(new StorageResource(item.objectName(), item.size()));
            }
            return list;
        } catch (Exception e) {
            throw new FileStorageAccessException(e);
        }
    }

    public StorageResource getResourceInfo(String fullResourcePath) {
        try {
            StatObjectResponse object = minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fullResourcePath)
                            .build());
            return new StorageResource(object.object(), object.size());
        } catch (Exception e) {
            throw new FileStorageAccessException(e);
        }
    }

    public void deleteResource(String fullResourcePath) {
        try {
            Iterable<Result<Item>> findResults = findObjects(fullResourcePath);
            List<DeleteObject> deleteObjectList = new ArrayList<>();
            for (Result<Item> result : findResults) {
                deleteObjectList.add(new DeleteObject(result.get().objectName()));
            }
            Iterable<Result<DeleteError>> deleteResults = minioClient.removeObjects(RemoveObjectsArgs.builder()
                    .bucket(bucketName)
                    .objects(deleteObjectList)
                    .build());
            for (Result<DeleteError> result : deleteResults) {
                result.get();
            }
        } catch (Exception e) {
            throw new FileStorageAccessException(e);
        }
    }

    public List<String> getResourceNameList(String fullResourcePath) {
        try {
            Iterable<Result<Item>> findResults = findObjects(fullResourcePath);
            List<String> resourceNameList = new ArrayList<>();
            for (Result<Item> result : findResults) {
                resourceNameList.add(result.get().objectName());
            }
            return resourceNameList;
        } catch (Exception e) {
            throw new FileStorageAccessException(e);
        }
    }

    public InputStream getResourceInputStream(String fullResourcePath) {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fullResourcePath)
                    .build());
        } catch (Exception e) {
            throw new FileStorageAccessException(e);
        }
    }

    public void renameResource(String oldFullResourcePath, String newfullResourcePath) {
        try {
            minioClient.copyObject(CopyObjectArgs.builder()
                    .bucket(bucketName)
                    .object(newfullResourcePath)
                    .source(CopySource.builder()
                            .bucket(bucketName)
                            .object(oldFullResourcePath)
                            .build())
                    .build());
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(oldFullResourcePath)
                    .build());
        } catch (Exception e) {
            throw new FileStorageAccessException(e);
        }
    }

    public List<StorageResource> search(String userRootPath, String query) {
        try {
            List<StorageResource> storageResourceList = new ArrayList<>();
            Iterable<Result<Item>> findResults = findObjects(userRootPath);
            for (Result<Item> result : findResults) {
                Item resource = result.get();
                String resourceName = PathUtil.getResourceNameWithoutSlash(resource.objectName());
                if (resourceName.toLowerCase(Locale.ROOT).contains(query)) {
                    storageResourceList.add(new StorageResource(resource.objectName(), resource.size()));
                }
            }
            return storageResourceList;
        } catch (Exception e) {
            throw new FileStorageAccessException(e);
        }
    }

    private Iterable<Result<Item>> findObjects(String fullResourcePath) {
        return minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucketName)
                        .prefix(fullResourcePath)
                        .recursive(true)
                        .build());
    }
}
