package danila.cloudfilestorage.service.resource;

import danila.cloudfilestorage.api.repository.ResourceStorageRepository;
import danila.cloudfilestorage.dto.ResourceResponseDto;
import danila.cloudfilestorage.enums.ResourceType;
import danila.cloudfilestorage.exception.ResourceAlreadyExistException;
import danila.cloudfilestorage.exception.ResourceNotFoundException;
import danila.cloudfilestorage.model.StorageResource;
import danila.cloudfilestorage.util.PathUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectoryService {
    private static final String PARENT_DIRECTORY_ERROR_MESSAGE = "Родителская папка не существует";
    private static final String SELECTED_DIRECTORY_ERROR_MESSAGE = "Выбранной папки не существует";
    private static final String DIRECTORY_ALREADY_EXIST = "Папка с таким именем уже существует";
    private final ResourceStorageRepository resourceStorageRepository;

    public ResourceResponseDto createDirectory(String userRootPath, String resourcePath) {
        String fullResourcePath = userRootPath + resourcePath;
        String fullResourceParentPath = PathUtil.getPathWithRemoteLastPart(fullResourcePath);
        if (!resourceStorageRepository.isExist(fullResourceParentPath)) {
            throw new ResourceNotFoundException(PARENT_DIRECTORY_ERROR_MESSAGE);
        }
        if (resourceStorageRepository.isExist(fullResourcePath)) {
            throw new ResourceAlreadyExistException(DIRECTORY_ALREADY_EXIST);
        }
        resourceStorageRepository.createDirectory(fullResourcePath);
        String resourceParentPath = PathUtil.getPathWithRemoteFirstDirectory(fullResourceParentPath);
        String dirName = PathUtil.getResourceNameWithoutSlash(resourcePath);
        return new ResourceResponseDto(resourceParentPath, dirName, null, ResourceType.DIRECTORY);
    }

    public List<ResourceResponseDto> getDirectoryContent(String userRootPath, String resourcePath) {
        String fullResourcePath = userRootPath + resourcePath;
        if (!resourceStorageRepository.isExist(fullResourcePath)) {
            throw new ResourceNotFoundException(SELECTED_DIRECTORY_ERROR_MESSAGE);
        }
        List<StorageResource> storageResourceList = resourceStorageRepository.getContentDirectory(fullResourcePath);
        return getResourceResponseDtoList(storageResourceList, userRootPath, resourcePath);
    }

    private List<ResourceResponseDto> getResourceResponseDtoList(List<StorageResource> storageResourceList,
                                                                 String userRootPath, String resourceParentPath) {
        List<ResourceResponseDto> responseDtoList = new ArrayList<>();
        for (StorageResource storageResource : storageResourceList) {
            String fullResourcePath = storageResource.fullResourcePath();
            String resourceName = PathUtil.getResourceNameWithoutSlash(fullResourcePath);
            if (fullResourcePath.endsWith("/")) {
                if (!fullResourcePath.equals(userRootPath + resourceParentPath)) {
                    responseDtoList.add(new ResourceResponseDto(resourceParentPath, resourceName,
                            null, ResourceType.DIRECTORY));
                }
            } else {
                responseDtoList.add(new ResourceResponseDto(resourceParentPath, resourceName,
                        storageResource.size(), ResourceType.FILE));
            }
        }
        return responseDtoList;
    }

}
