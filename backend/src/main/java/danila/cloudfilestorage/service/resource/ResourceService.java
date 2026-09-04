package danila.cloudfilestorage.service.resource;

import danila.cloudfilestorage.api.repository.ResourceStorageRepository;
import danila.cloudfilestorage.dto.ResourceResponseDto;
import danila.cloudfilestorage.enums.ResourceType;
import danila.cloudfilestorage.exception.ResourceNotFoundException;
import danila.cloudfilestorage.model.StorageResource;
import danila.cloudfilestorage.util.PathUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ResourceService {
    public static final String RESOURCE_NOT_FOUND = "Данный ресурс не найден";
    private final ResourceStorageRepository resourceStorageRepository;

    public ResourceResponseDto getInfo(String userRootPath, String resourcePath) {
        String fullResourcePath = userRootPath + resourcePath;
        if (!resourceStorageRepository.isExist(fullResourcePath)) {
            throw new ResourceNotFoundException(RESOURCE_NOT_FOUND);
        }
        StorageResource storageResource = resourceStorageRepository.getResourceInfo(fullResourcePath);
        String fullResourceParentPath = PathUtil.getPathWithRemoteLastPart(resourcePath);
        String resourceName = PathUtil.getResourceNameWithoutSlash(resourcePath);
        if (fullResourcePath.endsWith("/")) {
            return new ResourceResponseDto(fullResourceParentPath, resourceName, null, ResourceType.DIRECTORY);
        } else {
            return new ResourceResponseDto(fullResourceParentPath, resourceName, storageResource.size(), ResourceType.FILE);
        }
    }

    public void delete(String userRootPath, String resourcePath) {
        String fullResourcePath = userRootPath + resourcePath;
        if (!resourceStorageRepository.isExist(fullResourcePath)) {
            throw new ResourceNotFoundException(RESOURCE_NOT_FOUND);
        }
        resourceStorageRepository.deleteResource(fullResourcePath);
    }

    public List<ResourceResponseDto> search(String userRootPath, String query) {
        List<StorageResource> storageResourceList = resourceStorageRepository.search(userRootPath, query.toLowerCase(Locale.ROOT));
        List<ResourceResponseDto> responseDtoList = new ArrayList<>();
        for (StorageResource storageResource : storageResourceList) {
            String fullResourcePath = storageResource.fullResourcePath();
            String ResourcePath = PathUtil.getPathWithRemoteFirstDirectory(fullResourcePath);
            String resourceParentPath = PathUtil.getPathWithRemoteLastPart(ResourcePath);
            String resourceName = PathUtil.getResourceNameWithoutSlash(ResourcePath);
            if (fullResourcePath.endsWith("/")) {
                if (!fullResourcePath.equals(userRootPath)) {
                    responseDtoList.add(new ResourceResponseDto(resourceParentPath, resourceName, null,
                            ResourceType.DIRECTORY));
                }
            } else {
                responseDtoList.add(new ResourceResponseDto(resourceParentPath, resourceName, storageResource.size(),
                        ResourceType.FILE));
            }
        }
        return responseDtoList;
    }
}
