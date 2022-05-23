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

    static class Graph {
        private final HashMap<String, Vertex> adj = new HashMap<>();
        private final String source;

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

        public boolean bellmanFord() {
            for (int i = 1; i < adj.size() - 1; i++) {
                adj.forEach((uLabel, u) -> relax(uLabel));
            }
            return adj.values().stream().anyMatch(u -> u.edges.entrySet().stream().noneMatch(entry ->
                adj.get(entry.getKey()).weight >
                        (u.weight == INFINITY ? INFINITY : u.weight + entry.getValue())
            ));
        }

        public LinkedHashSet<Vertex> djikstra() {
            reset();
            LinkedHashSet<Vertex> shortestPath = new LinkedHashSet<>();
            PriorityQueue<Vertex> queue = new PriorityQueue<>(Comparator.comparingInt(v -> v.weight));
            queue.addAll(adj.values());
            while (!queue.isEmpty()) {
                Vertex u = queue.poll();
                shortestPath.add(u);
                relax(u.label);
            }
            return shortestPath;
        }

        private void relax(String u) {
            adj.get(u).edges.forEach((vLabel, vWeight) -> {
                if (adj.get(vLabel).weight >
                        (adj.get(u).weight == INFINITY ? INFINITY : adj.get(u).weight + vWeight)) {
                    adj.get(vLabel).weight = adj.get(u).weight + vWeight;
                    adj.get(vLabel).predecessor = adj.get(u);
                }
            });
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

    public static void main(String[] args) {
        Graph graph = new Graph("s", new String[]{"t", "y"}, new Integer[]{6, 7});
        graph.addEdge("t", new String[]{"x", "y", "z"}, new Integer[]{5, 8, -4});
        graph.addEdge("x", new String[]{"t"}, new Integer[]{-2});
        graph.addEdge("y", new String[]{"x", "z"}, new Integer[]{-3, 9});
        graph.addEdge("z", new String[]{"s", "x"}, new Integer[]{2, 7});
        System.out.println(graph.bellmanFord());
        graph.print();
        graph = new Graph("s", new String[]{"t", "y"}, new Integer[]{10, 5});
        graph.addEdge("t", new String[]{"x", "y"}, new Integer[]{1, 2});
        graph.addEdge("x", new String[]{"z"}, new Integer[]{4});
        graph.addEdge("y", new String[]{"t","x", "z"}, new Integer[]{3, 9, 2});
        graph.addEdge("z", new String[]{"s", "x"}, new Integer[]{7, 6});
        graph.djikstra().forEach(System.out::println);
    }
}
