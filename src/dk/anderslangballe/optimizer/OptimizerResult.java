package dk.anderslangballe.optimizer;

import dk.anderslangballe.trees.SimpleTree;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class OptimizerResult {
    public String name;
    public String newQuery;
    public SimpleTree<String> plan;
    public long loadingTime;
    public long planningTime;
    public long executionTime;
    public List<Map<String, Object>> tuples;
    public Set<String> bindingNames;

    public OptimizerResult(String name) {
        this.name = name;
    }
}
