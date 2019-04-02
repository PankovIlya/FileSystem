package system.data;

public class Dir extends INode {

    public static int IS_DIR = 73;

    Dir child;
    File file;


    public Dir(String path, String name) {
        super(path, name, IS_DIR);
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
