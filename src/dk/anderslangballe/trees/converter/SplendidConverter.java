package dk.anderslangballe.trees.converter;

import de.uni_koblenz.west.splendid.index.Graph;
import de.uni_koblenz.west.splendid.model.RemoteQuery;
import dk.anderslangballe.trees.SimpleTree;
import org.openrdf.query.algebra.TupleExpr;

import java.util.Set;
import java.util.stream.Collectors;

public class SplendidConverter extends OpenRdfConverter {
    @Override
    public SimpleTree fromExpr(TupleExpr expr) {
        if (expr instanceof RemoteQuery) {
            RemoteQuery remoteQuery = (RemoteQuery) expr;
            return applySources(fromExpr(remoteQuery.getArg()), remoteQuery.getSources());
        }

        System.err.println(String.format("No SPLENDID specific handler for class %s", expr.getClass().getName()));

        return super.fromExpr(expr);
    }

    private SimpleTree applySources(SimpleTree tree, Set<Graph> sources) {
        return tree.applySources(sources.stream().map(Graph::getNamespaceURL).collect(Collectors.toList()));
    }
}
