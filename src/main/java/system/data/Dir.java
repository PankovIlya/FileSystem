package system.data;

public class Dir extends INode {

    Dir child;
    File file;


    public Dir(String path, String name) {
        super(path, name);
        pos = 0;
    }

    public Dir(Node node) {
        super(node);
        pos = 0;
    }

    @Override
    public Dir getSibling() {
        return (Dir) sibling;
    }

    public Dir getChild() {
        return child;
    }

    public void setChild(Dir child) {
        this.child = child;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
