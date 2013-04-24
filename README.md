gcpsuite
========

JUnit Guava ClassPath Suite

This is a test suite, which runs all JUnit test found on the class path. The test may be filtered by filters - Guava
Predicates. The classpath is scanned by Guava `com.google.common.reflect.ClassPath`. The Guava `ClassPath` scanning has 
the known limitation, that it does not scan classes in JARs.

All is implemented in one file [`org.xmedeko.gcpsuite.GuavaClassPathSuite`](https://github.com/xmedeko/gcpsuite/blob/master/src/main/java/org/xmedeko/gcpsuite/GuavaClassPathSuite.java) - just copy&paste to your project.

Inspired by ClasspathSuite [http://johanneslink.net/projects/cpsuite.jsp]. 


Usage
-----

1. Use `@RunWith(GuavaClassPathSuite.class)` annotation.
2. Optional - use `@ClassNamePredicate` to filter by class name.
3. Optional - use `@ClassPredicate` to filter by instantiated class.

See the example [`org.xmedeko.gcpsuite.TestSuiteUse`](https://github.com/xmedeko/gcpsuite/blob/master/src/test/java/org/xmedeko/gcpsuite/TestSuite.java).
