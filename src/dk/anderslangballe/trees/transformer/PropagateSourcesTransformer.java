package dk.anderslangballe.trees.transformer;

import dk.anderslangballe.trees.SimpleBranch;
import dk.anderslangballe.trees.SimpleTree;

import java.util.*;
import java.util.stream.Collectors;

public class PropagateSourcesTransformer implements Transformer {
    // Propagate sources up in a tree
    // I.e. for a join between two leaf nodes with the same source, the parent should have the same source
    @Override
    public SimpleTree transform(SimpleTree tree) {
        if (tree instanceof SimpleBranch) {
            SimpleBranch branch = (SimpleBranch) tree;

            // Map children to sources
            // I removed transform in map as it is incorrect
            // At the moment, if all children of the root have the same source, we apply that source to the root
            List<List<String>> sources = Arrays.stream(branch.children)
                                               .map(s -> s.sources) // .map(s -> transform(s).sources)
                                               .collect(Collectors.toList());

            // If the number of distinct sources is the same, then use that source
            if (sources.stream().distinct().count() == 1) {
                tree.sources = sources.get(0);
            }
        }

        return tree;
    }
}
