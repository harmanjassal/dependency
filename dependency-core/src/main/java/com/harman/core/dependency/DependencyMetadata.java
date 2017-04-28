package com.harman.core.dependency;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Collectors;

public class DependencyMetadata {

    private final Multimap<Integer, Class<?>> dependencyMetadataMapping;

    public DependencyMetadata(Multimap<Integer, Class<?>> dependencyMetadataMapping) {
        this.dependencyMetadataMapping = dependencyMetadataMapping;
    }

    public Iterator<Collection<Class<?>>> iterator() {
        return dependencyMetadataMapping.asMap().keySet().stream().map(index -> dependencyMetadataMapping.get(index)).collect(Collectors.toList()).iterator();
    }

    public Iterator<Collection<Class<?>>> reverseIterator() {
        return ImmutableList.copyOf(dependencyMetadataMapping.asMap().keySet().stream().map(index -> dependencyMetadataMapping.get(index)).collect(Collectors.toList())).reverse().iterator();
    }
}
