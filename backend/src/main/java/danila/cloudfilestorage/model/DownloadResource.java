package danila.cloudfilestorage.model;

import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

public record DownloadResource(String resourceName, StreamingResponseBody body) {
}
