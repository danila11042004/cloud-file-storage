package danila.cloudfilestorage.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class RegexPattern {
    public final String REGEX_ALL_VALID_CHARS = "[A-Za-zА-Яа-я0-9_. \\-\\(\\)]";
    public final String REGEX_ALL_VALID_CHARS_EXCEPT_SPACE_AND_DOT = "[A-Za-zА-Яа-я0-9_\\-\\(\\)]";
    public final String REGEX_SEARCH = "("+REGEX_ALL_VALID_CHARS+"*)";
    public final String REGEX_DIRECTORY= "("+REGEX_ALL_VALID_CHARS_EXCEPT_SPACE_AND_DOT +"+"+REGEX_ALL_VALID_CHARS+"*/)";
    public final String REGEX_FILE="("+REGEX_ALL_VALID_CHARS+"*"+"\\."+ REGEX_ALL_VALID_CHARS_EXCEPT_SPACE_AND_DOT +"+"+REGEX_ALL_VALID_CHARS+"*)";
    public final String REGEX_ZERO_OR_MORE_DIRECTORIES = "^"+REGEX_DIRECTORY+"*$";
    public final String REGEX_ONE_OR_MORE_DIRECTORIES = "^"+REGEX_DIRECTORY+"+$";
    public final String REGEX_PATH_TO_RESOURCE = "^"+REGEX_DIRECTORY+"*" +
            "("+REGEX_DIRECTORY+"|"+REGEX_FILE+")$";
    public final String REGEX_SPLIT_KEEP_SLASH_LEFT = "(?<=/)";
    public final String REGEX_FIRST_DIRECTORY_PATH = "^[^/]+/";
    public final String REGEX_LAST_PART_PATH = "[^/]+/?$";
    public final String REGEX_EMPTY = "";
    public final String REGEX_USERNAME = "[A-Za-zА-Яа-я0-9_\\-]*[A-Za-zА-Яа-я][A-Za-zА-Яа-я0-9_\\-]*";
}
