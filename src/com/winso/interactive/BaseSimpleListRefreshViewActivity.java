package com.winso.interactive;

import java.util.ArrayList;
import java.util.HashMap;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.winso.comm_library.icedb.SelectHelp;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 应用程序Activity的基类
 * 
 * @author ericgoo
 * @version 1.0
 * @created 2014-12-11
 */
public abstract class BaseSimpleListRefreshViewActivity extends BaseActivity {

	// 刷新
	private PullToRefreshListView mPullRefreshListView;
	private ListView actualListView;
	ArrayList<HashMap<String, Object>> mListItem;
	SimpleAdapter mListItemAdapter;
	private boolean bIsWorking = false;
	protected SelectHelp mHelpValue = new SelectHelp();

	// 定义字段的对应关系
	public String s_ID = "title_id";
	public String s_Title_Left = "title_left"; // title
	public String s_Title_Right = "title_right";

	public Object o_Picture_ID = R.drawable.list_logo; // 用于图片的编号

	// 执行刷新任务
	public class GetDataTask extends AsyncTask<Void, Void, String[]> {

		private String[] mStrings = { " " };

		@Override
		protected String[] doInBackground(Void... params) {
			try {
				bIsWorking = true;
				reLoadView();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}

			bIsWorking = false;
			return mStrings;
		}

		@Override
		protected void onPostExecute(String[] result) {
			mListItemAdapter.notifyDataSetChanged();

			// Call onRefreshComplete when the list has been refreshed.
			mPullRefreshListView.onRefreshComplete();

			updateUIView();
			bIsWorking = false;
			super.onPostExecute(result);
		}

	}

	// 加载视图
	// 加载中间的视图
	abstract protected void reLoadView();

	abstract protected void processClickView(String sTitleID,
			String sTitleLeft, String sTitleRight);

	private void updateUIView() {
		mListItem.clear();

		if ( mHelpValue == null ) return;
		
		for (int i = 0; i < mHelpValue.size(); i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();

			map.put("title_left", mHelpValue.valueStringByName(i, s_Title_Left));

			map.put("title_id", mHelpValue.valueStringByName(i, s_ID));

			map.put("title_right",
					mHelpValue.valueStringByName(i, s_Title_Right));

			map.put("btn_save_sel", o_Picture_ID);
			mListItem.add(map);
		}
	}

	protected void initListView() {
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.lt_refresh_view);
		actualListView = mPullRefreshListView.getRefreshableView();

		// Set a listener to be invoked when the list should be refreshed.
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				// Do work to refresh the list here.
				if (!bIsWorking) {
					new GetDataTask().execute();
				}
			}
		});

		actualListView.setOnItemClickListener(listListener);

		mListItem = new ArrayList<HashMap<String, Object>>();
		mListItemAdapter = new SimpleAdapter(this, mListItem,// 数据源
				R.layout.list_view_refresh_simple,// ListItem的XML实现
				// 动态数组与ImageItem对应的子项
				new String[] { "title_left", "title_id", "title_right",
						"btn_save_sel" },
				// ImageItem的XML文件里面的一个ImageView,两个TextView ID
				new int[] { R.id.title_left, R.id.title_id, R.id.title_right,
						R.id.btn_save_sel });

		// 添加并且显示
		actualListView.setAdapter(mListItemAdapter);

	}

	/*
	 * 处理函数
	 */
	private OnItemClickListener listListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// When clicked, show a toast with the TextView text
			TextView tv = (TextView) view.findViewById(R.id.title_id);
			TextView txRight = (TextView) view.findViewById(R.id.title_right);
			TextView txLeft = (TextView) view.findViewById(R.id.title_left);
		
			processClickView(tv.getText().toString(), txLeft.getText()
					.toString(), txRight.getText().toString());

		}
	};
}
