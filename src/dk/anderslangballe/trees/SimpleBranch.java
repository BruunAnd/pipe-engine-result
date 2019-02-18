package dk.anderslangballe.trees;

import java.util.List;

public class SimpleBranch extends SimpleTree {
    public SimpleTree[] children;
    public String value;

    public SimpleBranch(NodeType type, SimpleTree ... children) {
        switch (type) {
            case JOIN:
                this.value = "\u22C8";
                break;
            case UNION:
                this.value = "\u222A";
                break;
            case PROJECTION:
                this.value = "\u03C0";
                break;
        }
        
        this.children = children;
    }

    public SimpleTree applySources(List<String> sources) {
        super.applySources(sources);

        for (SimpleTree child : children) {
            child.applySources(sources);
        }

        return this;
    }
}
