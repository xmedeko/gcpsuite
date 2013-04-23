package org.xmedeko.gcpsuite;

import org.junit.runner.RunWith;
import org.xmedeko.gcpsuite.GuavaClassPathSuite.ClassNamePredicate;
import org.xmedeko.gcpsuite.GuavaClassPathSuite.ClassPredicate;
import org.xmedeko.gcpsuite.TestSuite.PackageNamePredicate;
import org.xmedeko.gcpsuite.TestSuite.TestInterfacePredicate;

import com.google.common.base.Predicate;
import com.google.common.reflect.Reflection;

@RunWith(GuavaClassPathSuite.class)
@ClassNamePredicate(PackageNamePredicate.class)
@ClassPredicate(TestInterfacePredicate.class)
public class TestSuite {

	/**
	 * Predicate for {@link GuavaClassPathSuite} which filters by a package name.
	 */
	public static class PackageNamePredicate implements Predicate<String> {
		private final String RTS_PACKAGE_NAME = "org.xmedeko.gcpsuite.testpackage";

		@Override
		public boolean apply(String className) {
			String packageName = Reflection.getPackageName(className);
			return packageName.startsWith(RTS_PACKAGE_NAME);
		}
	}

	/**
	 * Predicate for {@link GuavaClassPathSuite} which filters by TestInterface.
	 */
	public static class TestInterfacePredicate implements Predicate<Class<?>> {

		@Override
		public boolean apply(Class<?> clazz) {
			return TestInterface.class.isAssignableFrom(clazz);
		}
	}
}
