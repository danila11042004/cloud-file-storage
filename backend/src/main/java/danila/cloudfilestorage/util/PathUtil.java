package danila.cloudfilestorage.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PathUtil {


    public String[] getSplitWithLeftSlash(String path) {
        return path.split(RegexPattern.REGEX_SPLIT_KEEP_SLASH_LEFT);
    }

    public String getPathWithRemoteFirstDirectory(String path) {
        return path.replaceFirst(RegexPattern.REGEX_FIRST_DIRECTORY_PATH, RegexPattern.REGEX_EMPTY);
    }

    public String getPathWithRemoteLastPart(String path) {
        return path.replaceFirst(RegexPattern.REGEX_LAST_PART_PATH, RegexPattern.REGEX_EMPTY);
    }

    public String getResourceNameWithoutSlash(String path) {
        String[] resourceArray = path.split("/");
        return resourceArray[resourceArray.length - 1];
    }

    public String getResourceNameWithSlashIfHas(String path) {
        String[] resourceArray = getSplitWithLeftSlash(path);
        return resourceArray[resourceArray.length - 1];
    }
}
