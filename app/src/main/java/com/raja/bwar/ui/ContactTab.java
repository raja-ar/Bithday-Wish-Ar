/*
 * author - Prajwol 
 * 			Saad
 */
package com.raja.bwar.ui;

import static com.raja.bwar.util.MyUtils.TAG;

import java.util.List;

import com.raja.bwar.R;
import com.raja.bwar.proxy.fb.MyFacebook;
import com.raja.bwar.proxy.model.MyFriend;
import com.raja.bwar.util.MyUtils;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class ContactTab extends ListActivity {
	private MyFacebook fb = MyFacebook.getInstance();
	private BcReceiver bcr = null;
	private MyAdapter adapter;
	private List<MyFriend> list;
	private ProgressDialog busyDialog;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		busyDialog = new ProgressDialog(this);
		busyDialog.setIndeterminate(true);
		busyDialog.setMessage("Refreshing");
	}

	private void refreshList() {
//		busyDialog.show();
		new Thread() {
			public void run() {
				list = fb.getAllFriends();
				Log.v(TAG, "contactstab- refresh called. " + list.size());
				adapter = new MyAdapter(ContactTab.this, list);
				handler.sendEmptyMessage(0);
			}
		}.start();
	}

	@Override
	protected void onResume() {
		if (bcr == null) {
			bcr = new BcReceiver();
			registerReceiver(bcr, new IntentFilter(MyUtils.FRIENDLIST_CHANGED));
		}
		refreshList();
		super.onResume();
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		MyFriend friend = (MyFriend) this.getListAdapter().getItem(position);

		Intent intent = new Intent(ContactTab.this, PersonalGreeting.class);
		intent.putExtra("fbID", friend.getFbID());
		intent.putExtra("name", friend.getName());
		startActivity(intent);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		new MenuInflater(getApplication()).inflate(R.layout.menu, menu);
		return (super.onPrepareOptionsMenu(menu));
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case R.id.globalGreeting:
			intent = new Intent(ContactTab.this, GlobalGreeting.class);
			startActivity(intent);
			break;
		case R.id.Settings:
			intent = new Intent(ContactTab.this, Settings.class);
			startActivity(intent);
		}
		return (super.onOptionsItemSelected(item));
	}

	public class BcReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			runOnUiThread(new Runnable() {
				public void run() {
					refreshList();
				}
			});
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			setListAdapter(adapter);
			//busyDialog.dismiss();
		}
	};
}