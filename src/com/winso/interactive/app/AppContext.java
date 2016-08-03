package com.winso.interactive.app;

import android.location.LocationManager;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.winso.interactive.PointActivity;
import com.winso.interactive.app.AppConfig;
import com.winso.comm_library.*;

import com.winso.comm_library.icedb.ICEDBUtil;
import com.winso.comm_library.icedb.SelectHelp;
import com.winso.comm_library.icedb.SelectHelpParam;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

/**
 * 应用程序业务访问类，用于访问各类业务组件�?
 * 
 * @author eric goo
 * @version 1.0
 * @created 2014-09-01
 */

public class AppContext extends Application {

	// 版本发布的目�?
	public static String mSUpgradePath = "release"; // 版本发布的服务器目录

	public static final int NETTYPE_WIFI = 0x01;
	public static final int NETTYPE_CMWAP = 0x02;
	public static final int NETTYPE_CMNET = 0x03;
	public static final String STORE_PIC_PATH = "inteactive_pic";

	public static final String S_REMOTE_PIC_PATH = "/upfile/upload/";
	public static final int CENT_PORT = 8860;
	public static final int LOCAL_UDP_PORT = 9990; // 本地的UDP服务

	// 0即问即答、1抢答、2挑人、3挑组
	public static final int ASK_TYPE_IM = 0;
	public static final int ASK_TYPE_RUSH = 1;
	public static final int ASK_TYPE_SELECT_PEOPLE = 2;
	public static final int ASK_TYPE_GROUP = 3;

	
	public boolean bNeedRefreshMainReview=false;
	//

	// 题目类别：0单选题、1多选题、2判断题、3问答题、4图片标注题
	public static final int QUESTION_TYPE_SINGLE = 0;
	public static final int QUESTION_TYPE_MULT = 1;
	public static final int QUESTION_TYPE_YESNO = 2;
	public static final int QUESTION_TYPE_CONTENT = 3;
	public static final int QUESTION_TYPE_PIC = 4;

	// 控制命令
	public static String CMD_POINT = "cmd_point"; // 点到 命令格式为: cmd_point,校验码
	public static String CMD_UPDATE_COURSE = "cmd_update_course"; // 更亲当前的班次  时间格式为  2014-01-02 12:12:12
																  // cmd_update_course,course_id,from_time
	public static String CMD_UPDATE_ANSWER = "cmd_update_answer"; // 更新题目,
	public static String CMD_UPDATE_IP = "update_ip"; // 本地更新IP
	
	public boolean mIsSelectPeople = false;

	public int mClassID = -1;
	public int mICouseID = -1; // 当前的课程号
	public int mIStudentID = -1; // 学号
	public String mSCheckPointNo = "";
	public int mGroupID = 0; // 组号
	public String mSCurIP = ""; // 最新的IP

	// 登录后更新当前

	public String mUserName = "admin";
	public String mUserRight;

	public SelectHelp mHelpRight = new SelectHelp(); // 用户权限列表

	private Map<String, String> mSQLMap = new HashMap<String, String>();
	// 用于存储key value的对应�??
	private Map<String, String> mMap = new HashMap<String, String>();
	// login_user,login_pass,cent_ip,cent_port

	// 用于访问本地的数据库
	//public SQLiteUtil mSQLiteDB = new SQLiteUtil();
	public String mLongitude = ""; // 经度
	public String mLatitude = ""; // 纬度

	public String DEFAULT_DB_SAVE_PATH() {
		// return getApplicationContext().getFilesDir().getPath() +
		// File.separator;
		return getPictureDirectory();
	}

	public LocationManager mGPS = null;

	public String getGPS() {

		return "";

	}

	// 用于保存配置信息
	TinyDB mTinydb = null;

	public ICEDBUtil m_ice = new ICEDBUtil();

	public AppContext() {
		super();

	}

