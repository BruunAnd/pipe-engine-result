package dk.anderslangballe.trees;

public class SimpleBranch<E> extends SimpleTree<E> {
    public SimpleTree<E> left;
    public SimpleTree<E> right;
    public NodeType type;

    public SimpleBranch(NodeType type, SimpleTree<E> left, SimpleTree<E> right) {
        this.left = left;
        this.right = right;
        this.type = type;
    }
}
