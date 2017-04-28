package com.harman.core.dependency.builder;

import com.google.common.collect.Multimap;
import com.google.common.collect.Ordering;
import com.google.common.collect.TreeMultimap;
import com.harman.core.dependency.DependencyContext;
import com.harman.core.dependency.DependencyMetadata;
import com.harman.core.dependency.metadata.DependencyAware;
import com.harman.core.dependency.metadata.DependsOn;
import com.harman.core.utils.ClasspathUtils;
import com.rits.cloning.Cloner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

public final class MetadataBuilder {
    private final Multimap<Integer, Class<?>> dependencyMetadataMapping = TreeMultimap.create(Ordering.natural(), Ordering.from(new Comparator<Class<?>>() {
        @Override
        public int compare(Class<?> o1, Class<?> o2) {
            return o1.getSimpleName().compareTo(o2.getSimpleName());
        }
    }));
    private final List<DependencyAwareNode<Class<?>>> acyclicDependencyGraph = new LinkedList<>();
    private final Map<Class<?>, DependencyAwareNode<Class<?>>> classMapping = new HashMap<>();
    private Logger LOGGER = LogManager.getLogger(MetadataBuilder.class);

    private MetadataBuilder() {

    }

    public static DependencyMetadata buildGraph(DependencyContext dependencyContext) {
        MetadataBuilder metadataBuilder = new MetadataBuilder();
        metadataBuilder.initializeUsingOptions(dependencyContext);
        return new DependencyMetadata(metadataBuilder.dependencyMetadataMapping);
    }

    private void initializeUsingOptions(DependencyContext dependencyContext) {
        Collection<? extends Class<?>> dependentClasses = scanDependentClasses(dependencyContext);
        Collection<? extends Class<?>> allClasses = scanAllDependencyAwareClasses(dependencyContext);
        allClasses.removeAll(dependentClasses);
        validat(allClasses, "At least one class should be configured as root");
        validat(dependentClasses, "At least one class should be configured as dependent");

        buildRootNodeMapping(allClasses);
        buildDependencyMapping(dependentClasses);

        detectCycle();

        for (DependencyAwareNode<Class<?>> dependencyAwareNode : acyclicDependencyGraph) {
            dependencyMetadataMapping.put(dependencyAwareNode.getDepth(), dependencyAwareNode.getType());
        }
    }

    private void detectCycle() {
        List<DependencyAwareNode<Class<?>>> dependencyAwareNodes = new Cloner().deepClone(acyclicDependencyGraph);
        new GraphCycleDetector(dependencyAwareNodes).detectCyclicDependency();
    }

    private void validat(Collection<? extends Class<?>> allClasses, String message) {
        if (CollectionUtils.isEmpty(allClasses)) {
            throw new RuntimeException(message);
        }

    }

    private void buildDependencyMapping(Collection<? extends Class<?>> dependentClasses) {
        dependentClasses.stream().forEach(dependentClass -> createDependencyFor(dependentClass));
    }

    private void createDependencyFor(Class<?> dependentClass) {
        if (!classMapping.containsKey(dependentClass)) {
            createClassMetadata(dependentClass);
        }

        createDependenciesFor(dependentClass);
    }

    private void createDependenciesFor(Class<?> dependentClass) {
        DependencyAwareNode<Class<?>> node = classMapping.get(dependentClass);
        DependsOn annotation = dependentClass.getAnnotation(DependsOn.class);
        Objects.requireNonNull(annotation, String.format("No dependsOn annotation defined for class %s", dependentClass.getSimpleName()));
        Objects.requireNonNull(annotation.dependsOn(), String.format("DependsOn annotation not configured for class %s", dependentClass.getSimpleName()));
        List<? extends Class<?>> dependencyList = Arrays.stream(annotation.dependsOn()).filter(dependency -> dependency != null).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(dependencyList)) {
            throw new RuntimeException(String.format("Class %s has not defined any dependnecies", dependentClass.getSimpleName()));
        }
        if (dependencyList.contains(dependentClass)) {
            throw new RuntimeException(String.format("Class %s cannot have self dependency", dependentClass.getSimpleName()));

        }
        dependencyList.stream().forEach(parentClass -> {
            createClassMetadata(parentClass);
            node.addParent(classMapping.get(parentClass));
        });


    }

    private void buildRootNodeMapping(Collection<? extends Class<?>> rootNodes) {
        rootNodes.stream().forEach(rootNodeClass -> {
            createClassMetadata(rootNodeClass);
        });
    }

    private void createClassMetadata(Class<?> rootNodeClass) {
        if (classMapping.get(rootNodeClass) == null) {
            DependencyAwareNode<Class<?>> node = new DependencyAwareNode<Class<?>>(rootNodeClass);
            acyclicDependencyGraph.add(node);
            classMapping.put(rootNodeClass, node);
        }
    }

    private Collection<? extends Class<?>> scanAllDependencyAwareClasses(DependencyContext dependencyContext) {
        List<String> basePackagesToScan = dependencyContext.getBasePackagesToScan();
        Collection<? extends Class<?>> classes = ClasspathUtils.scanClassAnnotatedWith(DependencyAware.class, basePackagesToScan.toArray(new String[basePackagesToScan.size()]));
        return classes;
    }

    private Collection<? extends Class<?>> scanDependentClasses(DependencyContext dependencyContext) {
        List<String> basePackagesToScan = dependencyContext.getBasePackagesToScan();
        Collection<? extends Class<?>> classes = ClasspathUtils.scanClassAnnotatedWith(DependsOn.class, basePackagesToScan.toArray(new String[basePackagesToScan.size()]));
        return classes;
    }


}
