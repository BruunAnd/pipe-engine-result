package dk.anderslangballe.trees.transformer;

import dk.anderslangballe.trees.NodeType;
import dk.anderslangballe.trees.SimpleBranch;
import dk.anderslangballe.trees.SimpleLeaf;
import dk.anderslangballe.trees.SimpleTree;

import java.util.*;

public class UnionTransformer {
    // TODO: Rewrite unions
    // Start at bottom and see if union between two things are literals, then make them into one literal

    public SimpleTree transform(SimpleTree tree) {
        // Apply to children

        if (tree instanceof SimpleBranch) {
            if (isUnionTree(tree)) {

            }
        }
        if (isUnionTree(tree)) {

        } else {

        }
        if (tree instanceof SimpleBranch) {
            SimpleBranch branch = (SimpleBranch) tree;

            if (branch.value == NodeType.UNION && branch.children.length >= 2 && branch.sameChildren()) {
                // All children are the same, union into one child
                Set<String> sourceSet = new HashSet<>();
                Arrays.stream(branch.children).forEach(e -> sourceSet.addAll(e.sources));
                System.err.println(sourceSet);

                // Create new leaf with the triple
                Optional<SimpleTree> any = Arrays.stream(branch.children).findAny();
                if (any.isPresent() && any.get() instanceof SimpleLeaf) {
                    return new SimpleLeaf(((SimpleLeaf) any.get()).literal).applySources(new ArrayList<>(sourceSet));
                }
            } else {
                // Create a new branch with possibly transformed children
                SimpleTree[] newChildren = Arrays.stream(branch.children).map(this::transform).toArray(SimpleTree[]::new);

                return new SimpleBranch(branch.value, newChildren);
            }
        }

        return tree;
    }

    private SimpleTree mergeChildren(SimpleTree tree) {
        if (!(tree instanceof SimpleBranch) || ((SimpleBranch) tree).children.length != 2) {
            return tree;
        }

    }

    private boolean isUnionTree(SimpleTree tree) {
        if (!(tree instanceof SimpleBranch) || ((SimpleBranch) tree).children.length != 2) {
            return false;
        }

        SimpleBranch branch = (SimpleBranch) tree;
        long unions = Arrays.stream(branch.children).filter(t -> t instanceof SimpleBranch && ((SimpleBranch) t).value == NodeType.UNION);
        long leaves = Arrays.stream(branch.children).filter(t -> t instanceof SimpleLeaf).count();

        return (unions == 1 && leaves == 1) || leaves == 2;
    }

    private boolean isSamePattern(SimpleLeaf first, SimpleTree second) {
        // When merging
        // Start by seeing if a child can merge

    }

}


// Given a union, if all children are unions or triples. merge into one