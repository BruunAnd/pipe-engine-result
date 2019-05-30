package dk.anderslangballe.trees.converter;

import dk.anderslangballe.trees.HoverValueBranch;
import dk.anderslangballe.trees.NodeType;
import dk.anderslangballe.trees.SimpleBranch;
import dk.anderslangballe.trees.SimpleTree;
import org.openrdf.query.algebra.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OpenRdfConverter extends Converter {

    public SimpleTree makeFilter(ValueExpr condition, SimpleTree child) {
        List<String> values = new ArrayList<>();
        if (condition instanceof Compare) {
            values.add(((Compare) condition).getLeftArg().toString());
            values.add(((Compare) condition).getOperator().getSymbol());
            values.add(((Compare) condition).getRightArg().toString());
        } else {
            System.err.println(String.format("Unknown condition %s", condition.getClass().getName()));
        }

        return new HoverValueBranch(NodeType.FILTER, values.toArray(new String[0]), child).applySources(child.sources);
    }

    @Override
    public SimpleTree fromExpr(TupleExpr expr) {
        // Handle service
        if (expr instanceof Service) {
            return fromExpr(((Service) expr).getArg()).applySources(Collections.singletonList(((Service) expr).getBaseURI()));
        }

        // Handle distinct (temporary solution)
        if (expr instanceof Distinct) {
            return fromExpr(((Distinct) expr).getArg());
        }

        // Handle query root
        if (expr instanceof QueryRoot) {
            return fromExpr(((QueryRoot) expr).getArg());
        }

        // Handle filter
        if (expr instanceof Filter) {
            return makeFilter(((Filter) expr).getCondition(), fromExpr(((Filter) expr).getArg()));
        }

        // Handle projection
        if (expr instanceof Projection) {
            String[] elements = ((Projection) expr).getProjectionElemList().getElements().stream().map(ProjectionElem::getTargetName).toArray(String[]::new);
            return new HoverValueBranch(NodeType.PROJECTION, elements, fromExpr(((Projection) expr).getArg()));
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

        if (expr instanceof EmptySet) {
            return new SimpleBranch(NodeType.EMPTY);
        }

        System.err.println(String.format("No OpenRDF handler for %s, returning null node", expr.getClass().getName()));

        return null;
    }
}
