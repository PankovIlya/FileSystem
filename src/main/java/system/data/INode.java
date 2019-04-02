package system.data;

import java.util.Objects;

public class INode extends Node {

    INode sibling;
    private final int pos;

    public INode(String path, String name, int pos) {
        super(path, name);
        this.pos = pos;
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
        if (o == null) return false;
        if (!super.equals(o)) return false;
        INode iNode = (INode) o;
        return pos == iNode.pos;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), pos);
    }
}
