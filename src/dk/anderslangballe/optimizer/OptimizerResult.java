package dk.anderslangballe.optimizer;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.anderslangballe.trees.SimpleTree;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.sail.SailTupleQuery;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class OptimizerResult {
    private static OptimizerResult _instance;

    public String name;
    public String newQuery;
    public SimpleTree plan;
    public long planningTime;
    public long executionTime;
    public List<Map<String, Object>> tuples;
    public Set<String> bindingNames;

    private OptimizerResult(String name) {
        this.name = name;
    }

    public static OptimizerResult getInstance() {
        return OptimizerResult._instance;
    }

    public OptimizerResult createInstance(String name) {
        // Overwrites instance if there is already one
        _instance = new OptimizerResult(name);

        return _instance;
    }

    public void evaluateQuery(TupleQuery query) throws QueryEvaluationException {
        evaluateQuery(query, false);
    }

    public void evaluateQuery(TupleQuery query, boolean subtractPlanningTime) throws QueryEvaluationException {
        // Set plan
        if (query instanceof SailTupleQuery) {
            this.plan = SimpleTree.fromQuery((SailTupleQuery) query);
        }

        // Evaluate query
        long start = System.currentTimeMillis();
        TupleQueryResult res = query.evaluate();
        List<Map<String, Object>> tuples = new ArrayList<>();

        while (res.hasNext()) {
            BindingSet next = res.next();
            Map<String, Object> tuple = new HashMap<>();
            for (String bindingName : next.getBindingNames()) {
                tuple.put(bindingName, next.getValue(bindingName));
            }
            tuples.add(tuple);
        }

        // Set execution time
        this.executionTime = System.currentTimeMillis() - start;

        // Get union of binding names
        Set<String> bindingNames = new HashSet<>();
        for (Map<String, Object> tuple : tuples) {
            bindingNames.addAll(tuple.keySet());
        }

        this.tuples = tuples;
        this.bindingNames = bindingNames;

        // Subtract planning time from execution time if requested
        if (subtractPlanningTime) {
            this.executionTime -= this.planningTime;
        }
    }

    public void saveToFile(String file) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(new File(file), this);
    }
}
