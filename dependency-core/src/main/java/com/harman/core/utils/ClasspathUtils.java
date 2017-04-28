package com.harman.core.utils;

import com.google.common.collect.ImmutableList;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ClasspathUtils {

    private ClasspathUtils() {
    }

    public static Collection<? extends Class<?>> scanClassAnnotatedWith(Class<? extends Annotation> annotationType, String... basePackages) {
        if (basePackages == null || basePackages.length == 0) {
            return ImmutableList.of();
        }
        AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(annotationType);

        return scannClassesUsingFilter(annotationTypeFilter, basePackages);

    }

    private static Collection<? extends Class<?>> scannClassesUsingFilter(AnnotationTypeFilter annotationTypeFilter, String[] basePackages) {
        final ClassPathScanningCandidateComponentProvider classPathScanningCandidateComponentProvider = new ClassPathScanningCandidateComponentProvider(false);

        classPathScanningCandidateComponentProvider.addIncludeFilter(annotationTypeFilter);
        List<? extends Class<?>> toReturn = Arrays.stream(basePackages).map(basePackage -> classPathScanningCandidateComponentProvider.findCandidateComponents(basePackage)).flatMap(bd -> bd.stream()).map(bd -> {
            try {
                return (ClassUtils.forName(bd.getBeanClassName(), ClasspathUtils.class.getClassLoader()));
            } catch (ClassNotFoundException e) {
                return null;
            }
        }).filter(result -> result != null).collect(Collectors.toList());
        return toReturn;
    }
}
