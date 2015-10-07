package com.raja.bwar.ui;

import com.raja.bwar.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class GlobalGreeting extends Activity {
	private TextView greeting;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.global_greeting);
		greeting = (TextView) findViewById(R.id.etmessage);
		greeting.setText(Main.db.getSettings("global_message"));
	}

	public void doBackToMain(View v) {
		finish();
	}

	public void doSaveGreeting(View v) {
		Main.db.setSettings("global_message", greeting.getText().toString());
		Toast.makeText(this, "Global Greeting Saved", Toast.LENGTH_SHORT)
				.show();
		finish();
	}
}