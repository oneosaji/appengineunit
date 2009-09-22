package com.sortedunderbelly.appengineunit.spi;

import com.sortedunderbelly.appengineunit.model.Test;
import com.sortedunderbelly.appengineunit.model.TestResult;

/**
 * @author Max Ross <maxr@google.com>
 */
public interface TestHarness {

  TestResult runTest(TestHarnessConfig config, Test test);
}
