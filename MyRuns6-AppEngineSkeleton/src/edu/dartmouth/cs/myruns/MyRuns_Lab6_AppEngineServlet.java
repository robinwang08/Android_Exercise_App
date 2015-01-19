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

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.dartmouth.cs.myruns.gae.HistoryEntry;

/**
 * Servlet that adds display number of devices and button to send a message.
 * <p>
 * This servlet is used just by the browser (i.e., not device) and contains the
 * main page of the demo app.
 */
@SuppressWarnings("serial")
// MyRuns_Lab6_AppEngineServlet
public class MyRuns_Lab6_AppEngineServlet extends BaseServlet {
	// public class HomeServlet extends BaseServlet {

	static final String ATTRIBUTE_STATUS = "status";
	// Time format to display dateTime
	public static final String DATE_FORMAT = "H:mm:ss MMM d yyyy";
	public static final String DISTANCE_FORMAT = "#.##";
	public static final String MINUTES_FORMAT = "%d mins";
	public static final String SECONDS_FORMAT = "%d secs";
	
	public static final double KM2MILE_RATIO = 1.609344;
	
	public static final DecimalFormat decimalFormat = new DecimalFormat("#.##");
	
	/**
	 * Displays the existing messages and offer the option to send a new one.
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();
		out.print("<html><body>");
		out.print("<head>");
		out.print("  <title>My Runs</title>");
		out.print("  <link rel='icon' href='favicon.png'/>");
		out.print("</head>");

		out.print("<h2>Exercise Entries for your devide:</h2>");

		String status = (String) req.getAttribute(ATTRIBUTE_STATUS);
		if (status != null) {
			out.print(status);
		}
		out.print("<table border=\"1\">");
		out.print("<tr>");
		out.print("<td>id</td>");
		
		out.print("<td>inputType</td>");
		out.print("<td>activityType</td>");
		out.print("<td>dateTime</td>");
		out.print("<td>duration</td>");
		out.print("<td>distance</td>");
		out.print("<td>avgSpeed</td>");
		out.print("<td>calorie</td>");
		out.print("<td>climb</td>");
		out.print("<td>heartrate</td>");
		out.print("<td>comment</td>");
		out.print("<td/>");
		out.print("</tr>");
		List<HistoryEntry> currentData = Datastore.getHistoryEntry(null);
		for (HistoryEntry entry : currentData) {
			out.print("<tr>");
			out.print("<td>"+entry.id+"</td>");
			out.print("<td>"+entry.inputType+"</td>");
			out.print("<td>"+entry.activityType+"</td>");
			// Display the long value time to human-readable format. 
			
			String dateString = parseTime(entry.dateTime);
			out.print("<td>"+ dateString +"</td>");
			
			// Display the duration to human-readable format.
			String durationString = parseDuration(entry.duration);		
			out.print("<td>" + durationString + "</td>");

			// Display the distance to human-readable format.
			out.print("<td>"+ decimalFormat.format(entry.distance / (1000) / KM2MILE_RATIO) + " Miles" + "</td>");
			
			
			out.print("<td>"+entry.avgSpeed+"</td>");
			out.print("<td>"+entry.calorie+"</td>");
			out.print("<td>"+entry.climb+"</td>");
			out.print("<td>"+entry.heartrate+"</td>");
			out.print("<td>"+entry.comment+"</td>");
			out.print("<td>" + "<input type=\"button\" onclick=\"location.href='sendDelete?id="+entry.id+"'\" value=\"Delete\">" + "</td>");
			out.print("</tr>");
		}
		out.print("</table>");
		out.print("</body></html>");
		resp.setStatus(HttpServletResponse.SC_OK);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		doGet(req, resp);
	}

	private String parseDuration(int durationInSeconds) {
		return durationInSeconds > 60 ? String.format(MINUTES_FORMAT,
				durationInSeconds / 60) : String.format(SECONDS_FORMAT,
				durationInSeconds);
	}
	
	private String parseTime(long timeInSec) {

		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(timeInSec * 1000);
		SimpleDateFormat dateFormat;
		dateFormat = new SimpleDateFormat(DATE_FORMAT);

		return dateFormat.format(calendar.getTime());
	}
	
}
