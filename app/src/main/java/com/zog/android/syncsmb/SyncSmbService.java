package com.zog.android.syncsmb;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.zog.android.syncsmb.MainActivity;

//杀不死的系统级后台服务
//XML加
//<service android:enabled="true" android:name=".SyncSmbService" android:process="system" android:priority = "1000">
//</service>
public class SyncSmbService extends Service {

    static Timer timer = null;
    public static final String CHANNEL_ID_STRING = "SyncSmbService";
    private Notification notification;

    //清除通知
    public static void StopServer() {
//        NotificationManager mn = (NotificationManager) MainActivity.getContext().getSystemService(NOTIFICATION_SERVICE);
//        mn.cancelAll();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        Intent intent = new Intent(MainActivity.getContext(), SyncSmbService.class);
        MainActivity.getContext().stopService(intent);
    }

    //添加通知
    public static void StartServer(int delayTime) {

        Intent intent = new Intent(MainActivity.getContext(), SyncSmbService.class);
        intent.putExtra("delayTime", delayTime);
        //下面固定写法 为了安卓8以上
        if (Build.VERSION.SDK_INT >= 26) {
            MainActivity.getContext().startForegroundService(intent);
        } else {
            MainActivity.getContext().startService(intent);
        }
    }

    @Override
    public void onCreate() {
        Log.d("SyncSmbService", "===========create=======");
        //下面固定写法 为了安卓8以上
        super.onCreate();
        NotificationManager mn = (NotificationManager) SyncSmbService.this.getSystemService(NOTIFICATION_SERVICE);
        NotificationChannel mChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            mChannel = new NotificationChannel(CHANNEL_ID_STRING, getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_LOW);
            mn.createNotificationChannel(mChannel);
            notification = new Notification.Builder(getApplicationContext(), CHANNEL_ID_STRING).build();
            startForeground(1, notification);
        }

    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public int onStartCommand(final Intent intent, int flags, int startId) {
        //下面固定写法 为了安卓8以上
        if (Build.VERSION.SDK_INT >= 26) {
            startForeground(1, notification);
        }
        //下面开始正式代码
        int period = intent.getIntExtra("delayTime", 2 * 60 * 60 * 1000);
        Log.d("SyncSmbService", "===========delay=======" + period);
        if (null == timer) {
            timer = new Timer();
        }
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                //主要做的任务在此，其他均为固定写法
                Log.d("SyncSmbService", "===========notice=======");
                doNotification("start sync to smb");
                SharedPreferences config = getSharedPreferences("ShareConfig", 0);
                MainActivity.doSync(false, config,0);
            }
        }, period, period);

        return START_REDELIVER_INTENT;
        //return super.onStartCommand(intent, flags, startId);
    }

//    参考回答：
//            **onStartCommand方式中，返回START_STICKY或则START_REDELIVER_INTENT**
//            **START_STICKY**：如果返回START_STICKY，表示Service运行的进程被Android系统强制杀掉之后，Android系统会将该Service依然设置为started状态（即运行状态），但是不再保存onStartCommand方法传入的intent对象
//**START_NOT_STICKY**：如果返回START_NOT_STICKY，表示当Service运行的进程被Android系统强制杀掉之后，不会重新创建该Service
//**START_REDELIVER_INTENT**：如果返回START_REDELIVER_INTENT，其返回情况与START_STICKY类似，但不同的是系统会保留最后一次传入onStartCommand方法中的Intent再次保留下来并再次传入到重新创建后的Service的onStartCommand方法中
//**提高Service的优先级**在AndroidManifest.xml文件中对于intent-filter可以通过android:priority = "1000"这个属性设置最高优先级，1000是最高值，如果数字越小则优先级越低，同时适用于广播；
//            **在onDestroy方法里重启Service**当service走到onDestroy()时，发送一个自定义广播，当收到广播时，重新启动service；
//            **提升Service进程的优先级**进程优先级由高到低：前台进程 一 可视进程 一 服务进程 一 后台进程 一 空进程可以使用startForeground将service放到前台状态，这样低内存时，被杀死的概率会低一些；
//            **系统广播监听Service状态**
//            **将APK安装到/system/app，变身为系统级应用**
//            **注意**：以上机制都不能百分百保证Service不被杀死，除非做到系统白名单，与系统同生共死

    public void doNotification(String text) {
                // TODO Auto-generated method stub
                NotificationManager mn = (NotificationManager) SyncSmbService.this.getSystemService(NOTIFICATION_SERVICE);
                Notification.Builder builder = new Notification.Builder(SyncSmbService.this);
                Intent notificationIntent = new Intent(SyncSmbService.this, MainActivity.class);//点击跳转位置
                PendingIntent contentIntent = PendingIntent.getActivity(SyncSmbService.this, 0, notificationIntent, 0);
                builder.setContentIntent(contentIntent);
                builder.setSmallIcon(R.mipmap.ic_launcher);
                builder.setTicker("SyncSmbService"); //测试通知栏标题
                builder.setContentText(text); //下拉通知啦内容
                builder.setContentTitle("SyncSmbService");//下拉通知栏标题
                builder.setAutoCancel(true);
                builder.setDefaults(Notification.DEFAULT_ALL);
                Notification notification = builder.build();
                mn.notify((int) System.currentTimeMillis(), notification);
    }

    @Override
    public void onDestroy() {
        Log.d("SyncSmbService", "===========destroy=======");
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        if (Build.VERSION.SDK_INT >= 26) {
            stopForeground(true);
        }
        //开启START_REDELIVER_INTENT 后 防止多开server
        super.onDestroy();

    }
}
