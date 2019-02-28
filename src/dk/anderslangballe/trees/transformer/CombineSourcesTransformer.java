package dk.anderslangballe.trees.transformer;

import dk.anderslangballe.trees.NodeType;
import dk.anderslangballe.trees.SimpleBranch;
import dk.anderslangballe.trees.SimpleLeaf;
import dk.anderslangballe.trees.SimpleTree;

import java.util.*;

public class CombineSourcesTransformer implements Transformer {
    public SimpleTree transform(SimpleTree tree) {
        if (tree instanceof SimpleBranch) {
            SimpleBranch branch = (SimpleBranch) tree;

            if (branch.value == NodeType.UNION && branch.children.length >= 2 && branch.sameChildren()) {
                // All children are the same, union into one child
                Set<String> sourceSet = new HashSet<>();
                Arrays.stream(branch.children).forEach(e -> sourceSet.addAll(e.sources));

                // Create new leaf with the triple
                Optional<SimpleTree> any = Arrays.stream(branch.children).findAny();
                if (any.isPresent() && any.get() instanceof SimpleLeaf) {
                    return new SimpleLeaf(((SimpleLeaf) any.get()).literal).applySources(new ArrayList<>(sourceSet));
                }
            } else {
                // Create a new branch with possibly transformed children
                SimpleTree[] newChildren = Arrays.stream(branch.children).map(this::transform).toArray(SimpleTree[]::new);

                return branch.setChildren(newChildren);
            }
        }

        return tree;
    }
}


// Given a union, if all children are unions or triples. merge into one