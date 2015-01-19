package edu.dartmouth.cs.myruns6;

import java.io.IOException;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

import edu.dartmouth.cs.myruns6.gae.ServerUtilities;

public class GCMIntentService extends GCMBaseIntentService {

	public GCMIntentService() {

		super();
	}
	/**
	 * Gets the sender ids.
	 *
	 * <p>By default, it returns the sender ids passed in the constructor, but
	 * it could be overridden to provide a dynamic sender id.
	 *
	 * @throws IllegalStateException if sender id was not set on constructor.
	 */
	@Override
	protected String[] getSenderIds(Context context) {
		String[] regId = new String[1];
		regId[0] = Globals.getSenderID(context);
		return regId;
	}

	/**
	 * Called on registration error. This is called in the context of a Service
	 * - no dialog or UI.
	 * 
	 * @param arg0
	 *            the Context
	 * @param arg1
	 *            an error message
	 */
	@Override
	protected void onError(Context arg0, String arg1) {
		String errorId = arg1;
		Log.i(Globals.TAG, "Received error: " + errorId);
	}

	/**
	 * Called when a cloud message has been received.
	 * 
	 * @param arg0
	 *            the Context
	 * @param arg1
	 *            the Intent contains the extra data
	 */
	@Override
	protected void onMessage(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub

		Context context = arg0;
		Intent intent = arg1;
		Log.i(Globals.TAG, "Received message");

		// Get message from the extras, split the message to extract id and operation. 
		// If the operation is "delete", delete the requested exercise entry.
		// Your code.

		Bundle extras = intent.getExtras();
		if (extras != null) {
			String message = (String) extras.get("message");
			String[] messages = message.split(Pattern.quote(Globals.SEPARATOR));

			//what is changetype?
			String changeType = messages[0];
			long id = Long.parseLong(messages[1]);

			if (changeType.equals("delete") ){
				//is it context or something else?
				context.getContentResolver().delete(
						Uri.parse(HistoryProvider.CONTENT_URI + "/"
								+ String.valueOf(id)), null, null);
			}
		}
	}

	/**
	 * Called when a registration token has been received.
	 * 
	 * @param arg0
	 *            the Context
	 * @param arg1
	 *            the registration id as a String
	 * @throws IOException
	 *             if registration cannot be performed
	 */
	@Override
	protected void onRegistered(Context arg0, String arg1) {
		// TODO Auto-generated method stub

		Context context = arg0;
		String registrationId = arg1;
		Log.i(Globals.TAG, "Device registered: regId = " + registrationId);
		// Use helper function in ServerUtilities to register. 
		// Your code.
		ServerUtilities.register(context, registrationId);
	}

	/**
	 * Called when the device has been unregistered.
	 * 
	 * @param arg0
	 *            the Context
	 * @param arg1
	 *            the registration id as a String
	 */
	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		// TODO Auto-generated method stub

		Context context = arg0;
		String registrationId = arg1;
		Log.i(Globals.TAG, "Device unregistered");
		if (GCMRegistrar.isRegisteredOnServer(context)) {
			ServerUtilities.unregister(context, registrationId);
		} else {
			// This callback results from the call to unregister made on
			// ServerUtilities when the registration to the server failed.
			Log.i(Globals.TAG, "Ignoring unregister callback");
		}

	}

}
