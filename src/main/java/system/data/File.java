package system.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class File extends INode {

    public static int IS_FILE = 37;

    final long timestamp;
    long size;
    byte[] data; //abstract


    public File(String path, String name) {
        super(path, name, IS_FILE);
        this.timestamp = System.currentTimeMillis();
    }

    @Override
    public File getSibling() {
        return (File) sibling;
    }

    InputStream newInputStream() throws IOException {
        return null;
    }

    OutputStream newOutputStream() throws IOException {
        return null;
    }


    public void free() {
    }
}
