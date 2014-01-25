package com.leonliu.cm;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

public class MyTabHost extends TabActivity {

	private TabHost tabhost;
	private TabWidget tabwidget;
	private FrameLayout tab_1;
	private FrameLayout tab_2;
	private FrameLayout tab_3;
	private FrameLayout tab_4;
	private FrameLayout tab_5;
	private ImageView img_1;
	private ImageView img_2;
	private ImageView img_3;
	private ImageView img_4;
	private ImageView img_5;
	private TextView txts[] = new TextView[5];
	public Handler SecurityLogHandler;

	private void getViews() {

		Intent intent_dev = new Intent();
		Bundle bundle = new Bundle();
		intent_dev.setClass(MyTabHost.this, VechicleStatus.class);
		intent_dev.putExtras(bundle);
		
		Intent intent_more = new Intent();
		Bundle bundle_more = new Bundle();
		intent_more.setClass(MyTabHost.this, MainActivity.class);
		intent_more.putExtras(bundle_more);
		
		tabhost = (TabHost) findViewById(android.R.id.tabhost);
		tabwidget = (TabWidget) findViewById(android.R.id.tabs);
		tab_1 = (FrameLayout) findViewById(R.id.FrameLayout_tab_1);
		tab_2 = (FrameLayout) findViewById(R.id.FrameLayout_tab_2);
		tab_3 = (FrameLayout) findViewById(R.id.FrameLayout_tab_3);
		tab_4 = (FrameLayout) findViewById(R.id.FrameLayout_tab_4);
		tab_5 = (FrameLayout) findViewById(R.id.FrameLayout_tab_5);
		img_1 = (ImageView) findViewById(R.id.ImageView_tab_1);
		img_2 = (ImageView) findViewById(R.id.ImageView_tab_2);
		img_3 = (ImageView) findViewById(R.id.ImageView_tab_3);
		img_4 = (ImageView) findViewById(R.id.ImageView_tab_4);
		img_5 = (ImageView) findViewById(R.id.ImageView_tab_5);
		txts[0] = (TextView)findViewById(R.id.TextView_tab_1);
		txts[1] = (TextView)findViewById(R.id.TextView_tab_2);
		txts[2] = (TextView)findViewById(R.id.TextView_tab_3);
		txts[3] = (TextView)findViewById(R.id.TextView_tab_4);
		txts[4] = (TextView)findViewById(R.id.TextView_tab_5);
		tab_1.setOnClickListener(new MyOnClickListener(0));
		tab_2.setOnClickListener(new MyOnClickListener(1));
		tab_3.setOnClickListener(new MyOnClickListener(2));
		tab_4.setOnClickListener(new MyOnClickListener(3));
		tab_5.setOnClickListener(new MyOnClickListener(4));

		tabhost.setup();
		tabhost.addTab(tabhost
				.newTabSpec("tab1")
				.setIndicator(getString(R.string.main_index_1))
				.setContent(intent_dev));
		tabhost.addTab(tabhost
				.newTabSpec("tab2")
				.setIndicator(getString(R.string.main_index_2))
				.setContent(new Intent(this, CarProducts.class)));
		tabhost.addTab(tabhost
				.newTabSpec("tab3")
				.setIndicator(getString(R.string.main_index_3))
				.setContent(new Intent(this, CarRepairShops.class)));
		tabhost.addTab(tabhost
				.newTabSpec("tab4")
				.setIndicator(getString(R.string.main_index_4))
				.setContent(new Intent(this, MyVechicle.class)));
		tabhost.addTab(tabhost
				.newTabSpec("tab5")
				.setIndicator(getString(R.string.main_index_5))
				.setContent(intent_more));
	}

	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}
		
		public void onClick(View v) {
			img_1.setImageResource(R.drawable.main_index_1_normal);
			img_2.setImageResource(R.drawable.main_index_2_normal);
			img_3.setImageResource(R.drawable.main_index_3_normal);
			img_4.setImageResource(R.drawable.main_index_4_normal);
			img_5.setImageResource(R.drawable.main_index_5_normal);
			for(TextView tv : txts) {
				tv.setTextColor(getResources().getColor(R.color.main_index_text));
			}
			switch(index) {
			case 0:
				img_1.setImageResource(R.drawable.main_index_1_pressed);
				break;
			case 1:
				img_2.setImageResource(R.drawable.main_index_2_pressed);
				break;
			case 2:
				img_3.setImageResource(R.drawable.main_index_3_pressed);
				break;
			case 3:
				img_4.setImageResource(R.drawable.main_index_4_pressed);
				break;
			case 4:
				img_5.setImageResource(R.drawable.main_index_5_pressed);
				break;
			}
			txts[index].setTextColor(getResources().getColor(R.color.main_index_text2));
			tabhost.setCurrentTab(index);
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.mytabs);

		Intent intent = this.getIntent();
		Bundle params = intent.getExtras();
		String classname = null;
		if (params != null) {
			classname = params.getString("class");
		}
		
		getViews();

		if (params == null || classname == null) {
			tab_1.performClick();
		} else {
			if (classname.equals(VechicleStatus.class.getName())) {
				tab_1.performClick();
			} else if (classname.equals(CarProducts.class.getName())) {
				tab_2.performClick();
			} else if (classname.equals(CarRepairShops.class.getName())) {
				tab_3.performClick();
			}else if (classname.equals(MyVechicle.class.getName())) {
				tab_4.performClick();
			}else if (classname.equals(MoreActivity.class.getName())) {
				tab_5.performClick();
			}
		}
	}

	public void onStart() {
		super.onStart();
	}

}
