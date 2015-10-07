package com.raja.bwar.util;

import com.raja.bwar.ui.Main;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MyUtils {
	public static final String TAG = "BwAr";
	public static final String FRIENDLIST_CHANGED = "FRIENDLIST_CHANGED";
	public static final String BIRTHDAY_ALERT = "BIRTHDAY_ALERT";
	public static final String ALARM_RESET = "ALARM_RESET";

	public static String getCurrentMonth() {
		return getTodaysDate()[1];
	}

	public static String[] getCurrentWeekDays() {
		// return new String[] { "17", "21" };

		return new String[] {
				getTodaysDate()[2],
				getLastDayofCurrentWeek().length() == 1 ? "0"
						+ getLastDayofCurrentWeek() : getLastDayofCurrentWeek() };
	}

	public static String[] getTodaysDate() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String todaysDate = dateFormat.format(cal.getTime());
		String[] dates = todaysDate.split("-");
		return dates;
	}

	public static String getLastDayofCurrentWeek() {
		Calendar now = Calendar.getInstance();

		int maxDay = now.getActualMaximum(Calendar.DAY_OF_WEEK);
		int left = maxDay - now.get(Calendar.DAY_OF_WEEK);
		int lastDay = now.get(Calendar.DATE) + left;

		return String.valueOf(lastDay);
	}

	public static long getAlarmStartTimeAsLong(int hour, int minute) {
		Calendar now = new GregorianCalendar();
		now.setTimeInMillis(System.currentTimeMillis());

		Calendar alarmTime = new GregorianCalendar();
		alarmTime.setTime(new Date());
		alarmTime.set(Calendar.MINUTE, minute);
		alarmTime.set(Calendar.HOUR_OF_DAY, hour);
		alarmTime.set(Calendar.SECOND, 0);
		alarmTime.set(Calendar.MILLISECOND, 0);

		if (hour < now.get(Calendar.HOUR_OF_DAY)
				|| (hour == now.get(Calendar.HOUR_OF_DAY) && minute <= now
						.get(Calendar.MINUTE))) {
			alarmTime.add(Calendar.DAY_OF_YEAR, 1);
		}

		return alarmTime.getTimeInMillis();
	}

	public static int getAlarmHour() {
		try {
			int syncHour = Integer.valueOf(Main.db.getSettings("hour"));
			return syncHour;
		} catch (NumberFormatException e) {
			return 10;
		}
	}

	public static int getAlarmMinute() {
		try {
			int syncMinute = Integer.valueOf(Main.db.getSettings("minute"));
			return syncMinute;
		} catch (NumberFormatException e) {
			return 10;
		}
	}
}
