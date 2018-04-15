package eu.nexwell.unlocker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ProximityBootReceiver extends BroadcastReceiver {
	static final String TAG = ProximityBootReceiver.class.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "intent=" + intent);
		context.startService(new Intent(context, ProximityUnlocker.class));
	}
}
