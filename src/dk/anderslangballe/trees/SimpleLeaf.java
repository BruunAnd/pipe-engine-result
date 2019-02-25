package dk.anderslangballe.trees;

public class SimpleLeaf extends SimpleTree  {
    public String literal;

    public SimpleLeaf(String value) {
        this.literal = value;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof SimpleLeaf) {
            return this.literal.equals(((SimpleLeaf) other).literal);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return this.literal.hashCode();
    }
}
