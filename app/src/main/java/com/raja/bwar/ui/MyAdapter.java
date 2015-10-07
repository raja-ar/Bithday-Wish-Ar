/*
 * author - Prajwol 
 * 			Saad
 */
package com.raja.bwar.ui;

import static com.raja.bwar.util.MyUtils.TAG;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

import com.raja.bwar.R;
import com.raja.bwar.proxy.model.MyFriend;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter {

	private List<MyFriend> myFriends;
	private LayoutInflater inflater;

	public MyAdapter(Context context, List<MyFriend> myFriends) {
		this.myFriends = myFriends;
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return myFriends.size();
	}

	@Override
	public Object getItem(int arg0) {
		return myFriends.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return (long) (arg0 * 1024);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;

		if (v == null) {
			v = inflater.inflate(R.layout.contact_tab, parent, false);
		}

		if (position >= myFriends.size()) {
			return v;
		}

		MyFriend friend = myFriends.get(position);
		ImageView icon_pic = (ImageView) v.findViewById(R.id.icon_pic);
		try {
			URL url = new URL(friend.getPic());
			InputStream is = (InputStream) url.getContent();
			Drawable image = Drawable.createFromStream(is, "fb");
			icon_pic.setImageDrawable(image);
		} catch (Exception e) {
			Log.v(TAG, "myadapter: " + e.getMessage());
		}

		TextView name = (TextView) v.findViewById(R.id.label);
		name.setText(friend.getName());

		TextView bday = (TextView) v.findViewById(R.id.label2);
		if (!friend.getBday().trim().equals("")) {
			bday.setText(friend.getBday());
		} else {
			bday.setText("~");
		}

		return v;
	}
}