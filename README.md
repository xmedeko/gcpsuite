gcpsuite
========

JUnit Guava ClassPath Suite

This is a test suite, which runs all JUnit test found on the class path. The test may be filtered by filters - Guava
predicates. The classpath is scanned by `Guava com.google.common.reflect.ClassPath`. The Guava ClassPath has 
the known limitation, that it does not scans classes in JARs.

All is implemented in one file `org.xmedeko.gcpsuite.GuavaClassPathSuite` - just copy&paste to your project.

Inspired by ClasspathSuite [http://johanneslink.net/projects/cpsuite.jsp]. 


Usage
-----

See `org.xmedeko.gcpsuite.TestSuiteUse`:
1. Use `@RunWith(GuavaClassPathSuite.class)` annotation.
2. Optional - use `@ClassNamePredicate` to filter by class name.
3. Optional - use `@ClassPredicate` to filter by instantiated class. 