package com.winso.interactive;

import java.io.File;


import com.winso.comm_library.CallbackInterface;
import com.winso.comm_library.FileUtils;
import com.winso.comm_library.icedb.DownloadFileTask;
import com.winso.comm_library.icedb.SelectHelp;
import com.winso.interactive.app.UIHelper;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.AsyncTask;

/**
 * 应用程序Activity，可以用于进来查询数据，然后有保存功能的单个数据的查询界面
 * 
 * @author ericgoo
 * @version 1.0
 * @created 2014-12-11
 */
public abstract class BaseViewSaveActivity extends BaseActivity {

	public boolean bIsNeedProgressDialog = true;
	private ProgressDialog progressDialog = null;

	public boolean bIsWorking = false;
	public SelectHelp mHelpGetData = new SelectHelp();

	
	public void startLoadDataThread()
	{
		if ( bIsWorking ) 
			return;
		
		new GetInDataTask().execute();
		
	}
	public void startSaveThread()
	{
		if ( bIsWorking ) 
			return;
		
		new SaveDataTask().execute();
		
	}
	
	// 执行查询数据任务
	public class GetInDataTask extends AsyncTask<Void, Void, String[]> {

		private String[] mStrings = { " " };
		protected void onPreExecute() {
			super.onPreExecute();
			showDialog();
		}
		@Override
		protected String[] doInBackground(Void... params) {
			try {
				bIsWorking = true;
				loadData();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}

			bIsWorking = false;
			return mStrings;
		}

		@Override
		protected void onPostExecute(String[] result) {

			processLoadData();

			dismissDialog();
			bIsWorking = false;
			
			super.onPostExecute(result);
		}

	}

	// 加载数据
	abstract protected void loadData();

	// 数据数据更新
	abstract protected void processLoadData();

	// 执行查询数据任务
	public class SaveDataTask extends AsyncTask<Void, Void, String[]> {
		private String[] mStrings = { " " };

		protected void onPreExecute() {
			super.onPreExecute();
			
			showDialog();
		}
		
		@Override
		protected String[] doInBackground(Void... params) {
			try {

				bIsWorking = true;
				processSaveData();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}

			bIsWorking = false;
			return mStrings;
		}

		@Override
		protected void onPostExecute(String[] result) {

			
			dismissDialog();
			processAfterSaveData();
			
			bIsWorking = false;
			super.onPostExecute(result);
		}

	}

	// 处理保存数据
	abstract protected void processSaveData();
	// 数据数据更新
	abstract protected void processAfterSaveData();

	/**
	 * 显示登录提示对话框。
	 */
	protected void showDialog() {
		if ( !bIsNeedProgressDialog )
			return;
		
		progressDialog = ProgressDialog
				.show(this, "正在执行....", "请稍后", true, false);
		progressDialog.setCancelable(true);
	}

	/**
	 * 隐藏登录提示对话框。
	 */
	protected void dismissDialog() {

		if ( !bIsNeedProgressDialog ) 
			return;
		
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	
	
	//////////////////////////////////////////////图片下载
	// 图片下载
	String msDownloadFilePath = "";
	boolean mDownloadOK = false;
	MyCallBack_download myDownloadCallback = null;
	public String sDownloadInfo = "";

	// 处理保存数据
	abstract protected void processDownload(boolean bOK);

	/*
	 * 下载功能
	 */
	public class MyCallBack_download implements CallbackInterface {
		BaseViewSaveActivity mAc;

		public void func(String responseText) {

			if (responseText == null)
				return;

			File ApkFile = new File(responseText);

			// 是否已下载更新文件
			if (ApkFile.exists()) {

				
				mDownloadOK = true;
				// 存在
				processDownload(true);
				
				return;
			}
			mDownloadOK = false;
		}

		public void cancel(String responseText) {
			sDownloadInfo = "文件不存在或者下载失败";
		}

		MyCallBack_download(BaseViewSaveActivity ac) {
			mAc = ac;
		}
	}
	
	public boolean isDownloadOK()
	{
		return mDownloadOK;
	}
	
	
	
	
	/*
	 * 下载文件
	 */
	public boolean downloadFile(String sRemote, String sLocal) {
		
		String sLoacalFile = appContext.getPictureDirectory()+ FileUtils.getFileName(sRemote);
		
		msDownloadFilePath = sLoacalFile;
		
		if (FileUtils.checkFileExists(msDownloadFilePath)) {
			mDownloadOK = false;
			return false;
		}
		
		
		mDownloadOK = false;
	
		new DownloadFileTask(appContext.m_ice, this, sRemote, sLocal,
				myDownloadCallback).execute();
		return true;
	}
	
}
