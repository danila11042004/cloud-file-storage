package danila.cloudfilestorage.service.resource;

import danila.cloudfilestorage.api.repository.ResourceStorageRepository;
import danila.cloudfilestorage.dto.ResourceResponseDto;
import danila.cloudfilestorage.enums.ResourceType;
import danila.cloudfilestorage.exception.InvalidRequestBodyException;
import danila.cloudfilestorage.exception.ResourceAlreadyExistException;
import danila.cloudfilestorage.exception.ResourceNotFoundException;
import danila.cloudfilestorage.exception.StreamProcessingException;
import danila.cloudfilestorage.model.ResourceAndPath;
import danila.cloudfilestorage.util.PathUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ResourceUploadService {
    private static final String RESOURCE_ALREADY_EXIST = "Ресурс с таким именем уже существует в родительской директории";
    private static final String DIRECTORY_NOT_FOUND = "Выбранной папки не существует";
    private static final String FILENAME_NOT_PASSED = "Имя файла не было передано";
    private static final String UNPACK_DATA_ERROR_MESSAGE = "Ошибка при распаковки переданных данных";
    private final ResourceStorageRepository resourceStorageRepository;

    public List<ResourceResponseDto> upload(String userRootPath, List<MultipartFile> fileList, String resourceParentPath) {
        String fullResourceParentPath = userRootPath + resourceParentPath;
        if (!resourceStorageRepository.isExist(fullResourceParentPath)) {
            throw new ResourceNotFoundException(DIRECTORY_NOT_FOUND);
        }
        List<ResourceResponseDto> responseDtoList = new ArrayList<>();
        for (MultipartFile multipartFile : fileList) {
            String fileName = multipartFile.getOriginalFilename();
            if (fileName == null || fileName.isBlank()) {
                throw new InvalidRequestBodyException(FILENAME_NOT_PASSED);
            }
            ResourceAndPath[] resourceAndPathArray = getResourceAndPathArray(fileName.toLowerCase(Locale.ROOT),
                    fullResourceParentPath);
            try {
                uploadResourcesFromArray(multipartFile, resourceAndPathArray, responseDtoList);
            } catch (ResourceAlreadyExistException e) {
                throw e;
            } catch (Exception e) {
                resourceRollback(resourceAndPathArray);
                throw e;
            }
        }
        return responseDtoList;

    }

    private ResourceAndPath[] getResourceAndPathArray(String fileName, String fullResourceParentPath) {
        String[] resourceNameOrderArray = PathUtil.getSplitWithLeftSlash(fileName);
        ResourceAndPath[] resourceAndPathArray = new ResourceAndPath[resourceNameOrderArray.length];
        StringBuilder path = new StringBuilder(fullResourceParentPath);
        for (int i = 0; i < resourceAndPathArray.length; i++) {
            resourceAndPathArray[i] = new ResourceAndPath(resourceNameOrderArray[i], path.toString());
            path.append(resourceNameOrderArray[i]);
        }
        return resourceAndPathArray;
    }

    private void uploadResourcesFromArray(MultipartFile multipartFile, ResourceAndPath[] resourceAndPathArray,
                                          List<ResourceResponseDto> responseDtoList) {
        long size = multipartFile.getSize();
        for (ResourceAndPath resourceAndPath : resourceAndPathArray) {
            String resourceName = resourceAndPath.resourceName();
            String fullResourceParentPath = resourceAndPath.path();
            ResourceResponseDto responseDto;
            String resourceParentPath = PathUtil.getPathWithRemoteFirstDirectory(fullResourceParentPath);
            if (resourceName.endsWith("/")) {
                if (resourceStorageRepository.isExist(fullResourceParentPath + resourceName)) {
                    throw new ResourceAlreadyExistException(RESOURCE_ALREADY_EXIST);
                }
                resourceStorageRepository.createDirectory(fullResourceParentPath + resourceName);
                String dirName = resourceName.substring(0, resourceName.length() - 1);
                responseDto = new ResourceResponseDto(resourceParentPath, dirName, null, ResourceType.DIRECTORY);
                responseDtoList.add(responseDto);
            } else {
                if (resourceStorageRepository.isExist(fullResourceParentPath + resourceName)) {
                    throw new ResourceAlreadyExistException(RESOURCE_ALREADY_EXIST);
                }
                try {
                    resourceStorageRepository.uploadResource(fullResourceParentPath + resourceName,
                            multipartFile.getContentType(), multipartFile.getInputStream(), size);
                } catch (IOException e) {
                    throw new StreamProcessingException(UNPACK_DATA_ERROR_MESSAGE);
                }
                responseDto = new ResourceResponseDto(resourceParentPath, resourceName, size, ResourceType.FILE);
                responseDtoList.add(responseDto);
            }
        }
    }

    private void resourceRollback(ResourceAndPath[] resourceAndPathArray) {
        String resourceName = resourceAndPathArray[0].resourceName();
        String fullResourceParentPath = resourceAndPathArray[0].path();
        resourceStorageRepository.deleteResource(fullResourceParentPath + resourceName);
    }
}
