package com.harman.core.dependency;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class DependencyContext {
    private List<String> basePackagesToScan;

    public DependencyContext(List<String> basePackagesToScan) {
        this.basePackagesToScan = basePackagesToScan;
    }

    public DependencyContext() {
        this.basePackagesToScan = new LinkedList<>();
    }

    public void addPackage(String packageToScan) {
        Objects.requireNonNull(packageToScan);
        this.basePackagesToScan.add(packageToScan);
    }

    public List<String> getBasePackagesToScan() {
        return basePackagesToScan;
    }
}
