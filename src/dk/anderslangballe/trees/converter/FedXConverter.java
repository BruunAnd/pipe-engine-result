package dk.anderslangballe.trees.converter;

import com.fluidops.fedx.algebra.*;
import dk.anderslangballe.trees.NodeType;
import dk.anderslangballe.trees.SimpleBranch;
import dk.anderslangballe.trees.SimpleTree;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.TupleExpr;

// The FedX converter is used by Odyssey, FedX and FedX-HiBISCus
public class FedXConverter extends OpenRdfConverter {
    @Override
    public SimpleTree fromExpr(TupleExpr expr) {
        if (expr instanceof SingleSourceQuery) {
            try {
                // This hackjob is needed to read the parsed query field from SingleSourceQuery
                return fromExpr((TupleExpr) readField(expr, "parsedQuery"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // Handle exclusive groups (group of statements that use the same source(s))
        // The sources are applied recursively to the children
        if (expr instanceof ExclusiveGroup) {
            return joinArguments(((ExclusiveGroup) expr).getStatements()).applySourcesFedX(((ExclusiveGroup) expr).getStatementSources());
        }

        // Handle FedXStatementPattern (these include the getStatementSources method)
        if (expr instanceof FedXStatementPattern) {
            return SimpleTree.fromStatementPattern((StatementPattern) expr).applySourcesFedX(((FedXStatementPattern) expr).getStatementSources());
        }

        System.err.println(String.format("No source treatment of %s", expr.getClass().getName()));

        // Handle n-ary joins
        if (expr instanceof NJoin) {
            return joinArguments(((NJoin) expr).getArgs());
        }

        // Handle n-ary unions
        if (expr instanceof NUnion) {
            NUnion union = (NUnion) expr;
            SimpleTree[] children = new SimpleTree[union.getNumberOfArguments()];

            for (int i = 0; i < union.getNumberOfArguments(); i++) {
                children[i] = fromExpr(union.getArg(i));
            }

            return new SimpleBranch(NodeType.UNION, children);
        }

        // TODO: FedXService

        // No FedX handler for this, can possibly be an OpenRDF node
        return super.fromExpr(expr);
    }
}
