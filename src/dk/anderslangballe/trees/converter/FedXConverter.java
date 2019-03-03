package dk.anderslangballe.trees.converter;

import com.fluidops.fedx.algebra.*;
import dk.anderslangballe.trees.NodeType;
import dk.anderslangballe.trees.SimpleBranch;
import dk.anderslangballe.trees.SimpleTree;
import org.openrdf.query.algebra.Service;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.TupleExpr;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

// The FedX converter is used by Odyssey, FedX and FedX-HiBISCus
public class FedXConverter extends OpenRdfConverter {
    @Override
    public SimpleTree fromExpr(TupleExpr expr) {
        // Handle filters
        if (expr instanceof FilterTuple) {
            if (((FilterTuple) expr).hasFilter()) {
                FilterValueExpr valueExpr = ((FilterTuple) expr).getFilterExpr();
                if (valueExpr instanceof FilterExpr) {
                    return makeFilter(((FilterExpr) valueExpr).getExpression(), fromExprInner(expr));
                }
            }
        }

        return fromExprInner(expr);
    }

    private SimpleTree fromExprInner(TupleExpr expr) {
        if (expr instanceof SingleSourceQuery) {
            try {
                // This hackjob is needed to read the parsed query field from SingleSourceQuery
                List<String> sources = Collections.singletonList(((SingleSourceQuery) expr).getSource().getEndpoint());
                return fromExpr((TupleExpr) readField(expr, "parsedQuery")).applySources(sources);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // Handle exclusive groups (group of statements that use the same source(s))
        // The sources are applied recursively to the children
        if (expr instanceof ExclusiveGroup) {
            return applySources(joinArguments(((ExclusiveGroup) expr).getStatements()), ((ExclusiveGroup) expr).getStatementSources());
        }

        // Handle FedXStatementPattern (these include the getStatementSources method)
        if (expr instanceof FedXStatementPattern) {
            return applySources(SimpleTree.fromStatementPattern((StatementPattern) expr), ((FedXStatementPattern) expr).getStatementSources());
        }

        // Handle FedXService
        if (expr instanceof FedXService) {
            Service service = ((FedXService) expr).getService();

            return fromExpr(service.getArg()).applySources(Collections.singletonList(service.getServiceRef().getValue().stringValue()));
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

        // No FedX handler for this, can possibly be an OpenRDF node
        return super.fromExpr(expr);
    }

    public SimpleTree applySources(SimpleTree tree, List<StatementSource> sources) {
        return tree.applySources(sources.stream().map(StatementSource::getEndpointID).collect(Collectors.toList()));
    }
}
