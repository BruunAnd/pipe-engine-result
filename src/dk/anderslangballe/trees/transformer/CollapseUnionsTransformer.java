package dk.anderslangballe.trees.transformer;

import dk.anderslangballe.trees.NodeType;
import dk.anderslangballe.trees.SimpleBranch;
import dk.anderslangballe.trees.SimpleLeaf;
import dk.anderslangballe.trees.SimpleTree;

import java.util.*;

public class CollapseUnionsTransformer implements Transformer {
    @Override
    public SimpleTree transform(SimpleTree tree) {
        if (tree instanceof SimpleBranch) {
            SimpleBranch branch = (SimpleBranch) tree;
            SimpleTree[] children = Arrays.stream(branch.children).map(this::transform).toArray(SimpleTree[]::new);

            if (branch.value == NodeType.UNION) {
                List<SimpleTree> newChildren = new ArrayList<>();
                for (SimpleTree child : Arrays.stream(branch.children).map(this::transform).toArray(SimpleTree[]::new)) {
                    if (child instanceof SimpleBranch && ((SimpleBranch) child).value == NodeType.UNION) {
                        newChildren.addAll(Arrays.asList(((SimpleBranch) child).children));
                    } else {
                        newChildren.add(child);
                    }
                }

                return new SimpleBranch(NodeType.UNION, newChildren.toArray(new SimpleTree[0]));
            }

            return new SimpleBranch(branch.value, children);
        }

        return tree;
    }
}
