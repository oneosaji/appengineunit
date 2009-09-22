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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Max Ross <max.ross@gmail.com>
 */
public final class Test {

  private final String id;
  private final Run run;
  private final Date startTime = new Date();
  private Date endTime;
  private Status status = Status.STARTED;
  private long numTests;
  private List<Failure> failures = new ArrayList<Failure>();

  public Test(String id, Run run) {
    this.id = id;
    this.run = run;
  }

  public String getId() {
    return id;
  }

  public Date getStartTime() {
    return startTime;
  }

  public Date getEndTime() {
    return endTime;
  }

  public void setEndTime(Date endTime) {
    this.endTime = endTime;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public Run getRun() {
    return run;
  }

  public long getNumTests() {
    return numTests;
  }

  public void setNumTests(long numTests) {
    this.numTests = numTests;
  }

  public List<Failure> getFailures() {
    return failures;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Test test = (Test) o;

    if (!id.equals(test.id)) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }
}
