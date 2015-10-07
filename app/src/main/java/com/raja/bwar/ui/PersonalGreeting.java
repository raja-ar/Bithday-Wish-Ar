package com.raja.bwar.ui;

import com.raja.bwar.R;
import com.raja.bwar.proxy.model.MyFriend;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PersonalGreeting extends Activity {
	TextView friendsName;
	EditText messageBox;
	CheckBox autoPost;

	String facebookId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.personal_greeting);
		friendsName = (TextView) findViewById(R.id.tvshowname);
		messageBox = (EditText) findViewById(R.id.etmessage);
		autoPost = (CheckBox) findViewById(R.id.cbpost);

		friendsName.setText(getIntent().getExtras().getString("name"));
		facebookId = getIntent().getExtras().getString("fbID");
		
		MyFriend friend = Main.db.getFriendByFbID(facebookId);
		messageBox.setText(friend.getBdayMessage());
		autoPost.setChecked(friend.isAutoPost());
	}	

	public void doBackToMain(View v) {
		finish();
	}

	public void doSaveGreeting(View v) {
		// FIXME place db in proper place
		Main.db.saveMessageByFbID(facebookId, messageBox.getText().toString(),
				autoPost.isChecked());
		finish();

		Toast.makeText(this, "Personal Greeting Saved", Toast.LENGTH_SHORT)
				.show();
	}
}