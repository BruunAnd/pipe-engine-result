package dk.anderslangballe.trees;

public class HoverValueBranch extends SimpleBranch {
    public String[] hoverValue;

    public HoverValueBranch(NodeType type, String[] hoverValue, SimpleTree ... children) {
        super(type, children);
        this.hoverValue = hoverValue;
    }
}
