package com.sortedunderbelly.appengineunit.model;

import com.google.appengine.api.datastore.Key;

/**
 * @author Max Ross <maxr@google.com>
 */
public class Failure {

  private Key key;
  private final long runId;
  private final String failureMsg;

  public Failure(long runId, String failureMsg) {
    this(null, runId, failureMsg);
  }

  public Failure(Key key, long runId, String failureMsg) {
    this.key = key;
    this.runId = runId;
    this.failureMsg = failureMsg;
  }

  public Key getKey() {
    return key;
  }

  public void setKey(Key key) {
    this.key = key;
  }

  public String getFailureMsg() {
    return failureMsg;
  }

  public long getRunId() {
    return runId;
  }
}
