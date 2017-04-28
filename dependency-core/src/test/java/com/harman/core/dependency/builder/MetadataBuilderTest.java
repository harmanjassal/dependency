package com.harman.core.dependency.builder;

import com.google.common.collect.Lists;
import com.harman.core.dependency.DependencyContext;
import com.harman.core.dependency.DependencyMetadata;
import com.harman.core.dependency.classes.*;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.Iterator;

public class MetadataBuilderTest {

    @Test
    public void testMappingBuilder() {
        DependencyMetadata dependencyMetadata = MetadataBuilder.buildGraph(new DependencyContext(Lists.newArrayList("com.harman")));
        Iterator<Collection<Class<?>>> iterator = dependencyMetadata.iterator();
        Assert.assertThat(iterator.next(), IsCollectionContaining.hasItems(A.class, E.class));
        Assert.assertThat(iterator.next(), IsCollectionContaining.hasItems(B.class, G.class));
        Assert.assertThat(iterator.next(), IsCollectionContaining.hasItems(C.class, D.class));
        Assert.assertThat(iterator.next(), IsCollectionContaining.hasItems(F.class));
    }

}