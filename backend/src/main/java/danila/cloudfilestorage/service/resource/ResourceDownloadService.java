package danila.cloudfilestorage.service.resource;

import danila.cloudfilestorage.api.repository.ResourceStorageRepository;
import danila.cloudfilestorage.exception.ResourceNotFoundException;
import danila.cloudfilestorage.exception.StreamProcessingException;
import danila.cloudfilestorage.model.DownloadResource;
import danila.cloudfilestorage.util.PathUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class ResourceDownloadService {
    private static final String RESOURCE_NOT_FOUND = "Данный ресурс не найден";
    private static final String ZIP_ARCHIVE_ERROR_MESSAGE = "Ошибка при формировании ZIP-архива";
    private final ResourceStorageRepository resourceStorageRepository;

    public DownloadResource download(String userRootPath, String resourcePath) {
        String fullResourcePath = userRootPath + resourcePath;
        if (!resourceStorageRepository.isExist(fullResourcePath)) {
            throw new ResourceNotFoundException(RESOURCE_NOT_FOUND);
        }
        if (fullResourcePath.endsWith("/")) {
            String dirNameZip = PathUtil.getResourceNameWithoutSlash(fullResourcePath) + ".zip";
            return new DownloadResource(dirNameZip, outputStream ->
                    downloadZipResource(fullResourcePath, outputStream));
        } else {
            InputStream inputStream = resourceStorageRepository.getResourceInputStream(fullResourcePath);
            String fileName = PathUtil.getResourceNameWithoutSlash(resourcePath);
            return new DownloadResource(fileName, inputStream::transferTo);
        }
    }

    private void downloadZipResource(String fullResourceParentPath, OutputStream outputStream) {
        try {
            ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
            String zipRootPath = PathUtil.getResourceNameWithSlashIfHas(fullResourceParentPath);
            List<String> fullResourcePathList = resourceStorageRepository.getResourceNameList(fullResourceParentPath);
            for (String fullResourcePath : fullResourcePathList) {
                String zipResourcePath = zipRootPath +
                        fullResourcePath.substring(fullResourceParentPath.length());
                try (InputStream inputStream = resourceStorageRepository
                        .getResourceInputStream(fullResourcePath)) {
                    zipOutputStream.putNextEntry(new ZipEntry(zipResourcePath));
                    inputStream.transferTo(zipOutputStream);
                    zipOutputStream.closeEntry();
                }
            }
            zipOutputStream.finish();
        } catch (IOException e) {
            throw new StreamProcessingException(ZIP_ARCHIVE_ERROR_MESSAGE);
        }
    }
}
