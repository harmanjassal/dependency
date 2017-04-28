package com.harman.core.dependency.builder;

import com.google.common.collect.Iterables;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class GraphCycleDetector {
    private final List<DependencyAwareNode<Class<?>>> graph;

    GraphCycleDetector(List<DependencyAwareNode<Class<?>>> graph) {
        this.graph = graph;
    }

    public void detectCyclicDependency() {
        List<DependencyAwareNode<?>> vis¥itedNodes = new LinkedList<>();

        Set<DependencyAwareNode<?>> toBeVisited = this.graph.stream().filter(node -> node.getChildren().isEmpty()).collect(Collectors.toSet());

        while (!(toBeVisited.isEmpty())) {
            DependencyAwareNode<?> currentNode = Iterables.getFirst(toBeVisited, null);

            toBeVisited.remove(currentNode);

            for (Iterator<? extends DependencyAwareNode<?>> edges = currentNode.getParents().iterator(); edges.hasNext(); ) {
                DependencyAwareNode<?> adjacentParent = edges.next();
                edges.remove();
                adjacentParent.getChildren().remove(currentNode);

                if (adjacentParent.getChildren().isEmpty()) {
                    toBeVisited.add(adjacentParent);
                }
            }

        }

        boolean hasAnyNodeWithChildren = toBeVisited.stream().filter(node -> !node.getChildren().isEmpty()).findFirst().isPresent();

        if (hasAnyNodeWithChildren) {
            throw new RuntimeException("Dependency graph has cyclic dependency");
        }
    }
}
