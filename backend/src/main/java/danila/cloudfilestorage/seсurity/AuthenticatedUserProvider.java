package danila.cloudfilestorage.seсurity;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedUserProvider {

    public AuthenticatedUser getAuthenticatedUser() {
        return (AuthenticatedUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public Long getAuthenticatedUserId() {
        return getAuthenticatedUser().id();
    }

    public String getAuthenticatedUserName() {
        return getAuthenticatedUser().username();
    }
}
