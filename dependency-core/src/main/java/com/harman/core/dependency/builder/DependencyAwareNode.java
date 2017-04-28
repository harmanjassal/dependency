package com.harman.core.dependency.builder;

import com.google.common.base.MoreObjects;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

class DependencyAwareNode<Type> {
    private final Type type;
    private List<DependencyAwareNode<Type>> parents;
    private List<DependencyAwareNode<Type>> children;

    DependencyAwareNode(Type type) {
        this.type = type;
        parents = new ArrayList<DependencyAwareNode<Type>>();
        children = new ArrayList<DependencyAwareNode<Type>>();
    }


    DependencyAwareNode<Type> addParent(DependencyAwareNode<Type> parent) {
        if (!parents.contains(parent)) {
            parent.addChild(this);
            parents.add(parent);
        }
        return this;
    }

    DependencyAwareNode<Type> addChild(DependencyAwareNode<Type> child) {
        children.add(child);
        return this;
    }

    int getDepth() {
        return findDepth(this, 1);
    }

    private int findDepth(DependencyAwareNode<Type> node, final int level) {
        final Set<DependencyAwareNode<Type>> visitedNodes = new HashSet<>();
        final AtomicInteger computedLevel = new AtomicInteger(level);
        node.getParents().stream().forEach(parent -> {
            if (!visitedNodes.contains(parent)) {
                int depth = findDepth(parent, level + 1);
                if (depth > level) {
                    computedLevel.set(depth);
                }
                visitedNodes.add(parent);
            }
        });
        return computedLevel.get();
    }

    public List<DependencyAwareNode<Type>> getParents() {
        return parents;
    }

    public Type getType() {
        return type;
    }

    public List<DependencyAwareNode<Type>> getChildren() {
        return children;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("type", type)

                .toString();
    }
}
