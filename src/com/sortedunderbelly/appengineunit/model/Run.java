/*
 * Copyright (C) 2009 Max Ross.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sortedunderbelly.appengineunit.model;

import java.util.Date;

/**
 * @author Max Ross <max.ross@gmail.com>
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
