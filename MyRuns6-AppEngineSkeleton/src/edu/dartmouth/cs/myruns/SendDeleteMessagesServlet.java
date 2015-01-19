/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.dartmouth.cs.myruns;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet that adds a new message to all registered devices.
 * <p>
 * This servlet is used just by the browser (i.e., not device).
 */
@SuppressWarnings("serial")
public class SendDeleteMessagesServlet extends BaseServlet {

	private static final int MAX_RETRY = 5;

	/**
	 * Processes the request to add a new message.
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {
		List<String> devices = Datastore.getDevices();
		logger.info("device size: " + devices.size());
		String status = "";
		if (devices.isEmpty()) {
			status = "Message ignored as there is no device registered!";
		} else {
			// Get id from the HttpServletRequest, send the delete message using GCM. 
			// Delete the related entry in datastore. 
			// Your Code.
			
			String id = req.getParameter("id");
			Message message = new Message.Builder().timeToLive(30)
					.delayWhileIdle(true).addData("message", "delete:"+id)
					.build();
			
			for (int i = 0; i < devices.size(); i++) {
				
				String device = devices.get(i);
				// Have to hard-coding the API key when creating the Sender
				Sender sender = new Sender("AIzaSyCv-fsS_uwgiKuSOut5piFPRYOXs0p9xMs");
				
				
				Datastore.deleteHistoryEntry(device, id);
				
				// Send the message to device, at most retrying MAX_RETRY times
				sender.send(message, device, MAX_RETRY);
				
			}

		}

		logger.info("SendDelete status: " + status);
		// Redirect the page to myruns_lab6_appengine. 
		// Your Code.
		resp.sendRedirect("/myruns_lab6_appengine");
		
	}

}
