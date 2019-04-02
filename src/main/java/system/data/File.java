package system.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class File extends INode {

    final long timestamp;
    long size;
    byte[] data; //abstract


    public File(String path, String name) {
        super(path, name, 1);
        this.timestamp = System.currentTimeMillis();
    }

    public File(Node node) {
        super(node);
        this.timestamp = System.currentTimeMillis();
        pos = 1;
    }

    @Override
    public File getSibling() {
        return (File) sibling;
    }

    File(File file) {
        super(file);
        this.timestamp = file.timestamp;
        pos = 1;
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
