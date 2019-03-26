package system.data;

import java.util.Objects;

public class INode extends Node {

    INode sibling;
    int pos;

    public INode(String path, String name) {
        super(path, name);
    }

    public INode(Node node) {
        super(node);
    }

    public INode getSibling() {
        return sibling;
    }

    public void setSibling(INode sibling) {
        this.sibling = sibling;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        INode iNode = (INode) o;
        return pos == iNode.pos;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), pos);
    }
}