package org.sortedunderbelly.appengineunit.spi;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * {@link TestRunListener} implementation that cycles through 1 or more
 * {@link TestRunListener TestRunListeners} for each event method.  The event
 * method on each contained listener will be called even if the event methods
 * on other contained listeners throw exceptions.
 *
 * @author Max Ross <maxr@google.com>
 */
public class CompoundTestRunListener implements TestRunListener {

  private final Logger logger = Logger.getLogger(getClass().getName());

  private final List<TestRunListener> listeners;

  public CompoundTestRunListener(List<TestRunListener> listeners) {
    this.listeners = listeners;
  }

  public void onCompletion(String statusURL, long runId) {
    RuntimeException thrown = null;
    for (TestRunListener trl : listeners) {
      // We're going to power through these listeners even
      // if one of them throws an exception.
      try {
        trl.onCompletion(statusURL, runId);
      } catch (RuntimeException rte) {
        logger.log(Level.SEVERE, "TestRunListener of type " + trl.getClass().getName() + " threw an exception.", rte);
        thrown = rte;
      }
    }

    // If one or more of the listeners threw an exception we'll just throw the
    // most recent one.
    if (thrown != null) {
      throw thrown;
    }
  }
}
