package com.raja.bwar.ui;

import com.raja.bwar.R;
import com.raja.bwar.proxy.fb.MyFacebook;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AlertPage extends Activity {
	private MyFacebook fb = MyFacebook.getInstance();

	private AbsoluteLayout messageLayout;
	private TextView nameView;
	private EditText newPost;
	private Button sendMessage;

	private String fbId;
	private String name;
	private String message;
	private boolean isGlobal;
	private boolean isPosted;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alert1);

		fbId = getIntent().getStringExtra("fbID");
		name = getIntent().getStringExtra("name");
		message = getIntent().getStringExtra("message");
		isGlobal = getIntent().getBooleanExtra("isGlobal", true);
		isPosted = getIntent().getBooleanExtra("isPosted", false);

		messageLayout = (AbsoluteLayout) findViewById(R.id.abMessage);
		nameView = (TextView) findViewById(R.id.tvmessage);
		nameView.setText(name + "'s birthday");

		sendMessage = (Button) findViewById(R.id.bPMessage);
		if (isGlobal) {
			sendMessage.setText("Send Global Message");
		}
		if (isPosted) {
			sendMessage.setEnabled(false);
			sendMessage.setText("Greetings sent");
		}
	}

	public void doCreateGreeting(View v) {
		messageLayout.setVisibility(View.VISIBLE);
	}

	public void doSendPersonalGreeting(View v) {
		fb.post(fbId, message);
		Toast.makeText(this, "Greeting Send: " + message, Toast.LENGTH_SHORT)
				.show();
		finish();
	}

	public void doPost(View v) {
		newPost = (EditText) findViewById(R.id.etMessage);
		fb.post(fbId, newPost.getText().toString());
		Toast.makeText(this, "Greeting Send: " + newPost.getText().toString(),
				Toast.LENGTH_SHORT).show();
		finish();
	}
}