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

import com.google.appengine.api.xmpp.JID;
import com.google.appengine.api.xmpp.Message;
import com.google.appengine.api.xmpp.MessageBuilder;
import com.google.appengine.api.xmpp.SendResponse;
import com.google.appengine.api.xmpp.XMPPService;
import com.google.appengine.api.xmpp.XMPPServiceFactory;

import java.util.logging.Logger;

/**
 * {@link TestRunListener} that sends a message via XMPP when the
 * {@link TestRun} is finished.
 *
 * @author Max Ross <max.ross@gmail.com>
 */
public class XMPPTestRunListener implements TestRunListener {

  private final Logger logger = Logger.getLogger(getClass().getName());
  private final JID jid;

  public XMPPTestRunListener(JID jid) {
    this.jid = jid;
  }

  public void onCompletion(String statusURL, long runId) {
    String msgBody = buildMessageBody(statusURL, runId);
    Message msg = new MessageBuilder().withRecipientJids(jid).withBody(msgBody).build();
    XMPPService xmpp = XMPPServiceFactory.getXMPPService();
    if (xmpp.getPresence(jid).isAvailable()) {
      SendResponse status = xmpp.sendMessage(msg);
      SendResponse.Status sendStatus = status.getStatusMap().get(jid);
      if (sendStatus != SendResponse.Status.SUCCESS) {
        logger.warning("xmpp send to " + jid + " failed with status " + sendStatus);
      }
    } else {
      logger.warning("Could not send xmpp notification to " + jid + " because they are not available.");
    }
  }

  private String buildMessageBody(String statusURL, long runId) {
    return String.format("Test run %d is complete.  Please visit %s to see your results.",
                         runId, statusURL);
  }
}
