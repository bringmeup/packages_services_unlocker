package eu.nexwell.unlocker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

public class ProximityUnlocker extends Service implements SensorEventListener {
	private static final String TAG = ProximityUnlocker.class.getSimpleName();
	private static final float SCREEN_ON_THRESHOLD = 105f;
	private static final float SCREEN_OFF_THRESHOLD = 115f;
	private SensorManager mSensorManager;
	private PowerManager mPowerManager;
	private PowerManager.WakeLock mWakelock;

	@Override
	public void onCreate() {
		Log.i(TAG, "onCreate()");

		super.onCreate();

		mPowerManager = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
		mSensorManager = (SensorManager) getApplicationContext().getSystemService(SENSOR_SERVICE);
		Sensor sensor = mSensorManager.getSensorList(Sensor.TYPE_PROXIMITY).get(0);
		mSensorManager.registerListener(this, sensor, 250000);
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy()");

		mSensorManager.unregisterListener(this);
		// ask ProximityBootReceiver for a restart
		Intent intent = new Intent("eu.nexwell.unlocker.STOPPED");
		sendBroadcast(intent);

		super.onDestroy();
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	synchronized public void onSensorChanged(SensorEvent sensorEvent) {
//		Log.i(TAG, "onSensorChanged, values[0]=" + sensorEvent.values[0]);

		if (sensorEvent.values[0] < SCREEN_ON_THRESHOLD && (mWakelock == null || !mWakelock.isHeld())) {
			Log.i(TAG, "acquire wakelock - screen will lit");
			mWakelock = mPowerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK |
					PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, TAG);
			mWakelock.acquire();
		} else if (sensorEvent.values[0] > SCREEN_OFF_THRESHOLD && mWakelock != null && mWakelock.isHeld()) {
			Log.i(TAG, "release wakelock - scrin will dim");
			mWakelock.release();
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int i) {
	}
}
