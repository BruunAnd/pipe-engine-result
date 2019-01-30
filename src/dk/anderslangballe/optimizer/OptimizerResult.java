package dk.anderslangballe.optimizer;

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
