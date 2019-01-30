package dk.anderslangballe.trees;

public class LiteralLeaf<E> extends SimpleLeaf<E> {
    public String value;

    public LiteralLeaf(String literal) {
        super(NodeType.LITERAL);
        this.value = literal;
    }
}
