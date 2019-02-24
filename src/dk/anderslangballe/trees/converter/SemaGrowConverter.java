package dk.anderslangballe.trees.converter;

import dk.anderslangballe.trees.SimpleTree;
import org.openrdf.query.algebra.TupleExpr;

public class SemaGrowConverter extends OpenRdfConverter {
    @Override
    public SimpleTree fromExpr(TupleExpr expr) {
        // Yikes

        // No SemaGrow specific handler for this node
        return super.fromExpr(expr);
    }
}
