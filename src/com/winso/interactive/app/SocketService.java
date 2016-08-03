package com.winso.interactive.app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import com.winso.interactive.MainActivity;
import com.winso.interactive.PointActivity;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;

public class SocketService extends Service {

	public AppContext appContext;
	Thread mServiceThread;
//	CheckBackgroundThread mBackThread;
	Thread mBackThread;
	Socket client;

	boolean send2Center() {

		Socket socket = null;

		try {
			// 创建一个流套接字并将其连接到指定主机上的指定端口号
			socket = new Socket(appContext.getCookie("cent_ip"), 8119);
			//socket = new Socket("192.168.2.10", 8119);

			// 交互采用socket通讯，端口为：8119。
			// 1、 App状态主动上报：
			// App上报协议：#app,course_id,user_id,change_type\r\n
			// 服务器返回:服务器无响应消息

			// appContext.mICouseID;

			// 读取服务器端数据
			// DataInputStream input = new
			// DataInputStream(socket.getInputStream());
			// 向服务器端发送数据
			//DataOutputStream out = new DataOutputStream(
			//		socket.getOutputStream());
			// System.out.print("请输入: \t");
			String str = "#app,";
			str += String.valueOf(appContext.mICouseID);
			str += ",";
			str += String.valueOf(appContext.mIStudentID);
			str += ",";
			
			boolean bok = isBackgroundRunning();
			if ( bok )
			{
				str += "1";
			}
			else
			{
				str += "3";
			}
			str += "\r\n";
			
		    Writer writer = new OutputStreamWriter(socket.getOutputStream());  
		    writer.write(str);  
		    
		    writer.flush();//写完后要记得flush  
		    Thread.sleep(5000);
		    writer.close();  
		    
			//out.close();
			// input.close();

		} catch (Exception e) {
			System.out.println("客户端异常:" + e.getMessage());
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					socket = null;
					System.out.println("客户端 finally 异常:" + e.getMessage());
				}
			}
		}

		return true;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		appContext = (AppContext) getApplication();
		AppManager am = AppManager.getAppManager();
//		mServiceThread = new Thread(new SocketServerThread());
		
//		mBackThread = new CheckBackgroundThread();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);

		mServiceThread = new Thread(mServiceRunnable);
		mBackThread = new Thread(mBackRunnable);
		mServiceThread.start();
		mBackThread.start();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	Runnable mBackRunnable = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (true) {
				try {

					Thread.sleep(20 * 1000);
					send2Center();

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	};
	
	public class CheckBackgroundThread extends Thread {
		// private static final int PORT = 54321;

		AppManager am;

		private CheckBackgroundThread() {
			am = AppManager.getAppManager();

		}

		@Override
		public void run() {
			while (true) {
				try {

					Thread.sleep(20 * 1000);
					send2Center();

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}

	}

	private boolean isBackgroundRunning() {
		String processName = "match.android.activity";

		ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

		if (activityManager == null)
			return false;
		// get running application processes
		List<ActivityManager.RunningAppProcessInfo> processList = activityManager
				.getRunningAppProcesses();
		for (ActivityManager.RunningAppProcessInfo process : processList) {
			if (process.processName.startsWith(processName)) {
				boolean isBackground = process.importance != IMPORTANCE_FOREGROUND
						&& process.importance != IMPORTANCE_VISIBLE;
				boolean isLockedState = keyguardManager
						.inKeyguardRestrictedInputMode();
				if (isBackground || isLockedState)
					return true;
				else
					return false;
			}
		}
		return false;
	}

	Runnable mServiceRunnable = new Runnable() {
		public void run() {
			try {
				ServerSocket server = new ServerSocket(
						AppContext.LOCAL_UDP_PORT);

				while (true) {
					System.out.println("begin client connected");
					client = server.accept();
					System.out.println("client connected");

					BufferedReader reader = new BufferedReader(
							new InputStreamReader(client.getInputStream()));
					System.out.println("read from client:");

					String textLine = reader.readLine();
					// if (textLine.equalsIgnoreCase("EXIT")) {
					// System.out.println("EXIT invoked, closing client");
					// break;
					// }

					sendMsgtoActivty(textLine);
					System.out.println(textLine);

					PrintWriter writer = new PrintWriter(new BufferedWriter(
							new OutputStreamWriter(client.getOutputStream())));

					writer.println("success");
					writer.flush();

					writer.close();
					reader.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.err.println(e);
			}
		}
	};
	
	public class SocketServerThread extends Thread {
		// private static final int PORT = 54321;

		private SocketServerThread() {
		}

		@Override
		public void run() {
			try {
				ServerSocket server = new ServerSocket(
						AppContext.LOCAL_UDP_PORT);

				while (true) {
					System.out.println("begin client connected");
					client = server.accept();
					System.out.println("client connected");

					BufferedReader reader = new BufferedReader(
							new InputStreamReader(client.getInputStream()));
					System.out.println("read from client:");

					String textLine = reader.readLine();
					// if (textLine.equalsIgnoreCase("EXIT")) {
					// System.out.println("EXIT invoked, closing client");
					// break;
					// }

					sendMsgtoActivty(textLine);
					System.out.println(textLine);

					PrintWriter writer = new PrintWriter(new BufferedWriter(
							new OutputStreamWriter(client.getOutputStream())));

					writer.println("success");
					writer.flush();

					writer.close();
					reader.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.err.println(e);
			}
		}

	}

	/**
	 * 把信息传递给activity
	 * 
	 * @param msg
	 */
	private void sendMsgtoActivty(String src) {

		boolean bNeedSend = false;
		String[] vsAll = src.split(",", -1);

		if (vsAll.length <= 0)
			return;

		String sCmd = vsAll[0];

		if (sCmd.equals(AppContext.CMD_POINT)) {

			if (vsAll.length < 2)
				return;

			bNeedSend = true;
		} else if (sCmd.equals(AppContext.CMD_UPDATE_COURSE)) {

			if (vsAll.length < 2)
				return;

			bNeedSend = true;
		} else if (sCmd.equals(AppContext.CMD_UPDATE_ANSWER)) {
			bNeedSend = true;
		}
		// mICouseID

		if (!bNeedSend)
			return;

		if (isRunningMainActivity() == false) {
			Intent dialogIntent = new Intent(getBaseContext(),
					MainActivity.class);
			dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			getApplication().startActivity(dialogIntent);

		}

		// for (int i = 0; i < 20; i++) {
		//
		// if (isRunningMainActivity() == true) {
		// break;
		//
		// }
		//
		// try {
		// Thread.sleep(200);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// }
		// 发送命令
		Intent intent = new Intent("android.intent.action.interactive");
		intent.putExtra("cmd", sCmd);

		for (int i = 1; i < vsAll.length; i++) {
			String sParam = "param";
			sParam += String.valueOf(i);
			intent.putExtra(sParam, vsAll[i]);
		}

		this.sendBroadcast(intent);
	}

	private boolean isRunningMainActivity() {
		Intent intent = new Intent();
		intent.setClassName("com.winso.interactive", "MainActivity");

		if (getPackageManager().resolveActivity(intent, 0) == null) {
			// 说明系统中不存在这个activity
			return false;
		}
		return true;
	}

}