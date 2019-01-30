package dk.anderslangballe.trees;

import org.openrdf.model.Value;
import org.openrdf.query.algebra.*;
import org.openrdf.query.parser.ParsedTupleQuery;
import org.openrdf.repository.sail.SailTupleQuery;

public abstract class SimpleTree<E> {
    public static SimpleTree<String> fromQuery(SailTupleQuery query) {
        ParsedTupleQuery parsed = query.getParsedQuery();
        TupleExpr tupleExpression = parsed.getTupleExpr();

        return fromExpr(tupleExpression);
    }

    private static SimpleTree<String> fromExpr(TupleExpr expr) {
        if (expr instanceof Projection) {
            SimpleTree<String> child = fromExpr(((Projection) expr).getArg());
            return new OperatorLeaf<>(NodeType.PROJECTION, child);
        }

        if (expr instanceof StatementPattern) {
            StatementPattern pattern = (StatementPattern) expr;

            String subject = getVarString(pattern.getSubjectVar());
            String predicate = getVarString(pattern.getPredicateVar());
            String object = getVarString(pattern.getObjectVar());

            return new LiteralLeaf<>(String.format("%s %s %s", subject, predicate, object));
        }

        if (expr instanceof Join) {
            Join join = (Join) expr;
            return new SimpleBranch<>(NodeType.JOIN, fromExpr(join.getLeftArg()),
                    fromExpr(join.getRightArg()));
        }

        if (expr instanceof Union) {
            Union union = (Union) expr;
            return new SimpleBranch<>(NodeType.UNION, fromExpr(union.getLeftArg()),
                    fromExpr(union.getRightArg()));
        }

        System.out.println("No handler for node base " + expr);

        if (expr instanceof UnaryTupleOperator) {
            return fromExpr(((UnaryTupleOperator) expr).getArg());
        }

        return null;
    }

    private static String getVarString(org.openrdf.query.algebra.Var var) {
        if (var == null) {
            return ".";
        }

        Value value = var.getValue();
        if (value != null) {
            return value.stringValue();
        }

        return String.format("?%s", var.getName());
    }
}


