package dk.anderslangballe.trees;

public class SimpleLeaf<E> extends SimpleTree<E> {
    public final NodeType type;

    public SimpleLeaf(NodeType type) {
        this.type = type;
    }
}
