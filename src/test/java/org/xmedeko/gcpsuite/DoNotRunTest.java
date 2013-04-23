package org.xmedeko.gcpsuite;

import org.junit.Assert;
import org.junit.Test;

/**
 * Do not run this test.
 */
public class DoNotRunTest implements TestInterface {

	@Test
	public void test() {
		Assert.fail("Do not run this test " + DoNotRunTest.class);
	}

}
