package dk.anderslangballe.trees;

import com.fluidops.fedx.algebra.StatementSource;
import dk.anderslangballe.trees.converter.FedXConverter;
import dk.anderslangballe.trees.converter.SemaGrowConverter;
import org.openrdf.model.Value;
import org.openrdf.query.algebra.StatementPattern;
import org.openrdf.query.algebra.TupleExpr;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public abstract class SimpleTree {
    public List<String> sources;

    public SimpleTree applySources(List<String> sources) {
        this.sources = sources;

        return this;
    }

    // FedX specific statement sources
    public SimpleTree applySourcesFedX(List<StatementSource> sources) {
        List<String> stringSources = new ArrayList<>();

        for (StatementSource source : sources) {
            stringSources.add(source.getEndpointID());
        }

        return this.applySources(stringSources);
    }

    public static SimpleTree fromStatementPattern(StatementPattern pattern) {
        String subject = getVarString(pattern.getSubjectVar());
        String predicate = getVarString(pattern.getPredicateVar());
        String object = getVarString(pattern.getObjectVar());

        return new SimpleLeaf(String.format("%s %s %s", subject, predicate, object));
    }

    public static SimpleTree fromFedX(TupleExpr expr) {
        return new FedXConverter().fromExpr(expr);
    }

    private static SimpleTree fromSemaGrow(TupleExpr expr) {
        return new SemaGrowConverter().fromExpr(expr);
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


