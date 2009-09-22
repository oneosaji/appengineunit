package com.sortedunderbelly.appengineunit.model;

import java.util.Date;

/**
 * @author Max Ross <maxr@google.com>
 */
public final class Run {

  private final long id;
  private final Class<?> testRunnerClass;
  private final Date created;
  private long numTests;


  public Run(long id, Class<?> testRunnerClass, Date created) {
    this.id = id;
    this.testRunnerClass = testRunnerClass;
    this.created = created;
  }

  public long getId() {
    return id;
  }

  public long getNumTests() {
    return numTests;
  }

  public void setNumTests(long numTests) {
    this.numTests = numTests;
  }

  public Class<?> getTestRunnerClass() {
    return testRunnerClass;
  }

  public Date getCreated() {
    return created;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Run run = (Run) o;

    if (id != run.id) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return (int) (id ^ (id >>> 32));
  }
}
