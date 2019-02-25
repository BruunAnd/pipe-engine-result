package dk.anderslangballe.trees;

import java.util.List;

public class SimpleBranch extends SimpleTree {
    public SimpleTree[] children;
    public NodeType value;

    public SimpleBranch(NodeType type, SimpleTree ... children) {
        this.value = type;
        this.children = children;
    }

    public SimpleTree applySources(List<String> sources) {
        super.applySources(sources);

        for (SimpleTree child : children) {
            child.applySources(sources);
        }

        return this;
    }

    public boolean sameChildren() {
        if (children.length <= 1) {
            return true;
        }

        for (int i = 1; i < children.length; i++) {
            if (!children[i].equals(children[i - 1])) {
                return false;
            }
        }

        return true;
    }
}