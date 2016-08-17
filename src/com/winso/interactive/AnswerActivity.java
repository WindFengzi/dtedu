package com.winso.interactive;

import java.util.Vector;

import com.winso.interactive.R;
import com.winso.interactive.app.ActionSelectAdapter.ViewHolderAction;
import com.winso.comm_library.CItem;
import com.winso.comm_library.FileUtils;
import com.winso.comm_library.icedb.SelectHelp;
import com.winso.interactive.app.ActionSelectAdapter;
import com.winso.interactive.app.AppContext;
import com.winso.interactive.app.UIHelper;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class AnswerActivity extends BaseViewSaveActivity {

	ListViewUtility listUtil = new ListViewUtility();
	// answer 选择列表
	private ListView answerListView;
	ActionSelectAdapter mAnswerAdapter;
	boolean mbRefreshing = false; // 是否已经在加载
	ArrayAdapter<CItem> mAdapterCheckItem = null;

	//
	boolean bRobOK = false; // 是否已经抢答到
	private SelectHelp mHelpChoies = new SelectHelp(); // 选择的答案
	Button mBtAnswer;
	// 0即问即答、1抢答、2挑人、3挑组
	int iAskType = -1;
	String sAskType = "单选题";
	int iQuestionType = 1;

	String sAnswer = ""; // 答案
	String sQuestion = "";
	String sAnswerArray[];
	boolean bYesNoRight;

	String sAskID = "0";
	TextView txTitle;  // 标题栏

	EditText edAnswerText; // 答案框
	EditText edQuestion;   // 问题框

	String sPicURL = "";
	ImageView imageView = null; // 图片答案框

	Button mBtRob = null;
	String sSaveText = "";
	
	int m_iListHeight = 120;
	

	String sDrawInfo = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_answer_v2);

		myDownloadCallback = new MyCallBack_download(this);

		edQuestion = (EditText) findViewById(R.id.ed_question);
		edAnswerText = (EditText) findViewById(R.id.ed_answer_text);
		imageView = (ImageView) findViewById(R.id.image_pic);

		txTitle = (TextView) findViewById(R.id.tx_header_title);
		txTitle.setText("问答");

		// 放大图片
		imageView.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (!sAskType.equals("图片比对")) {
					return;
				}
				if (msDownloadFilePath.length() <= 0){
					return;					
				}

				Intent intent = new Intent(AnswerActivity.this,
						ViewPicDrawActivity.class);
				intent.putExtra("pic_path", msDownloadFilePath);
				intent.putExtra("draw_info", sDrawInfo);
				AnswerActivity.this.startActivityForResult(intent,
						UIHelper.INTENT_DRAW_PIC);

				// UIHelper.openViewPic(v.getContext(), msDownloadFilePath);
			}
		}

		);

		// 提交按扭
		fbSave = (Button) findViewById(R.id.btn_save);
		getRightChangeBtn(RIGHT_SUBMIT);
		fbSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// 检查是否选择了内容
				if (iAskType < 0) {
					UIHelper.showMsg(AnswerActivity.this, "", "请刷新题目");
					return;
				}
				if (iAskType == AppContext.ASK_TYPE_RUSH) {
					if (bRobOK == false) {
						UIHelper.showMsg(AnswerActivity.this, "",
								"抢答成功的人才能回答问题");
						return;
					}
				}

				if (iQuestionType == AppContext.QUESTION_TYPE_CONTENT) {
					if (edAnswerText.getText().toString().length() <= 0) {
						UIHelper.showMsg(AnswerActivity.this, "", "请填写回答内容");
						return;
					}

				} else if (iQuestionType == AppContext.QUESTION_TYPE_PIC) {
					if (edAnswerText.getText().toString().length() <= 0) {
						// UIHelper.showMsg(AnswerActivity.this, "", "请填写回答内容");
						// return;
					}

				} else {

					boolean bFind = false;
					int iSizeAnswer[] = new int[7];
					int i = 0;

					for (i = 0; i < 7; i++) {
						iSizeAnswer[i] = 0;
					}

					for (i = 0; i < mAnswerAdapter.isSelected.size(); i++) {
						if (mAnswerAdapter.isSelected.get(i)) {
							bFind = true;
						} else {
							iSizeAnswer[i] = 0;
						}
					}

					if (bFind == false) {
						UIHelper.showMsg(AnswerActivity.this, "", "请选择答案");
						return;
					}

				}

				if (appContext.mIsSelectPeople == false) {
					UIHelper.showMsg(AnswerActivity.this, "", "你无权答题");
					return;
				}

				startSaveThread();
			}
		});

		// 返回按钮
		Button mBtBack = (Button) findViewById(R.id.btn_back);
		mBtBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		// 抢答按钮
		mBtRob = (Button) findViewById(R.id.bt_rob_ask);
		mBtRob.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (bRobOK) {
					UIHelper.showMsg(AnswerActivity.this, "", "已经抢答成功,请先答题");

					return;

				}

				processRob();
			}
		});
		
		// 填写答案按钮
		mBtAnswer = (Button) findViewById(R.id.bt_write_answer);
		mBtAnswer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(v.getContext(),
						WriteAnswerActivity.class);
				String sContent = getEditText(R.id.ed_answer_text);
				intent.putExtra("answer_content", sContent);
				startActivityForResult(intent, UIHelper.INTENT_WRITE_ANSWER);
			}
		});
		// WriteAnswerActivity

		EditText ed = (EditText) findViewById(R.id.ed_question);
		ed.setKeyListener(null);

		//
		answerListView = (ListView) findViewById(R.id.pl_list_action);
		answerListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

			}
		});

		// 刷新按钮
		Button mBtRefresh = (Button) findViewById(R.id.bt_refresh_question);
		mBtRefresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startLoadDataThread();
			}
		});

		initListView();

		startLoadDataThread();
	}

	// 处理抢答
	public void processRob() {
		if (iAskType != AppContext.ASK_TYPE_RUSH) {
			return;
		}

		bRobOK = appContext.robAsk(sAskID);

		if (bRobOK) {
			UIHelper.showMsg(AnswerActivity.this, "", "抢答成功，请提交答案");
		} else {
			UIHelper.showMsg(AnswerActivity.this, "", "抢答失败");
		}

	}

	public boolean isExistAnswer(int idx) {
		String SA = "A";
		if (idx == 0) {
			SA = "A";
		} else if (idx == 1) {
			SA = "B";
		} else if (idx == 2) {
			SA = "C";
		} else if (idx == 3) {
			SA = "D";
		} else if (idx == 4) {
			SA = "E";
		} else if (idx == 5) {
			SA = "F";
		}

		for (int i = 0; i < sAnswerArray.length; i++) {
			if (sAnswerArray[i].compareToIgnoreCase(SA) == 0) {
				return true;
			}
		}

		return false;
	}

	public String getAskType() {
		// //0即问即答、1抢答、2挑人、3挑组

		if (iAskType == AppContext.ASK_TYPE_IM) {
			return "即问即答";
		}
		if (iAskType == AppContext.ASK_TYPE_RUSH) {
			return "抢答";
		}
		if (iAskType == AppContext.ASK_TYPE_SELECT_PEOPLE) {
			return "挑人";
		}
		if (iAskType == AppContext.ASK_TYPE_GROUP) {
			return "挑组";
		}
		if (iAskType == 4) {
			return "即问即答";
		}

		return "未知";
	}

	private void setTitle(String sInfo) {
		String sNew = sInfo;

		if (appContext.mIsSelectPeople) {
			sNew = "请答题-";
			sNew += sInfo;
		}
		else
		{
			sNew = "你无权答题-";
			sNew += sInfo;
		}

		if (sInfo.equals("图片比对")) {
			txTitle.setText(sNew);
			
			
			
			//answerListView
		} else {
			txTitle.setText(getAskType() + "(" + sNew + ")");
		}
	}

	@Override
	protected void loadData() {
		// TODO Auto-generated method stub
		mHelpGetData = appContext.getLastAsk();
	}

	// 获取选择结果
	private void getAnswerSelect() {
		if (mHelpGetData == null)
			return;

		if (mHelpGetData.size() <= 0)
			return;

		// mAnswerAdapter = new ActionSelectAdapter(this, answerListView);

		mHelpChoies.reset();
		mHelpChoies.addField("title_id");
		mHelpChoies.addField("title_left");

		if (iQuestionType == AppContext.QUESTION_TYPE_PIC) {
			return;
		}
		// 判断题
		// if ( iQuestionType == AppContext.QUESTION_TYPE_YESNO )
		// {
		//
		// vs.add("A");
		// vs.add(mHelpGetData.valueStringByName(0, "ChoiceA"));
		// mAnswerAdapter.updateData(mHelpChoies);
		// return;
		// }
		// Vector<String> vs = new Vector<String>();

		int iSize = mHelpGetData.valueIntByName(0, "ChoiceNum");

		if (iQuestionType == AppContext.QUESTION_TYPE_YESNO) {
			iSize = 1;
			Vector<String> vs = new Vector<String>();

			vs.add("A");
			vs.add("正确");
			mHelpChoies.addValue(vs);

			Vector<String> vs2 = new Vector<String>();
			vs2.add("B");
			vs2.add("错误");
			mHelpChoies.addValue(vs2);
			mAnswerAdapter.updateData(mHelpChoies);
			return;
		}

		if (iSize <= 0) {
			mAnswerAdapter.updateData(mHelpChoies);
			return;
		}
		if (iSize > 0) {
			Vector<String> vs = new Vector<String>();

			if (iQuestionType == AppContext.QUESTION_TYPE_YESNO) {

				vs.add("A");
				vs.add("");
				// setEditText(R.id.ed_question,"");

			} else {
				vs.add("A");
				vs.add(mHelpGetData.valueStringByName(0, "ChoiceA"));
			}

			mHelpChoies.addValue(vs);
		}
		if (iSize > 1) {
			Vector<String> vs = new Vector<String>();
			vs.add("B");
			vs.add(mHelpGetData.valueStringByName(0, "ChoiceB"));
			mHelpChoies.addValue(vs);
		}
		if (iSize > 2) {
			Vector<String> vs = new Vector<String>();
			vs.add("C");
			vs.add(mHelpGetData.valueStringByName(0, "ChoiceC"));
			mHelpChoies.addValue(vs);
		}
		if (iSize > 3) {
			Vector<String> vs = new Vector<String>();
			vs.add("D");
			vs.add(mHelpGetData.valueStringByName(0, "ChoiceD"));
			mHelpChoies.addValue(vs);
		}
		if (iSize > 4) {
			Vector<String> vs = new Vector<String>();
			vs.add("E");
			vs.add(mHelpGetData.valueStringByName(0, "ChoiceE"));
			mHelpChoies.addValue(vs);
		}
		if (iSize > 5) {
			Vector<String> vs = new Vector<String>();
			vs.add("F");
			vs.add(mHelpGetData.valueStringByName(0, "ChoiceF"));
			mHelpChoies.addValue(vs);
		}

		mAnswerAdapter.updateData(mHelpChoies);
		// listUtil.setListViewHeightBasedOnChildren(answerListView);

	}

	@Override
	protected void processLoadData() {
		// TODO Auto-generated method stub
		if (mHelpGetData == null)
			return;

		if (mHelpGetData.size() <= 0)
			return;

		sAskID = mHelpGetData.valueStringByName(0, "ask_id");

		iQuestionType = mHelpGetData.valueIntByName(0, "questionType");
		iAskType = mHelpGetData.valueIntByName(0, "ask_type");
		sPicURL = mHelpGetData.valueStringByName(0, "pic_url");

		sAnswer = mHelpGetData.valueStringByName(0, "answer");
		sAnswerArray = sAnswer.split(",", -1);

		sQuestion = mHelpGetData.valueStringByName(0, "question");

		if (iQuestionType == AppContext.QUESTION_TYPE_SINGLE) {
			sAskType = "单选题";
			answerListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			answerListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {

					if (sAskType.equals("单选题"))
						mAnswerAdapter.processUncheck(position);

				}
			});
			getAnswerSelect();
		} else if (iQuestionType == AppContext.QUESTION_TYPE_MULT) {
			sAskType = "多选题";
			answerListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
			answerListView.setOnItemClickListener(new OnItemClickListener() {
				@SuppressWarnings("static-access")
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					ViewHolderAction vHollder = (ViewHolderAction) view
							.getTag();
					// 在每次获取点击的item时将对于的checkbox状态改变，同时修改map的值。
					vHollder.cBox.toggle();
					mAnswerAdapter.isSelected.put(position,
							vHollder.cBox.isChecked());
				}
			});

			getAnswerSelect();
		} else if (iQuestionType == AppContext.QUESTION_TYPE_YESNO) {
			sAskType = "判断题";
			answerListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			answerListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {

					if (sAskType.equals("判断题"))
						mAnswerAdapter.processUncheck(position);

				}
			});

			getAnswerSelect();
		} else if (iQuestionType == AppContext.QUESTION_TYPE_CONTENT) {
			sAskType = "问答题";
			getAnswerSelect();
		} else if (iQuestionType == AppContext.QUESTION_TYPE_PIC) {
			sAskType = "图片比对";
			getAnswerSelect();
		} else {
			sAskType = "问答题";
			getAnswerSelect();
		}

		setTitle(sAskType);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;// 宽度
		int height = dm.heightPixels;// 高度

		setEditText(R.id.ed_question, sQuestion);

		if (iQuestionType != AppContext.QUESTION_TYPE_CONTENT) {

			if (iQuestionType != AppContext.QUESTION_TYPE_PIC) {
				mBtAnswer.setVisibility(View.INVISIBLE);

				edAnswerText.setVisibility(View.INVISIBLE);
				edAnswerText.getLayoutParams().height = 0;
			}

			if (sPicURL.length() <= 0) {
				answerListView.setVisibility(View.VISIBLE);
				answerListView.getLayoutParams().height = height - 100;

				imageView.setVisibility(View.INVISIBLE);
				imageView.getLayoutParams().height = 0;
			} else {
				int iH = edAnswerText.getLayoutParams().height;

				answerListView.setVisibility(View.VISIBLE);
				if (iH <= height - 100 - 240
						- edQuestion.getLayout().getHeight()) {
					// answerListView.getLayoutParams().height += 14;
				} else {
					answerListView.getLayoutParams().height = height - 100
							- 240 - edQuestion.getLayout().getHeight();
				}

				imageView.setVisibility(View.VISIBLE);
				imageView.getLayoutParams().height = height
						- answerListView.getLayoutParams().height - 100;
				imageView.getLayoutParams().width = width;
			}

		} else {
			mBtAnswer.setVisibility(View.VISIBLE);

			answerListView.setVisibility(View.INVISIBLE);
			answerListView.getLayoutParams().height = 0;

			if (sPicURL.length() <= 0) {
				edAnswerText.setVisibility(View.VISIBLE);
				edAnswerText.getLayoutParams().height = height
						- edQuestion.getLayout().getHeight() - 100;

				imageView.setVisibility(View.INVISIBLE);
				imageView.getLayoutParams().height = 0;
			} else {
				edAnswerText.setVisibility(View.VISIBLE);
				edAnswerText.getLayoutParams().height = height
						- edQuestion.getLayout().getHeight() - 100 - 240;

				imageView.setVisibility(View.VISIBLE);
				imageView.getLayoutParams().height = 240;
				imageView.getLayoutParams().width = width;
			}
		}
		mAnswerAdapter.notifyDataSetChanged();

		if (sPicURL.length() > 0) {

			String sLoacalFile = appContext.getPictureDirectory()
					+ FileUtils.getFileName(sPicURL);
			msDownloadFilePath = sLoacalFile;

			if (!FileUtils.checkFileExists(sLoacalFile)) {
				downloadFile(sPicURL, appContext.getPictureDirectory());
			} else {
				imageView.setImageURI(Uri.parse(msDownloadFilePath));
			}

		}

		// 如果是抢答
		if (iAskType == AppContext.ASK_TYPE_RUSH) {
			mBtRob.setVisibility(View.VISIBLE);
		} else {

			mBtRob.setVisibility(View.INVISIBLE);
		}

		// setButtonText(R.id.bt_select_teacher, sAskType);

		sDrawInfo = "";
	}

	@Override
	protected void processSaveData() {
		// TODO Auto-generated method stub

		int iSizeAnswer[] = new int[7];
		int i = 0;

		for (i = 0; i < 7; i++) {
			iSizeAnswer[i] = 0;
		}
		//
		int iRightSize = 0;
		int bRight = 1;

		if (iQuestionType == AppContext.QUESTION_TYPE_YESNO
				|| iQuestionType == AppContext.QUESTION_TYPE_SINGLE
				|| iQuestionType == AppContext.QUESTION_TYPE_MULT) {
			for (i = 0; i < mAnswerAdapter.isSelected.size(); i++) {
				if (mAnswerAdapter.isSelected.get(i)) {
					iSizeAnswer[i] = 1;

					if (!isExistAnswer(i)) {
						bRight = 0;
					}

					iRightSize++;
				} else {
					iSizeAnswer[i] = 0;
				}
			}
		}

		String sGetAnswerContent = getEditText(R.id.ed_answer_text);

		if (iRightSize != sAnswerArray.length) {
			bRight = 0;
		}

		if (iQuestionType == AppContext.QUESTION_TYPE_YESNO) {

			if (mHelpGetData.valueIntByName(0, "answer") == 1) {
				if (iSizeAnswer[0] == 1)
					bRight = 1;
				else
					bRight = 0;
			} else {
				if (iSizeAnswer[1] == 1)
					bRight = 1;
				else
					bRight = 0;

			}
		}
		if (iQuestionType == AppContext.QUESTION_TYPE_CONTENT
				|| iQuestionType == AppContext.QUESTION_TYPE_PIC) {
			bRight = 999;
		}
		// 判断是否是正确答案

		if (appContext.insertAnswer(sAskID, iSizeAnswer[0], iSizeAnswer[1],
				iSizeAnswer[2], iSizeAnswer[3], iSizeAnswer[4], iSizeAnswer[5],
				iSizeAnswer[6], sGetAnswerContent, bRight, sDrawInfo) == 1) {
			sSaveText = "答题成功";
		} else {
			sSaveText = "本题已答";
		}

		bRobOK = false;

		appContext.mIsSelectPeople = false;
	}

	@Override
	protected void processAfterSaveData() {
		// TODO Auto-generated method stub

		UIHelper.showMsg(this, "", sSaveText);
	}

	// /List view 相关
	protected void initListView() {
		mAnswerAdapter = new ActionSelectAdapter(this, answerListView);
		answerListView.setAdapter(mAnswerAdapter);
		answerListView.setItemsCanFocus(false);
		answerListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	}

	public class ListViewUtility {
		public void setListViewHeightBasedOnChildren(ListView listView) {

			if (mAnswerAdapter == null) {
				// pre-condition
				return;
			}

			int totalHeight = 0;
			for (int i = 0; i < mAnswerAdapter.getCount(); i++) {
				View listItem = mAnswerAdapter.getView(i, null, listView);
				listItem.measure(0, 0);
				totalHeight += listItem.getMeasuredHeight();
			}

			ViewGroup.LayoutParams params = listView.getLayoutParams();
			params.height = totalHeight
					+ (listView.getDividerHeight() * (mAnswerAdapter.getCount() - 1));
			listView.setLayoutParams(params);
		}
	}

	// 处理返回结果
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode != RESULT_OK)
			return;

		if (requestCode == UIHelper.INTENT_WRITE_ANSWER) {

			String sContent = data.getStringExtra("answer_content");
			setEditText(R.id.ed_answer_text, sContent);

			return;
		}
		//
		else if (requestCode == UIHelper.INTENT_DRAW_PIC) {

			sDrawInfo = data.getStringExtra("draw_info");
			// setEditText(R.id.ed_answer_text, sContent);

			// sDrawInfo
			System.out.println("sDrawInfo--->" + sDrawInfo);
			return;
		}
	}

	@Override
	protected void processDownload(boolean bOK) {
		// TODO Auto-generated method stub
		if (!isDownloadOK()) {
			Toast.makeText(getApplicationContext(), "下载失败", Toast.LENGTH_SHORT)
					.show();

		} else {

			imageView.setImageURI(Uri.parse(msDownloadFilePath));

		}
	}

}
