# README #

This module will make it easy to define dependencies between multiple entities using metadata. This will be particularly useful when developers have to write code to process entities in order of dependencies. 

### What is this repository for? ###

* This module will make it easy to define dependencies between multiple entities using metadata. This will be particularly useful when developers have to write code to process entities in order of dependencies. 
  e.g. Consider a scenario where you have to do calculation by invoking multiple calculators where each calculator has to be invoked in a sequence of dependency
* 1.0.0-SNAPSHOT
* [Learn Markdown](https://bitbucket.org/tutorials/markdowndemo)

### How do I get set up? ###

* Put a dependency on the module

```
#!xml

<group>com.harman.core</group>
<artifactId>dependency-core</artifactId>
```
* In order for this module to work you will need to configure two type of entities
** Root
*** Root entities are configured using annotation **@DependencyAware**
** Child
*** Child entities are configured using annotation **@DependencyAware** and **@DependsOn**. DependsOn annotation defines the dependencies of the entity on other root or child nodes.
Once configured you will need to build the metadata by passing the base package to scan

```
#!java

DependencyMetadata dependencyMetadata = MetadataBuilder.buildGraph(new DependencyContext(Lists.newArrayList("com.harman")));
Iterator<Collection<Class<?>>> iterator = dependencyMetadata.iterator();
       
```

Iterator will return the list of classes that are the nth level parent in the graph. First call to next will return root nodes and next call will return Level 1 nodes and henceforth.



![Alt text](http://g.gravizo.com/source/dependencygraph?http%3A%2F%2Fwww.gravizo.com#
dependencygraph
  digraph G{
    aize ="4,4";
    A [shape=circle];
    B [shape=circle];
    C [shape=circle];
    A -> D [weight=8];
    A -> E;
    D -> F;
    F -> G;
    D -> G;
    B -> G;
    B -> F;
    C -> F;
  }
dependencygraph)


### Who do I talk to? ###

* Repo owner or admin
* Other community or team contact