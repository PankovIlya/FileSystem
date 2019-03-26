package system.data;

public enum ErrorCode {

    OK(0, ""),
    FILE_ALREADY_EXIST(10, "%s already exists"),
    FILE_NOT_FOUND(30, "file %s not found"),
    DIR_NOT_FOUND(40, "path %s not found"),
    NOT_VALID_NAME(60, "not valid name %s"),

    IS_ROOT(100, "can't delete"),
    UNKNOWN_ERROR(1000, "unknown error %s");

    private int code;
    private String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
