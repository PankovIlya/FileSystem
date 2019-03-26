package system.data;

public class Status<T> {

    public final int code;
    public final String message;
    private T data;

    public Status(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public Status(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Status(ErrorCode error) {
        this(error.getCode(), error.getMessage());
    }

    public Status(Status status) {
        this(status.code, status.message);
    }

    public Status(ErrorCode error, String message) {
        this(error.getCode(), String.format(error.getMessage(), message));
    }

    public Status(ErrorCode error, T data) {
        this(error.getCode(), error.getMessage(), data);
    }

    public Status(ErrorCode error, String message, T data) {
        this(error.getCode(), String.format(error.getMessage(), message), data);
    }

    public T getData() {
        return data;
    }
}
