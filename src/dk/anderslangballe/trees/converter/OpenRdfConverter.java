package dk.anderslangballe.trees.converter;

import dk.anderslangballe.trees.NodeType;
import dk.anderslangballe.trees.SimpleBranch;
import dk.anderslangballe.trees.SimpleTree;
import org.openrdf.query.algebra.*;

public class OpenRdfConverter extends Converter {
    @Override
    public SimpleTree fromExpr(TupleExpr expr) {
        // Handle query root
        if (expr instanceof QueryRoot) {
            return fromExpr(((QueryRoot) expr).getArg());
        }

        // Handle projection
        if (expr instanceof Projection) {
            SimpleTree child = fromExpr(((Projection) expr).getArg());
            return new SimpleBranch(NodeType.PROJECTION, child);
        }

        // Handle binary joins
        if (expr instanceof Join) {
            Join join = (Join) expr;
            return new SimpleBranch(NodeType.JOIN, fromExpr(join.getLeftArg()), fromExpr(join.getRightArg()));
        }

        // Handle binary left joins
        if (expr instanceof LeftJoin) {
            LeftJoin join = (LeftJoin) expr;
            return new SimpleBranch(NodeType.LEFT_JOIN, fromExpr(join.getLeftArg()), fromExpr(join.getRightArg()));
        }

        // Handle binary unions
        if (expr instanceof Union) {
            Union union = (Union) expr;
            return new SimpleBranch(NodeType.UNION, fromExpr(union.getLeftArg()), fromExpr(union.getRightArg()));
        }

        // Handle simple statement pattern (no sources, but its parent might apply one recursively)
        if (expr instanceof StatementPattern) {
            return SimpleTree.fromStatementPattern((StatementPattern) expr);
        }

        System.err.println(String.format("No OpenRDF conversion of %s", expr.getClass().getName()));

        return null;
    }
}
