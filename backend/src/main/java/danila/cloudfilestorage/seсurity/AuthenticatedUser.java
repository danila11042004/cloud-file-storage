package danila.cloudfilestorage.seсurity;

import java.io.Serializable;

public record AuthenticatedUser(String username, Long id) implements Serializable {
}
