/*
 * Copyright (C) 2013 Ondrej Medek Licensed under the Poetic Licence, see LICENSE.txt.
 */
package org.xmedeko.gcpsuite;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;
import com.google.common.reflect.Reflection;

/**
 * A Runner for JUnit, which acts like Suite. It runs test classes from {@link ClassPath#getTopLevelClasses()}. These
 * classes are filtered by
 * <ol>
 * <li>Optional {@link ClassNamePredicate}.</li>
 * <li>Internal filter recognizing JUnit test class.</li>
 * <li>Optional {@link ClassPredicate}.</li>
 * </ol>
 */
public class GuavaClassPathSuite extends Suite {

	/**
	 * The <code>ClassNamePredicate</code> annotation specifies a predicate which filter the final set of all classes
	 * first just by class name.
	 * 
	 * @see Reflection
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface ClassNamePredicate {
		/**
		 * @return the predicate to fitler set of {@link ClassInfo} by class name.
		 */
		public Class<? extends Predicate<String>> value();
	}

	/**
	 * The <code>ClassPredicate</code> annotation specifies a class which filter the final set
	 * {@link ClassPath#getTopLevelClasses()}. The result class are run.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	public @interface ClassPredicate {
		/**
		 * @return the predicate to fitler set of {@link ClassInfo}.
		 */
		public Class<? extends Predicate<Class<?>>> value();
	}

	private static Class<?>[] findClasses(Class<?> klass) throws InitializationError {
		// all classes
		Iterable<ClassInfo> classInfos;
		try {
			classInfos = ClassPath.from(klass.getClassLoader()).getTopLevelClasses();
		} catch (IOException e) {
			throw new InitializationError(Collections.<Throwable> singletonList(e));
		}

		// filter by class name
		ClassNamePredicate classNamePredicateAnnotation = klass.getAnnotation(ClassNamePredicate.class);
		classInfos = filterByClassNamePredicate(classInfos, classNamePredicateAnnotation);

		// transform to classes
		Iterable<Class<?>> classes = Iterables.transform(classInfos, new ClassFunction());

		// filter test classes
		classes = Iterables.filter(classes, new TestPredicate());

		// final filtering
		ClassPredicate classPredicateAnnotation = klass.getAnnotation(ClassPredicate.class);
		classes = filterByClassPredicate(classes, classPredicateAnnotation);

		Class<?>[] result = Iterables.toArray(classes, Class.class);
		return result;
	}

	/**
	 * Filter by {@link ClassNamePredicate}.
	 */
	private static Iterable<ClassInfo> filterByClassNamePredicate(Iterable<ClassInfo> classes, ClassNamePredicate predicateAnnotation) throws InitializationError {
		if (predicateAnnotation == null) {
			return classes;
		}

		Class<? extends Predicate<String>> predicateClass = predicateAnnotation.value();
		if (predicateClass == null) {
			return classes;
		}

		Predicate<String> predicate;
		try {
			predicate = predicateClass.newInstance();
		} catch (InstantiationException e) {
			throw new InitializationError(Collections.<Throwable> singletonList(e));
		} catch (IllegalAccessException e) {
			throw new InitializationError(Collections.<Throwable> singletonList(e));
		}

		return Iterables.filter(classes, Predicates.compose(predicate, new ClassNameFunction()));
	}

	/**
	 * Filter by {@link ClassPredicate}.
	 */
	private static Iterable<Class<?>> filterByClassPredicate(Iterable<Class<?>> classes, ClassPredicate predicateAnnotation) throws InitializationError {
		if (predicateAnnotation == null) {
			return classes;
		}

		Class<? extends Predicate<Class<?>>> predicateClass = predicateAnnotation.value();
		if (predicateClass == null) {
			return classes;
		}

		Predicate<Class<?>> predicate;
		try {
			predicate = predicateClass.newInstance();
		} catch (InstantiationException e) {
			throw new InitializationError(Collections.<Throwable> singletonList(e));
		} catch (IllegalAccessException e) {
			throw new InitializationError(Collections.<Throwable> singletonList(e));
		}

		return Iterables.filter(classes, predicate);
	}

	/**
	 * Called reflectively on classes annotated with <code>@RunWith(Suite.class)</code>
	 * 
	 * @param klass
	 *            the root class
	 * @param builder
	 *            builds runners for classes in the suite
	 * @throws InitializationError
	 */
	public GuavaClassPathSuite(Class<?> klass, RunnerBuilder builder) throws InitializationError {
		super(builder, klass, findClasses(klass));
	}

	/**
	 * A function to get class name from Guava {@link ClassInfo}.
	 */
	public static class ClassNameFunction implements Function<ClassInfo, String> {
		@Override
		public String apply(ClassInfo classInfo) {
			return classInfo == null ? null : classInfo.getName();
		}
	}

	/**
	 * A function to get {@link Class} from Guava {@link ClassInfo}.
	 */
	public static class ClassFunction implements Function<ClassInfo, Class<?>> {
		@Override
		public Class<?> apply(ClassInfo classInfo) {
			return classInfo == null ? null : classInfo.load();
		}
	}

	/**
	 * A predicate to filter out non-JUnit test classes.
	 */
	public static class TestPredicate implements Predicate<Class<?>> {
		@Override
		public boolean apply(Class<?> clazz) {
			if (clazz == null) {
				return false;
			}

			if ((clazz.getModifiers() & Modifier.ABSTRACT) != 0) {
				return false; // abstract class
			}

			if (TestCase.class.isAssignableFrom(clazz)) {
				return true; // old jUnit 3 test
			}

			try {
				for (Method method : clazz.getMethods()) {
					if (method.getAnnotation(Test.class) != null) {
						return true; // junit 4 test
					}
				}
			} catch (NoClassDefFoundError ignore) {
			}

			return false;
		}
	}

}
