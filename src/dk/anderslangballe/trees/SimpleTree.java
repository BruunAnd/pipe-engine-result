package dk.anderslangballe.trees;

import com.fluidops.fedx.algebra.*;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.openrdf.model.Value;
import org.openrdf.query.algebra.*;
import org.openrdf.query.parser.ParsedTupleQuery;
import org.openrdf.repository.sail.SailTupleQuery;

import java.lang.reflect.Field;
import java.util.List;

public abstract class SimpleTree {
    public static SimpleTree fromQuery(SailTupleQuery query) {
        ParsedTupleQuery parsed = query.getParsedQuery();
        TupleExpr tupleExpression = parsed.getTupleExpr();

        return fromExpr(tupleExpression);
    }

    private static SimpleTree joinArguments(List<? extends TupleExpr> args) {
        SimpleTree result = null;
        for (TupleExpr arg : args) {
            if (result == null) {
                result = fromExpr(arg);
            } else {
                result = new SimpleBranch(NodeType.JOIN, result, fromExpr(arg));
            }
        }

        return result;
    }

    public static SimpleTree fromExpr(TupleExpr expr) {
        if (expr instanceof Projection) {
            SimpleTree child = fromExpr(((Projection) expr).getArg());
            return new SimpleBranch(NodeType.PROJECTION, child);
        }

        if (expr instanceof NUnion) {
            NUnion union = (NUnion) expr;
            SimpleTree[] children = new SimpleTree[union.getNumberOfArguments()];

            for (int i = 0; i < union.getNumberOfArguments(); i++) {
                children[i] = fromExpr(union.getArg(0));
            }

            return new SimpleBranch(NodeType.UNION, children);
        }

        if (expr instanceof ExclusiveGroup) {
            return joinArguments(((ExclusiveGroup) expr).getStatements());
        }

        if (expr instanceof NJoin) {
            return joinArguments(((NJoin) expr).getArgs());
            /*SimpleTree result = null;
            for (ExclusiveStatement statement : group.getStatements()) {
                if (result == null) {
                    result = fromExpr(statement);
                } else {
                    result = new SimpleBranch(NodeType.JOIN, result, fromExpr(statement));
                }
            }

            NJoin join = (NJoin) expr;
            join.getar
            SimpleTree result = fromExpr(join.getArg(0));

            for (int i = 1; i < join.getNumberOfArguments(); i++) {
                result = new SimpleBranch(NodeType.JOIN, result, fromExpr(join.getArg(i)));
            }

            return result;*/
        }

        if (expr instanceof StatementPattern) {
            StatementPattern pattern = (StatementPattern) expr;

            String subject = getVarString(pattern.getSubjectVar());
            String predicate = getVarString(pattern.getPredicateVar());
            String object = getVarString(pattern.getObjectVar());

            return new SimpleLeaf(String.format("%s %s %s", subject, predicate, object));
        }

        if (expr instanceof Join) {
            Join join = (Join) expr;
            return new SimpleBranch(NodeType.JOIN, fromExpr(join.getLeftArg()),
                    fromExpr(join.getRightArg()));
        }

        if (expr instanceof Union) {
            Union union = (Union) expr;
            return new SimpleBranch(NodeType.UNION, fromExpr(union.getLeftArg()),
                    fromExpr(union.getRightArg()));
        }

        System.out.println("No handler for node base " + expr);

        if (expr instanceof UnaryTupleOperator) {
            return fromExpr(((UnaryTupleOperator) expr).getArg());
        }

        if (expr instanceof SingleSourceQuery) {
            try {
                return fromExpr((TupleExpr) readField(expr, "parsedQuery"));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return null;
    }

    private static Object readField(Object obj, String fieldName) throws IllegalAccessException, NoSuchFieldException {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(obj);
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


