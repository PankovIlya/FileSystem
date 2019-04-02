package system.data;

import java.util.Objects;

import static system.MemoryFileSystem.SPACE;

public class Node {

    final String path; //parent
    final String name;

    public Node(String path, String name) {
        this.path = path;
        this.name = name;
    }

    public Node(Node node) {
        this(node.path, node.name);
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        if (!path.equals(SPACE))
            return path + SPACE + name;
        return path + name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Node node = (Node) o;
        return path.equals(node.path) &&
                name.equals(node.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, name);
    }

}
