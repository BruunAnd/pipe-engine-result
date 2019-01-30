package dk.anderslangballe.trees;

public class OperatorLeaf<E> extends SimpleLeaf<E> {
    public SimpleTree<E> child;

    public OperatorLeaf(NodeType type, SimpleTree<E> child) {
        super(type);
        this.child = child;
    }
}
