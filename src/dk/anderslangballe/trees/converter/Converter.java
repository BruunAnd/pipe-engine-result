package dk.anderslangballe.trees.converter;

import dk.anderslangballe.trees.NodeType;
import dk.anderslangballe.trees.SimpleBranch;
import dk.anderslangballe.trees.SimpleTree;
import org.openrdf.query.algebra.TupleExpr;

import java.lang.reflect.Field;
import java.util.List;

public abstract class Converter {
    public abstract SimpleTree fromExpr(TupleExpr expr);

    SimpleTree joinArguments(List<? extends TupleExpr> args) {
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

    static Object readField(Object obj, String fieldName) throws IllegalAccessException, NoSuchFieldException {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(obj);
    }
}
