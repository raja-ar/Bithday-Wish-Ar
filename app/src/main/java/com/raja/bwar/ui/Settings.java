/*
 * Author - Prajwol, Saad
 */
package com.raja.bwar.ui;

import com.raja.bwar.R;
import com.raja.bwar.util.MyUtils;
import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

public class Settings extends Activity {
	private TextView time;
	private int syncHour;
	private int syncMinute;

	private static final int TIME_DIALOG_ID = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);

		time = (TextView) findViewById(R.id.textViewTime);
		syncHour = MyUtils.getAlarmHour();
		syncMinute = MyUtils.getAlarmMinute();
		time.setText(syncHour + ":" + syncMinute);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case TIME_DIALOG_ID:
			return new TimePickerDialog(this, mTimeSetListener, syncHour, syncMinute, true);
		}
		return null;
	}

	public void doSelectTime(View v) {
		showDialog(TIME_DIALOG_ID);
	}

	private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			time.setText(hourOfDay + ":" + minute);
			syncHour = hourOfDay;
			syncMinute = minute;

			Main.db.setSettings("hour", syncHour + "");
			Main.db.setSettings("minute", syncMinute + "");
			sendBroadcast(new Intent(MyUtils.ALARM_RESET));
		}
	};
}