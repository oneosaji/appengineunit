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
package com.sortedunderbelly.appengineunit.spi;

import com.google.appengine.api.mail.MailService;
import com.google.appengine.api.mail.MailServiceFactory;

import java.io.IOException;
import java.util.List;

/**
 * {@link TestRunListener} that sends emails when the {@link TestRun} is
 * finished.
 *
 * @author Max Ross <max.ross@gmail.com>
 */
public class EmailTestRunListener implements TestRunListener {

  private final List<String> to;
  private final List<String> cc;
  private final List<String> bcc;
  private final String sender;

  public EmailTestRunListener(List<String> to, List<String> cc, List<String> bcc, String sender) {
    this.to = to;
    this.cc = cc;
    this.bcc = bcc;
    this.sender = sender;
  }

  public void onCompletion(String statusURL, long runId) {
    MailService.Message msg = buildCompletionMessage(statusURL, runId);
    try {
      MailServiceFactory.getMailService().send(msg);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private MailService.Message buildCompletionMessage(String statusURL, long runId) {
    MailService.Message msg = new MailService.Message();
    msg.setTo(to);
    msg.setCc(cc);
    msg.setBcc(bcc);
    msg.setSender(sender);
    msg.setSubject("Test run " + runId + " is complete.");
    msg.setTextBody("Please visit " + statusURL + " to see your results.");
    return msg;
  }
}
