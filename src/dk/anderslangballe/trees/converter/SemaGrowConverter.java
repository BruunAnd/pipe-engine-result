package dk.anderslangballe.trees.converter;

import dk.anderslangballe.trees.SimpleTree;
import eu.semagrow.core.impl.plan.ops.SourceQuery;
import eu.semagrow.core.plan.Plan;
import eu.semagrow.core.source.Site;
import org.openrdf.query.algebra.TupleExpr;

import java.util.List;
import java.util.stream.Collectors;

public class SemaGrowConverter extends OpenRdfConverter {
    @Override
    public SimpleTree fromExpr(TupleExpr expr) {
        if (expr instanceof Plan) {
            // Could get cost of plan here, just ignoring it for now
            return fromExpr(((Plan) expr).getArg());
        }

        if (expr instanceof SourceQuery) {
            return applySources(fromExpr(((SourceQuery) expr).getArg()), ((SourceQuery) expr).getSources());
        }

        // TODO: BindJoin, QueryRoot (maybe not necessary)
        System.err.println(String.format("No SemaGrow specific handler for class %s", expr.getClass().getName()));

        // No SemaGrow specific handler for this node
        return super.fromExpr(expr);
    }

    private SimpleTree applySources(SimpleTree tree, List<Site> sources) {
        return tree.applySources(sources.stream().map(site -> site.getID().toString()).collect(Collectors.toList()));
    }
}
