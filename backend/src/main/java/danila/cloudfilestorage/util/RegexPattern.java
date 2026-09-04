package danila.cloudfilestorage.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class RegexPattern {
    public final String REGEX_ZERO_OR_MORE_DIRECTORIES = "^([A-Za-zА-Яа-я0-9_-]+/)*$";
    public final String REGEX_ONE_OR_MORE_DIRECTORIES = "^([A-Za-zА-Яа-я0-9_-]+/)+$";
    public final String REGEX_PATH_TO_RESOURCE = "^([A-Za-zА-Яа-я0-9_-]+/)*" +
            "(([A-Za-zА-Яа-я0-9_-]+/)|([A-Za-zА-Яа-я0-9_-]+\\.[A-Za-zА-Яа-я0-9-]+))$";
    public final String REGEX_ALL_VALID_CHARACTERS_EXCEPT_SLASH = "[A-Za-zА-Яа-я0-9_.-]+";
    public final String REGEX_SPLIT_KEEP_SLASH_LEFT = "(?<=/)";
    public final String REGEX_FIRST_DIRECTORY_PATH = "^[^/]+/";
    public final String REGEX_LAST_PART_PATH = "[^/]+/?$";
    public final String REGEX_EMPTY = "";
    public final String REGEX_USERNAME = "[A-Za-zА-Яа-я0-9_-]*[A-Za-zА-Яа-я][A-Za-zА-Яа-я0-9_-]*";
}
