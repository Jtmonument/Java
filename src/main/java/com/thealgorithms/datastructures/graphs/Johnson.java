package com.thealgorithms.datastructures.graphs;

import java.util.*;

public class Johnson {

    private static final int INFINITY = Integer.MAX_VALUE;

    static class Vertex {
        public String label;
        public int weight = INFINITY;
        public Vertex predecessor;
        public final HashMap<String, Integer> edges = new HashMap<>();

        public Vertex(String label) {
            this.label = label;
        }

        @Override
        public String toString() {
            StringBuilder stringOfEdges = new StringBuilder();
            stringOfEdges.append(predecessor == null ? "NONE" : predecessor.label).append("\n\tEdges:");
            edges.forEach((v, cost) -> stringOfEdges.append("\n\t\t").append(cost).append(" ")
                        .append(label).append(" -> ").append(v));
            return "Vertex " + label +
                    ":\n\tWeight: " + weight +
                    "\n\tPredecessor: " +
                    stringOfEdges;
        }
    }

    static class NegativeWeightCycleException extends Exception {

        public NegativeWeightCycleException(String message) {
            super(message);
        }
    }

    static class Graph {
        private final HashMap<String, Vertex> adj = new HashMap<>();
        private String source;

        enum WeightFunction {
            NORMAL, REWEIGH
        }

        public Graph(String source, String[] next, Integer[] weights) {
            addEdge((this.source = source), next, weights);
            adj.get(source).weight = 0;
        }

        public void addEdge(String label, String[] next, Integer[] weights) {
            adj.put(label, new Vertex(label));
            for (int i = 0; i < next.length; i++) {
                adj.get(label).edges.put(next[i], weights[i]);
            }
        }

        public boolean bellmanFord(WeightFunction w) {
            for (int i = 1; i < adj.size() - 1; i++) {
                adj.forEach((uLabel, u) -> relax(uLabel, w));
            }
            return adj.values().stream().anyMatch(u -> u.edges.entrySet().stream().noneMatch(entry ->
                adj.get(entry.getKey()).weight >
                        (u.weight == INFINITY ? INFINITY : u.weight + entry.getValue())
            ));
        }

        public HashMap<String, Vertex> djikstra(WeightFunction w) {
            reset();
            HashMap<String, Vertex> shortestPath = new HashMap<>();
            PriorityQueue<Vertex> queue = new PriorityQueue<>(Comparator.comparingInt(v -> v.weight));
            queue.addAll(adj.values());
            while (!queue.isEmpty()) {
                Vertex u = queue.poll();
                shortestPath.put(u.label, u);
                relax(u.label, w);
            }
            return shortestPath;
        }

        public ArrayList<HashMap<String, Vertex>> johnson() throws NegativeWeightCycleException {
            computePrime();
            if (!bellmanFord(WeightFunction.NORMAL)) {
                 throw new NegativeWeightCycleException("This graph contains a negative weight cycle.");
            }
            /*S(u, v)*/
            adj.forEach((uLabel, u) -> {
                u.edges.forEach((vLabel, vWeight) -> {
                    u.edges.put(vLabel, vWeight + u.weight - adj.get(vLabel).weight);
                });
            });
            ArrayList<HashMap<String, Vertex>> matrix = new ArrayList<>(){
                @Override
                public String toString() {
                    forEach(vertices -> {
                        vertices.values().forEach(System.out::println);
                        System.out.println("-".repeat(20));
                    });
                    return "";
                }
            };
            adj.remove(source);
            adj.forEach((uLabel, u) -> {
                source = uLabel;
                HashMap<String, Vertex> list = djikstra(WeightFunction.REWEIGH);
                u.edges.forEach((vLabel, vWeight) -> {
                    list.get(uLabel).edges.put(vLabel, /*^S(u, v)*/ +
                           vWeight + list.get(vLabel).weight - list.get(uLabel).weight);
                });
                matrix.add(list);
            });
            return matrix;
        }

        private void computePrime() {
            adj.get(source).weight = INFINITY;
            source = "S";
            Integer[] weights = new Integer[adj.size()];
            Arrays.fill(weights, 0);
            addEdge(source, adj.keySet().toArray(new String[0]), weights);
            adj.get(source).weight = 0;
        }

        private void relax(String u, WeightFunction w) {
            if (w.equals(WeightFunction.NORMAL)) {
                adj.get(u).edges.forEach((vLabel, vWeight) -> {
                    if (adj.get(vLabel).weight >
                            (adj.get(u).weight == INFINITY ? INFINITY : adj.get(u).weight + vWeight)) {
                        adj.get(vLabel).weight = adj.get(u).weight + vWeight;
                        adj.get(vLabel).predecessor = adj.get(u);
                    }
                });
            } else if (w.equals(WeightFunction.REWEIGH)) {
                adj.get(u).edges.forEach((vLabel, vWeight) -> {
                    if (adj.get(vLabel).weight >
                            (adj.get(u).weight == INFINITY || adj.get(vLabel).weight == INFINITY ? INFINITY :
                                    vWeight + adj.get(u).weight - adj.get(vLabel).weight)) {
                        adj.get(vLabel).weight = vWeight + adj.get(u).weight - adj.get(vLabel).weight;
                        adj.get(vLabel).predecessor = adj.get(u);
                    }
                });
            }
        }

        public void reset() {
            adj.forEach((label, vertex) -> {
                vertex.weight = vertex.label.equals(source) ? 0 : INFINITY;
                vertex.predecessor = null;
            });
        }

        public void print() {
            adj.values().forEach(System.out::println);
        }
    }

    public static void main(String[] args) throws NegativeWeightCycleException {
        /*Graph graph = new Graph("s", new String[]{"t", "y"}, new Integer[]{6, 7});
        graph.addEdge("t", new String[]{"x", "y", "z"}, new Integer[]{5, 8, -4});
        graph.addEdge("x", new String[]{"t"}, new Integer[]{-2});
        graph.addEdge("y", new String[]{"x", "z"}, new Integer[]{-3, 9});
        graph.addEdge("z", new String[]{"s", "x"}, new Integer[]{2, 7});
        System.out.println(graph.bellmanFord());
        graph.print();*/
        /*Graph graph = new Graph("s", new String[]{"t", "y"}, new Integer[]{10, 5});
        graph.addEdge("t", new String[]{"x", "y"}, new Integer[]{1, 2});
        graph.addEdge("x", new String[]{"z"}, new Integer[]{4});
        graph.addEdge("y", new String[]{"t","x", "z"}, new Integer[]{3, 9, 2});
        graph.addEdge("z", new String[]{"s", "x"}, new Integer[]{7, 6});
        graph.djikstra(Graph.WeightFunction.NORMAL);
        graph.print();*/
        Graph graph = new Graph("1", new String[]{"2", "3", "5"}, new Integer[]{3, 8, -4});
        graph.addEdge("2", new String[]{"4", "5"}, new Integer[]{1, 7});
        graph.addEdge("3", new String[]{"2"}, new Integer[]{4});
        graph.addEdge("4", new String[]{"1", "3"}, new Integer[]{2, -5});
        graph.addEdge("5", new String[]{"4"}, new Integer[]{6});
        System.out.println(graph.johnson());
    }
}
