package com.winso.interactive.app;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.app.Service;
import android.content.Intent;
public class ServerHeartService extends Service {
    private WifiManager wifiManager=null;  //用来检测自身网络是否连接
    private boolean isConnected;
    private static int errorNum = 0; //用于一次连接不成功后记录，并且，记录到4次后再对程序进行提示
 
    private static final String TAG = "ServerHeartService";
    private String getMsg = "";
 
    private int _getPort = 7894;
    private int _sendPort = 5489;
    private String _partnerIP;
    private static  boolean   ServerFlag = false;
    private int num = 1;
    private Thread thread;
    private DatagramSocket socket;
    private boolean passFlag = false;
 
    private boolean controlFlag = false;
    private int length = 1;
 
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
 
    @Override
    public void onCreate() {
        Log.v(TAG, "In Server onCreate");
    }
 
    @Override
    public void onDestroy() {
        System.out.println("DestoryJoinnerUdp");
        thread.stop();
        super.onDestroy();
    }
 
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        thread = new Thread(new Runnable() {
        @Override
          public void run() {
                while(true){
                    try {
                       //1.自测wifi是否正常
                        isConnected = isWifiConnect();
                        if(isConnected == false){
                            if(length == 1){
                                //不能直接在Service上通知，必须经过Handler去提示
//                                Message heartMessage = ServerTestingActivity.heartHandler.obtainMessage();
//                                heartMessage.arg1 = 1;
//                                ServerTestingActivity .heartHandler.sendMessage(heartMessage);
//                                //因为是多线程，防止多次发送Handler
                                length ++;
                            }
                            break;
                        }
 
                        SendMsg();
                        GetMsg();
                        //用于调试，可删除
                        String msg = "";
                        if( ServerFlag == false){
                            msg = "false";
                        }else{
                            msg = "true";
                        }
                        Log.d("ServerFlag", msg);
                        //记录未接收到客户端发来的消息 +1
                        if( ServerFlag == false){
                            errorNum ++;
                        }else{
                            errorNum = 0;
                        }
 
                        if(errorNum == 4){
                            ServerHeartMsg("客户端网络故障！");
                            break;
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }
    private boolean isWifiConnect(){
        try{
            ConnectivityManager conManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = conManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            return mWifi.isConnected();
        }catch(Exception e){
            return false;
        }
}
 
// 将控制信息已广播的形式发送到Activity.
    private void ServerHeartMsg(String str) {
//        final Intent intent = new Intent();
//        intent.setAction(Constants.Controller.UDP_BORADCAST);
//        intent.putExtra(UDPData.UDP_HEART_ERROR, str);
//        sendBroadcast(intent);
    }
 
    public void GetMsg() throws IOException, InterruptedException{
        try {
            socket = new DatagramSocket(_getPort);
            Log.d(TAG, "Server连接到端口");
            byte data[] = new byte[256];
            DatagramPacket packet = new DatagramPacket(data, data.length);
        if(passFlag == false){
            socket.setSoTimeout(100000);
        }else{
            socket.setSoTimeout(3000);
        }
        socket.receive(packet);
        controlFlag = true;
        passFlag = true;
        Log.d(TAG, "Server接收到信息");
        getMsg = new String(packet.getData(), packet.getOffset(), packet.getLength());
        socket.close();
    } catch (SocketException e) {
        Log.d(TAG, "Server未找到服务器");
        socket.close();
        controlFlag = false;
        e.printStackTrace();
    } catch (UnknownHostException e) {
        Log.d(TAG, "Server未连接到服务器");
        socket.close();
        controlFlag = false;
        e.printStackTrace();
    } catch (IOException e) {
        Log.d(TAG, "Server消息未接收成功");
        socket.close();
        controlFlag = false;
        e.printStackTrace();
    }
}
 
    public void SendMsg() throws IOException{
        try {
            Thread.sleep(1000);
            socket = new DatagramSocket();
            InetAddress serverAddress = InetAddress.getByName("192.168.2.8");
            String str = "服务端网络故障！";
            byte data[] = str.getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, serverAddress, _sendPort);
            socket.send(packet);
            Log.d(TAG, "Server消息发送成功");
            socket.close();
        } catch (SocketException e) {
            Log.d(TAG, "Server未找到服务器");
            socket.close();
            e.printStackTrace();
        } catch (UnknownHostException e) {
            Log.d(TAG, "Server未连接到服务器");
            socket.close();
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(TAG, "Server消息未发送成功");
            socket.close();
            e.printStackTrace();
        } catch (InterruptedException e) {
            Log.d(TAG, "Sleep线程");
            socket.close();
            e.printStackTrace();
        }
    }
}