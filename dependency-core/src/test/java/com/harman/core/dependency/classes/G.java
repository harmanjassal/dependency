package com.harman.core.dependency.classes;

import com.harman.core.dependency.metadata.DependencyAware;
import com.harman.core.dependency.metadata.DependsOn;

@DependencyAware
@DependsOn(dependsOn = {E.class})
public class G {
}
