/*
 * author: Prajwol
 */

package com.raja.bwar.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import com.raja.bwar.R;
import com.raja.bwar.proxy.fb.MyFacebook;
import com.raja.bwar.proxy.model.Filter;
import com.raja.bwar.proxy.model.MyFriend;
import com.raja.bwar.ui.Main;
import com.raja.bwar.util.MyUtils;

import static com.raja.bwar.util.MyUtils.TAG;

//ref - http://developer.android.com/resources/samples/ApiDemos/src/com/example/android/apis/app/AlarmService_Service.html
public class BdRemService extends Service {
	private final IBinder binder = new MyBinder();
	private Intent intent = null;

	private MyFacebook fb = MyFacebook.getInstance();

	private NotificationManager myNotification;

	@Override
	public void onCreate() {
		myNotification = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		showNotification();
		new Thread(mTask, "BdRemService").start();
		Log.i(TAG, "bdremservice- Service created");
	}

	@Override
	public void onDestroy() {
		myNotification.cancel(R.string.app_name);
		Toast.makeText(this, "Birthday Reminder service finished",
				Toast.LENGTH_SHORT).show();
	}

	Runnable mTask = new Runnable() {
		public void run() {
			Log.i(TAG, "bdremservice- started");

			Log.i(TAG, "bdremservice- Trace10");
			// 1. Update the local DB from facebook
			// 2. Note - local DB should be merged than replaced
			fb.reLoadAllFriends();// syncFriends B-)

			// 3. Get the friends who have birthday today
			List<MyFriend> bdayFriends = Main.db
					.getFriendsFilteredBy(Filter.DAY);

			// 2. Post the appropriate message in their wall OR/AND
			// 3. Show alert
			// 4. (2) and (3) should be based on settings like
			// - automatic post, global/personal message
			// Log.i(TAG, "bdremservice- Trace1");
			for (MyFriend friend : bdayFriends) {
				// Log.i(TAG, "bdremservice- Trace2");
				boolean isGlobal = true;
				String message = Main.db.getSettings("global_message");
				// Log.i(TAG, "bdremservice- Trace3");
				if (!friend.getBdayMessage().trim().equals("")) {
					// Log.i(TAG, "bdremservice- Trace4");
					message = friend.getBdayMessage();
					isGlobal = false;
				}
				// Log.i(TAG, "bdremservice- Trace5");
				if (message.trim().equals("")) {
					// Log.i(TAG, "bdremservice- Trace6");
					message = "Happy Birthday!";
					isGlobal = true;
				}
				// Log.i(TAG, "bdremservice- Trace7");
				if (friend.isAutoPost()) {
					// Log.i(TAG, "bdremservice- Trace8");
					fb.post(friend.getFbID(), message);
					// Log.i(TAG, "bdremservice- Tying to post to "
					// + friend.getName() + ", " + message);
				}
				Log.i(TAG, "bdremservice- Trace9");
				intent = new Intent(MyUtils.BIRTHDAY_ALERT);
				intent.putExtra("fbID", friend.getFbID());
				intent.putExtra("name", friend.getName());
				intent.putExtra("message", message);
				intent.putExtra("isGlobal", isGlobal);
				intent.putExtra("isPosted", friend.isAutoPost());
				sendBroadcast(intent);
			}

			Log.i(TAG, "bdremservice- finished");
			BdRemService.this.stopSelf();
		}
	};

	@Override
	public IBinder onBind(Intent arg0) {
		return binder;
	}

	private void showNotification() {
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, Main.class), 0);

		Notification notification = new Notification(R.drawable.user,
				"Birthday Reminder service is running", System
						.currentTimeMillis());
		notification.setLatestEventInfo(this, "Service in action",
				"Birthday Reminder service is running", contentIntent);

		myNotification.notify(R.string.app_name, notification);
	}

	class MyBinder extends Binder {
		BdRemService getService() {
			return BdRemService.this;
		}
	}
}
