package dk.anderslangballe.trees;

import com.fluidops.fedx.algebra.*;
import org.openrdf.model.Value;
import org.openrdf.query.algebra.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class SimpleTree {
    public List<String> sources;

    public SimpleTree applySources(List<String> sources) {
        this.sources = sources;

        return this;
    }

    public SimpleTree applyStatementSources(List<StatementSource> sources) {
        List<String> stringSources = new ArrayList<>();

        for (StatementSource source : sources) {
            stringSources.add(source.getEndpointID());
        }

        return this.applySources(stringSources);
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

    public static SimpleTree fromStatementPattern(StatementPattern pattern) {
        String subject = getVarString(pattern.getSubjectVar());
        String predicate = getVarString(pattern.getPredicateVar());
        String object = getVarString(pattern.getObjectVar());

        return new SimpleLeaf(String.format("%s %s %s", subject, predicate, object));
    }

    public static SimpleTree fromExpr(TupleExpr expr) {
        if (expr instanceof Projection) {
            SimpleTree child = fromExpr(((Projection) expr).getArg());
            return new SimpleBranch(NodeType.PROJECTION, child);
        }

        // Handle exclusive groups (group of statements that use the same source(s))
        // The sources are applied recursively to the children
        if (expr instanceof ExclusiveGroup) {
            return joinArguments(((ExclusiveGroup) expr).getStatements()).applyStatementSources(((ExclusiveGroup) expr).getStatementSources());
        }

        // Handle FedXStatementPattern (these include the getStatementSources method)
        if (expr instanceof FedXStatementPattern) {
            return fromStatementPattern((StatementPattern) expr).applyStatementSources(((FedXStatementPattern) expr).getStatementSources());
        }

        System.out.println("No source treatment of " + expr);

        // Handle simple statement pattern (no sources, but its parent might apply one recursively)
        if (expr instanceof StatementPattern) {
            return fromStatementPattern((StatementPattern) expr);
        }

        // Handle n-ary joins
        if (expr instanceof NJoin) {
            return joinArguments(((NJoin) expr).getArgs());
        }

        // Handle binary joins
        if (expr instanceof Join) {
            Join join = (Join) expr;
            return new SimpleBranch(NodeType.JOIN, fromExpr(join.getLeftArg()),
                    fromExpr(join.getRightArg()));
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

        // Handle binary unions
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
                // This hackjob is needed to read the parsed query field from SingleSourceQuery
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