	public void initApp() {
		mTinydb = new TinyDB(this);

		AssetsDatabaseManager.initManager(this);

		if (getCookie("cent_ip").length() <= 1) {

			setCookie("cent_ip", "www.zjtouchnet.com");

		}

		if (getCookie("http_port").length() <= 0) {
			setCookie("http_port", "8080");
		}

		if (getCookie("is_check_up").length() <= 0) {
			setCookie("is_check_up", "1");
		}

		// 设置默认查询时间
		// getCurDateTime

		setCookie("start_time", TimeZoneUtil.getDateByDay(10) + " 00:01:01");
		setCookie("end_time", TimeZoneUtil.getCurDate() + " 23:59:59");
		setCookie("break_prop", "99");

		// startCustomService();

	}

	public void convertSQLMap(String sContent) {
		if (sContent == null)
			return;

		mSQLMap.clear();
		String[] vsAll = sContent.split(";");

		for (int i = 0; i < vsAll.length; i++) {
			String[] vSub = vsAll[i].split("@");

			if (vSub.length >= 2) {

				mSQLMap.put(vSub[0].trim(), vSub[1]);
			}
		}
	}

	public String getSQL(String sType) {
		return mSQLMap.get(sType);
	}

	public boolean loadInitConfigure(String sIP) {

		// 加载 http_server
		String sTmp = m_ice.getConfigure("common", "http_port");
		if (sTmp.length() > 0)
			setCookie("http_port", sTmp);

		sTmp = "";
		sTmp = m_ice.getConfigure("common", "http_server");
		if (sTmp.length() > 0)
			setCookie("http_server", sTmp);

		// 加载数据库文�?
//		EasyLog.debug(DEFAULT_DB_SAVE_PATH() + "break_law.db");
//
//		if (mSQLiteDB.login(DEFAULT_DB_SAVE_PATH() + "break_law.db") == false) {
//			EasyLog.debug(mSQLiteDB.getMsg());
//		}

		
		
		getLastCourseID();
		
		// 上报IP地址

		updateUserIP(sIP);
		
		
		
		return true;

	}

	// 获取服务器上Android App的版本号
	public int getServerVersion() {
		
		String str = m_ice.getConfigure("common","app_android_version");
//		System.out.println("Appcontext--->" + str + "---");
		return  str == null || str.equals("") ? 0 : Integer.parseInt(m_ice.getConfigure("common",
						"app_android_version"));
	}

	public boolean isCheckUp() {
		if (Integer.parseInt(getCookie("is_check_up")) == 0) {
			return false;
		}
		return true;
	}

	public String getDBID(String sType) {

		return m_ice.getID(sType);
	}

	public String getTime() {

		return m_ice.getTime();
	}

	public SelectHelp getItemDetail(String sId) {

		// 加载配置信息
		SelectHelp help = m_ice.select("get_br_check_item_detail", sId);

		// help.changeFiledName("hazard_id", "detail_id");
		// help.changeFiledName("hazard_content", "content");

		return help;
	}

	public SelectHelp getHazardContent(String sType) {

		// 加载配置信息
		return m_ice.select("get_hazard_content", sType);

	}

	/**
	 * 设置Cookie，可以持续保存�??
	 * 
	 * @return
	 */
	public void setCookie(String k, String v) {
		mTinydb.putString(k, v);
	}

	public void setCookieInt(String k, int v) {
		mTinydb.putInt(k, v);
	}

	public void setCookieBoolean(String k, boolean v) {
		mTinydb.putBoolean(k, v);
	}

	/**
	 * 查询 Cookie
	 * 
	 * @return 返回查到的Cookie
	 */
	public String getCookie(String k) {
		return mTinydb.getString(k);
	}

	public int getCookieInt(String k) {
		return mTinydb.getInt(k);
	}

	public boolean getCookieBoolean(String k) {
		return mTinydb.getBoolean(k);
	}

	public boolean containsProperty(String key) {
		Properties props = getProperties();
		return props.containsKey(key);
	}

	public void setProperties(Properties ps) {
		AppConfig.getAppConfig(this).set(ps);
	}

