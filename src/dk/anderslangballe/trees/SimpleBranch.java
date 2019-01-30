package dk.anderslangballe.trees;

public class SimpleBranch<E> extends SimpleTree<E> {
    public SimpleTree<E> left;
    public SimpleTree<E> right;
    public NodeType operator;

    public SimpleBranch(NodeType operator, SimpleTree<E> left, SimpleTree<E> right) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }
}
