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

import static eu.nexwell.unlocker.SystemProperties.read;

public class ProximityUnlocker extends Service implements SensorEventListener {
	private static final String TAG = ProximityUnlocker.class.getSimpleName();
	private static float SCREEN_HYSTERESIS = 10;
	private static float SCREEN_ON_THRESHOLD = 105f;
	private static float SCREEN_OFF_THRESHOLD = SCREEN_ON_THRESHOLD + SCREEN_HYSTERESIS;
	private SensorManager mSensorManager;
	private PowerManager mPowerManager;
	private PowerManager.WakeLock mWakelock;

	@Override
	public void onCreate() {
		Log.i(TAG, "onCreate()");

		super.onCreate();

		String distance = SystemProperties.read("persist.unlocker.distance");
		try {
			SCREEN_ON_THRESHOLD = Float.valueOf(distance);
			SCREEN_OFF_THRESHOLD = SCREEN_ON_THRESHOLD + SCREEN_ON_THRESHOLD;
		} catch (NumberFormatException e) {
			Log.i(TAG, "persist.unlocker.distance not available, using default ON=" + SCREEN_ON_THRESHOLD + "cm");
		}

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
