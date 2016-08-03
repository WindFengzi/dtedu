package com.winso.interactive.app;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.winso.comm_library.icedb.SelectHelp;
import com.winso.interactive.R;

public class LazyAdapter extends BaseAdapter {

	public static Map<Integer, Boolean> isSelected;    
	private List<Map<String, Object>> mData;    
	ListView list;
	static List<LazyAdapter.MyDat> MyViewedMeItemList;
	// =========
	LazyAdapter adapter;
	private Activity activity;

	private LayoutInflater inflater = null;
	Context context;

	public LazyAdapter(Activity a) {
		activity = a;

		context = a;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	public int getCount() {
		return 25;
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public void updateData(SelectHelp help) {
		mData.clear();
		// mData=new ArrayList<Map<String, Object>>();
		for (int i = 0; i < help.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			// map.put("img", R.drawable.icon);
			map.put("title_id", help.valueStringByName(i, "title_id"));
			map.put("title_left", help.valueStringByName(i, "title_left"));
			mData.add(map);
		}
		// 这儿定义isSelected这个map是记录每个listitem的状态，初始状态全部为false。
		isSelected = new HashMap<Integer, Boolean>();
		for (int i = 0; i < mData.size(); i++) {
			isSelected.put(i, false);
		}
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (convertView == null)
			vi = inflater.inflate(R.layout.row_single, null);

		final MyDat mydat;// = new MyDat();
		if (position >= MyViewedMeItemList.size()) {
			mydat = new MyDat();

		} else {
			mydat = MyViewedMeItemList.get(position);
		}

		mydat.cBox = (CheckBox) vi.findViewById(R.id.rajesh);
		mydat.cBox.setVisibility(View.VISIBLE);
		mydat.cBox.setText(" Position" + position);

		if (mydat.myCheckStatus) {
			// Toast.makeText(MessageInboxDelete.this, "true --->" +//
			// position,// Toast.LENGTH_SHORT).show();
			mydat.cBox.setChecked(true);
		} else {
			/*
			 * Toast.makeText(MessageInboxDelete.this, "false --->" +* position,
			 * Toast.LENGTH_SHORT).show();
			 */
			mydat.cBox.setChecked(false);
		}

		mydat.cBox.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View paramView) {
				// TODO Auto-generated method stub
				// Toast.makeText(context, "Rajesh", Toast.LENGTH_SHORT).show();

				int gridchild;
				gridchild = list.getChildCount();
				for (int j = 0; j < gridchild; j++) {
					// DrawArea_Steptwo.gridview.getChildAt(j);

					// Toast.makeText(context, "Rajesh",
					// Toast.LENGTH_SHORT).show();
					RelativeLayout gridchildlayout = (RelativeLayout) list
							.getChildAt(j);

					CheckBox tempRadioToggle = (CheckBox) gridchildlayout
							.findViewById(R.id.rajesh);

					tempRadioToggle.setChecked(false);

					MyDat tempViewholder = MyViewedMeItemList.get(j);
					tempViewholder.myCheckStatus = false;
				}

				for (int j = 0; j < MyViewedMeItemList.size(); j++) {
					MyDat tempViewholder = MyViewedMeItemList.get(j);
					tempViewholder.myCheckStatus = false;
				}
				CheckBox tempRadioToggle = (CheckBox) paramView
						.findViewById(R.id.rajesh);
				tempRadioToggle.setChecked(true);

				MyDat tempViewholder = MyViewedMeItemList.get(position);

				if (tempViewholder.myCheckStatus == false) {
					tempViewholder.myCheckStatus = true;
					// tempViewholder.deleteRadioButton.setChecked(true);

				} else {
					tempViewholder.myCheckStatus = false;
					// tempViewholder.deleteRadioButton.setChecked(false);

				}

			}
		});

		if (MyViewedMeItemList.size() <= position) {
			MyViewedMeItemList.add(mydat);
		}

		return vi;
	}

	public class MyDat {

		Boolean myCheckStatus = false;
		// CheckBox rajesh;

		public TextView title_id;
		public TextView title_left;
		public CheckBox cBox;

	}

}