	public Properties getProperties() {
		return AppConfig.getAppConfig(this).get();
	}

	public void setProperty(String key, String value) {
		AppConfig.getAppConfig(this).set(key, value);
	}

	public String getProperty(String key) {
		return AppConfig.getAppConfig(this).get(key);
	}

	public void removeProperty(String... key) {
		AppConfig.getAppConfig(this).remove(key);
	}

	/**
	 * �?测网络是否可�?
	 * 
	 * @return
	 */
	public boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null && ni.isConnectedOrConnecting();
	}

	/**
	 * 获取当前网络类型
	 * 
	 * @return 0：没有网�? 1：WIFI网络 2：WAP网络 3：NET网络
	 */
	public int getNetworkType() {
		int netType = 0;
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			String extraInfo = networkInfo.getExtraInfo();
			if (!StringUtils.isEmpty(extraInfo)) {
				if (extraInfo.toLowerCase().equals("cmnet")) {
					netType = NETTYPE_CMNET;
				} else {
					netType = NETTYPE_CMWAP;
				}
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = NETTYPE_WIFI;
		}
		return netType;
	}

	/**
	 * 判断当前版本是否兼容目标版本的方�?
	 * 
	 * @param VersionCode
	 * @return
	 */
	public static boolean isMethodsCompat(int VersionCode) {
		int currentVersion = android.os.Build.VERSION.SDK_INT;
		return currentVersion >= VersionCode;
	}

	/**
	 * 获取App安装包信�?
	 * 
	 * @return
	 */
	public PackageInfo getPackageInfo() {
		PackageInfo info = null;
		try {
			info = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace(System.err);
		}
		if (info == null)
			info = new PackageInfo();
		return info;
	}

	public static boolean isSDAviable() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			// sd card 可用
			return true;

		}

		return false;
	}

	public String getPictureDirectory() {

		if (isSDAviable()) {
			File directory = null;
			directory = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath()
					+ File.separator
					+ AppContext.STORE_PIC_PATH);
			if (!directory.exists()) {
				directory.mkdirs();
			}
			return directory.getAbsolutePath() + "/";
		}

		// 只能存在本地文件�?
		String sDest = getApplicationContext().getFilesDir().getPath()
				+ File.separator + AppContext.STORE_PIC_PATH + File.separator;

		File directory = new File(sDest);
		if (!directory.exists()) {
			directory.mkdirs();
		}

		return sDest;

	}

	// 判断用户是否有指定流程节点某项权�?
	public boolean hasNodeRight(int iRightID) {

		return true;
	}

	// ///////////////////////////////////////////////////////////////////////

	// /
	// 与业务相关的查询
	public SelectHelp getNoticeDetail(String sID) {

		SelectHelpParam helpParam = new SelectHelpParam();

		helpParam.add(sID);

		return m_ice.selectByParam(AppSQLConst.S_SQL_GET_NOTICE_DETAIL,
				helpParam.get());
	}

	// 与业务相关的查询
	public SelectHelp getNotice() {
		return m_ice.selectByParam(AppSQLConst.S_SQL_GET_NOTICE, "");
	}

	// 提交考勤
	public boolean insertAttendance() {
		SelectHelpParam helpParam = new SelectHelpParam();

		helpParam.add(String.valueOf(mIStudentID));
		helpParam.add(String.valueOf(mICouseID));

		return m_ice.execSQLByParam(AppSQLConst.S_SQL_INSERT_ATTENDANCE,
				helpParam.get());
	}

	// 获取是否是已经签到
	public String getIsAtandence() {
		SelectHelp help = m_ice.selectByParam(
				AppSQLConst.S_SQL_ATANDANCE_IS_ANSANCE,
				String.valueOf(mIStudentID));
		if (help.size() <= 0)
			return "";

		return help.valueStringByName(0, "create_time");
	}
	// 获取是否是已经请假
	public boolean getISLeave() {
		SelectHelp help = m_ice.selectByParam(
				AppSQLConst.S_SQL_LEAVE_IS_LEAVE,
				String.valueOf(mIStudentID));
		
		if (help.size() <= 0)
			return false;

		return true;
	}
	
	// 获取校验码
	public String getCheckNo() {

		return mSCheckPointNo;
	}

	// 最近的请假历史
	public SelectHelp getLeaveHis() {
		SelectHelpParam helpParam = new SelectHelpParam();
		helpParam.add(String.valueOf(mIStudentID));

		return m_ice.selectByParam(AppSQLConst.S_SQL_GET_LEAVE_HIS,
				helpParam.get());
	}

	//
	public SelectHelp getTeacherList() {
		// SelectHelpParam helpParam = new SelectHelpParam();
		// helpParam.add(String.valueOf(mIStudentID));

		return m_ice.selectByParam(AppSQLConst.S_SQL_GET_TEACHER_LIST, "");
	}

	public SelectHelp getStudentList() {
		SelectHelpParam helpParam = new SelectHelpParam();
		helpParam.add(String.valueOf(mIStudentID));

		return m_ice.selectByParam(AppSQLConst.S_SQL_GET_STUDENT_LIST,
				helpParam.get());
	}
	public SelectHelp getStudentList_review() {
		SelectHelpParam helpParam = new SelectHelpParam();
		helpParam.add(String.valueOf(mIStudentID));
		//helpParam.add(String.valueOf(mICouseID));
		helpParam.add(String.valueOf(mIStudentID));
		
		SelectHelp helpNew =  m_ice.selectByParam(AppSQLConst.S_SQL_GET_STUDENT_LIST_NOT_REVIEW,
				helpParam.get());
		
		//更新是否评阅过
		helpParam.clear();
		//S_SQL_GET_STUDENT_HAS_REVIEW
		helpParam.add(String.valueOf(mICouseID));
		helpParam.add(String.valueOf(mIStudentID));
		
		SelectHelp helpHasReview =  m_ice.selectByParam(AppSQLConst.S_SQL_GET_STUDENT_HAS_REVIEW,
				helpParam.get());
		
		
		for(int i=0;i<helpNew.size();i++)
		{
			
			for(int j=0;j<helpHasReview.size();j++)
			{
				if ( helpNew.valueIntByName(i, "title_id") ==helpHasReview.valueIntByName(j, "be_user_id") )
				{
					helpNew.setValueString(i,"title_right","已评");
					
					
				}
			}
			
		}
		
		
		return helpNew;
	}
	
	public SelectHelp getStudentList_review_load() {
		SelectHelpParam helpParam = new SelectHelpParam();
		helpParam.add(String.valueOf(mIStudentID));
		helpParam.add(String.valueOf(mICouseID));
		
		return m_ice.selectByParam(AppSQLConst.S_SQL_GET_STUDENT_LIST_NOT_REVIEW,
				helpParam.get());
	}
	
	//
	
	public SelectHelp getItemList() {

		return m_ice.selectByParam(AppSQLConst.S_SQL_GET_ITEM_LIST, "");
	}

	//获取当前学生的评价列表
	public SelectHelp getItemList_student(String sUserID) {
		
		
		SelectHelp  helpAll=  m_ice.selectByParam(AppSQLConst.S_SQL_GET_ITEM_LIST, "");
		
		//
		SelectHelpParam helpParam = new SelectHelpParam();
		helpParam.add(String.valueOf(mICouseID));
		helpParam.add(sUserID);

		SelectHelp  helpHasReview=  m_ice.selectByParam("select a.*,b.grade_desc from evaluation_result a,evaluation_grade b  where a.course_id=:course_id<int>  and a.be_user_id=:user_id<int>  and a.grade_id=b.grade_id",
				helpParam.get());
		
		
		for(int i=0;i<helpAll.size();i++)
		{
			
			for(int j=0;j<helpHasReview.size();j++)
			{
				if ( helpAll.valueIntByName(i, "title_id") ==helpHasReview.valueIntByName(j, "item_id") )
				{
					
					
					
					if ( helpHasReview.valueIntByName(j,"user_id") == mIStudentID )
					{
						helpAll.setValueString(i,"title_right",helpHasReview.valueStringByName(j,"grade_desc"));
					}
					else
					{
						//helpAll.setValueString(i,"title_right","----");
					}
					
				}
			}
			
		}
		
		
		return helpAll;
	}
	
	//选择评分
	public SelectHelp getItemGradeList(String sItemID) {
		
		//S_SQL_GET_ITEM_GRADE_ID
		//SelectHelp nHelp = new SelectHelp();
		
		
		
		//old version
		SelectHelpParam helpParam = new SelectHelpParam();
		helpParam.add(sItemID);

		return m_ice.selectByParam(AppSQLConst.S_SQL_GET_ITEM_GRADE_LIST, helpParam.get());
	}

	
	// 保存请假

	public boolean insertLeave(String sTeacherID, String sContent,
			String sFromDate, String sToDate) {
		SelectHelpParam helpParam = new SelectHelpParam();

		helpParam.add(String.valueOf(mIStudentID));
		helpParam.add(sTeacherID);
		helpParam.add(sContent);
		helpParam.add(sFromDate);
		helpParam.add(sToDate);

		return m_ice.execSQLByParam(AppSQLConst.S_SQL_INSERT_LEAVE,
				helpParam.get());
	}

	// 获取详细的Leave信息
	public SelectHelp getLeaveDeatail(String sID) {
		SelectHelpParam helpParam = new SelectHelpParam();
		helpParam.add(sID);

		return m_ice.selectByParam(AppSQLConst.S_SQL_GET_LEAVE_DETAIL,
				helpParam.get());
	}

	
	//获取最新的课程
	
	public void getLastCourseID() {
		SelectHelpParam helpParam = new SelectHelpParam();
		helpParam.add(String.valueOf(mIStudentID));
		SelectHelp help = m_ice.selectByParam(AppSQLConst.S_SQL_GET_LAST_COURSE,
				helpParam.get());
		
		if ( help.size() <= 0 )
			return;
	
		mICouseID = help.valueIntByName(0, "course_id");
		mClassID = help.valueIntByName(0, "class_id");
		
	}
	// 获取评定的教师
	public SelectHelp getReviewTeacher(String sID1, String sID2) {
		SelectHelpParam helpParam = new SelectHelpParam();
		helpParam.add(sID1);
		helpParam.add(sID2);

		return m_ice.selectByParam(AppSQLConst.S_SQL_GET_REVIEW_TEACHER_NAME,
				helpParam.get());
	}

	// 获取最新的问题
	public SelectHelp getLastAsk() {
		SelectHelpParam helpParam = new SelectHelpParam();
		helpParam.add(String.valueOf(mICouseID));

		return m_ice.selectByParam(AppSQLConst.S_SQL_GET_LAST_ASK,
				helpParam.get());

	}

	// 提交考勤
	public int insertAnswer(String sAskID, int is_a, int is_b, int is_c,
			int is_d, int is_e, int is_f, int is_g, String sAnswerContent,
			int bRight,String sDrawInfo) {
		// ask_id,student_id,group_id,is_a,is_b,is_c,is_d,is_e,is_f,is_g,answer_content
		SelectHelpParam helpParam = new SelectHelpParam();

		helpParam.add(sAskID);
		helpParam.add(String.valueOf(mIStudentID));

		helpParam.add(String.valueOf(mGroupID));

		helpParam.add(String.valueOf(is_a));
		helpParam.add(String.valueOf(is_b));
		helpParam.add(String.valueOf(is_c));
		helpParam.add(String.valueOf(is_d));
		helpParam.add(String.valueOf(is_e));
		helpParam.add(String.valueOf(is_f));
		helpParam.add(String.valueOf(is_g));

		helpParam.add(sAnswerContent);
		helpParam.add(String.valueOf(bRight));
		helpParam.add(sDrawInfo);

		return m_ice.execSQLByParam_int(AppSQLConst.S_SQL_INSERT_ANSWER,
				helpParam.get());
	}

	// 更新IP地址
	public int updateUserIP(String sIP) {

		// ask_id,student_id,group_id,is_a,is_b,is_c,is_d,is_e,is_f,is_g,answer_content
		SelectHelpParam helpParam = new SelectHelpParam();

		helpParam.add(sIP);

		helpParam.add(String.valueOf(mIStudentID));

		return m_ice.execSQLByParam_int(AppSQLConst.S_SQL_UPDATE_USER_IP,
				helpParam.get());

		// return insertNetLog(sIP);
	}

	public int insertNetLog(String sIP, int iType, String sComment) {

		// ask_id,student_id,group_id,is_a,is_b,is_c,is_d,is_e,is_f,is_g,answer_content
		SelectHelpParam helpParam = new SelectHelpParam();

		helpParam.add(String.valueOf(mIStudentID));
		helpParam.add(sIP);
		helpParam.add(mSCurIP);
		helpParam.add(String.valueOf(iType));

		helpParam.add(sComment);

		mSCurIP = sIP;

		return m_ice.execSQLByParam_int(AppSQLConst.S_SQL_CHANGE_NET_USER_IP,
				helpParam.get());
	}

	public boolean robAsk(String sAskID) {

		// ask_id,student_id,group_id,is_a,is_b,is_c,is_d,is_e,is_f,is_g,answer_content
		SelectHelpParam helpParam = new SelectHelpParam();

		helpParam.add(String.valueOf(mIStudentID));
		helpParam.add(sAskID);

		SelectHelp help = m_ice.execProc(AppSQLConst.S_SQL_ROB_ASK,
				helpParam.get());

		if (help.size() <= 0)
			return false;

		int iOK = help.valueIntByName(0, "return_code");

		if (iOK > 0)
			return true;

		return false;
		// return insertNetLog(sIP);
	}
	public boolean removeERSult(int iItemID)
	{
		SelectHelpParam helpParam = new SelectHelpParam();
		
		helpParam.add(String.valueOf(mICouseID));
		helpParam.add(String.valueOf(iItemID));
		helpParam.add(String.valueOf(mIStudentID));

		
		return m_ice.execSQLByParam("delete from evaluation_result where course_id=:course_id<int> and item_id=:item_id<int> and be_user_id=:user_id<int>",
				helpParam.get());
		
	
	}
	public boolean isExistERSult(int iItemID)
	{
		
		SelectHelpParam helpParam = new SelectHelpParam();
		helpParam.add(String.valueOf(iItemID));
		helpParam.add(String.valueOf(mICouseID));

		SelectHelp help = m_ice.selectByParam(AppSQLConst.S_SQL_GET_IS_GRADE,
				helpParam.get());
		
		
		if (help.size() <= 0)
			return false;

		int iOK = help.valueIntByName(0, "count_size");

		if (iOK > 0)
			return true;

		return false;
	}
	
	public int insertEResult(int iItemID,int iEVType,int iGradeID,String sEVInfo,String sBeUserID,String sBeGroupID) {

		//item_id,user_id,course_id,evaluation_type,grade_id,evaluation_info,be_user_id,be_group_id
		
		SelectHelpParam helpParam = new SelectHelpParam();

		helpParam.add(String.valueOf(iItemID));
		helpParam.add(String.valueOf(mIStudentID));
		helpParam.add(String.valueOf(mICouseID));
		
		helpParam.add(String.valueOf(iEVType));
		helpParam.add(String.valueOf(iGradeID));
		helpParam.add(sEVInfo);
		helpParam.add(sBeUserID);
		helpParam.add(sBeGroupID);
	
		return m_ice.execSQLByParam_int(AppSQLConst.S_SQL_INSERT_EVALUATION_RESULT,
				helpParam.get());
	}
	
}
