package com.zog.android.syncsmb;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.commons.io.FileUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.zog.android.syncsmb.smbtools.SMBServerConnect;
import com.zog.android.syncsmb.smbtools.SMBCopyRemoteFile;
import com.zog.android.syncsmb.smbtools.SMBServerConnectV2;
import com.zog.android.syncsmb.smbtools.SMBTools;
import com.zog.android.syncsmb.smbtools.SMBCopyLocalFile;
import com.zog.android.syncsmb.smbtools.SMBDeleteRemoteFile;
import com.zog.android.syncsmb.smbtools.SMBUploadFile;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    //private SMBTools smb;

    private Button buttonUpload;
    private Button buttonStopServer;
    private static TextView editProgress;
    private static TextView editLastUploadTime;
    private SharedPreferences config;


    //for server
    private static Context sContext = null;

    //for server
    public static Context getContext() {
        return sContext;
    }

    private boolean checkConfig() {
        config = getSharedPreferences("ShareConfig", 0);

        if (config.getString("server", null) == null)
            return false;
        return true;
    }

    private void openConfigActivity() {
        Intent intent = new Intent(MainActivity.this, ConfigActivity.class);
        //startActivity(intent);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                connect();
                Log.d("mainactivity", "connect");
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //cancelled
                Log.d("mainactivity", "cancellled");
            }
        }
    }

    private void connect() {
        new SMBServerConnect(new SMBServerConnect.AsyncResponse() {
            @Override
            public void processFinish(SMBTools smb, String error) {
                //MainActivity.this.smb = smb;
                if (smb == null) {
                    buttonUpload.setEnabled(false);
                    alert(error);
                } else {
                    buttonUpload.setEnabled(true);
                    smb.close();
                }
            }
        }).execute(getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuSetting:
                openConfigActivity();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


//    、、rename//
//DiskShare tmpShare = (DiskShare) session.connectShare(mo.getSharename() );
//
//    com.hierynomus.smbj.share.File tmpRemoteFile = tmpShare.openFile(
//            mo.getRemoteFilePath() + tmpFileName,
//            EnumSet.of(AccessMask.DELETE, AccessMask.GENERIC_WRITE),
//            EnumWithValue.EnumUtils.toEnumSet(
//                    tmpShare.getFileInformation(mo.getRemoteFilePath() + tmpFileName).getBasicInformation().getFileAttributes(),
//                    FileAttributes.class), // copy original file attributes
//            SMB2ShareAccess.ALL,
//            SMB2CreateDisposition.FILE_OPEN,
//            null);
//					            tmpRemoteFile.rename( mo.getRemoteFilePath() + realFileName, true);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //for server
        sContext = this;
        //给 权限
        PermissionUtils.isGrantExternalRW(this, 1);

        buttonStopServer = findViewById(R.id.buttonStopServer);
        buttonUpload = findViewById(R.id.buttonUpload);
        editProgress = findViewById(R.id.editProgress);
        editLastUploadTime = findViewById(R.id.editLastUploadTime);

        //读取配置，连接测试
        if (checkConfig())
            connect();
        else
            openConfigActivity();

        SharedPreferences.Editor editor = config.edit();
        editor.putBoolean("syncing", false);
        editor.commit();

        //最后更新时间
        long lastUploadTime = config.getLong("LastUploadTime", 0);
        ShowLastUploadTime(lastUploadTime);


        //开始服务
        SyncSmbService.StopServer(); //先停止   //开启START_REDELIVER_INTENT 后 防止多开server
        String timeHour = config.getString("TimeHour", "2");
        SyncSmbService.StartServer(Integer.parseInt(timeHour) * 3600 * 1000);
        //SyncSmbService.StartServer( 5*1000); //for test

        //手动停止服务
        buttonStopServer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SyncSmbService.StopServer();
            }
        });

        //按下按钮
        buttonUpload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                doSync(true, config, 0);

//                new SMBServerConnect(new SMBServerConnect.AsyncResponse() {
//                    @Override
//                    public void processFinish(final SMBTools smb, String error) {
//
//                        if (smb == null) {
//                            alert(error);
//                            return;
//                        }
                //下载的 有效
//                         new SMBCopyRemoteFile(smb, new SMBCopyRemoteFile.AsyncResponse(){
//                             @Override
//                             public void processFinish(String output, boolean error){
//
//                                 Log.d("m",output);
//                                 if(!error) {
//                                          alert("ok:"+output);
//                                 }
//                                 else {
//                                     alert(getResources().getString(R.string.error_not_found)+output);
//                                 }
//                                 smb.close();
//                             }
//                         }).execute("1.pdf");

                //删除 有效
//                         new SMBDeleteRemoteFile(smb, new SMBDeleteRemoteFile.AsyncResponse() {
//                             @Override
//                             public void processFinish(boolean success, String error) {
//                                      if(success) {
//                                                   alert("ok");
//                                          }
//                                          else {
//                                              alert(error);
//                                 }
//                                 smb.close();
//                    }
//                         }).execute("1.pdf");

                //远端列表 有效。不能获取根目录
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//                                    smb.listShareFolder("a");
//                                } catch (IOException ex) {
//                                    Log.d("m","listShareFolder " +  ex.toString());
//                                }
//                            }
//                        }).start();

                //上传 有效
//                        File up=new File(GetLocalDir("DCIM/SharedFolder/")+"2.pdf");
//                        Log.d("m","up " +   up.canRead());
//
//                            new SMBCopyLocalFile(smb,"a/2.pdf", new SMBCopyLocalFile.AsyncResponse() {
//                                @Override
//                                public void processFinish(boolean success, String error) {
//                                    if (success) {
//                                        alert("ok");
//                                    } else {
//                                        alert(error);
//                                    }
//                                    smb.close();
//                                }
//                            }).execute(up);
//                    }
//                }).execute(getApplicationContext());
            }
        });
    }

    //真正同步要做的事情
    public static void doSync(final boolean ui, final SharedPreferences config, final int retry) {

        if (retry > 10) {
            Log.d("mainactivity", "retry max 10 break");
            return;
        }

        boolean syncing = config.getBoolean("syncing", false);
        if (syncing) {
            Log.d("mainactivity", "syncing break");
            return;
        }

        SharedPreferences.Editor editor = config.edit();
        editor.putBoolean("syncing", true);
        editor.commit();

        String RemoteServer = config.getString("server", null);
        String RemoteUsername = config.getString("username", null);
        String RemotePassword = config.getString("password", null);
        String RemoteShare = config.getString("share", null);
        String RemoteDomain = config.getString("domain", null);

        //远端根路径
        String model = Build.MODEL;
        final String remoteRootDir = config.getString("PhoneName", model);
        //获取本地路径
        final List<String> localDirList = GetAllLocalDir(config);

        //连接SMB
        new SMBServerConnectV2(RemoteServer, RemoteUsername, RemotePassword, RemoteShare, RemoteDomain, new SMBServerConnectV2.AsyncResponse() {
            @Override
            public void processFinish(final SMBTools smb, final String error) {
                //连接完成
                if (smb == null) {
                    if (ui) {
                        editProgress.setText(error);
                    }
                    SharedPreferences.Editor editor = config.edit();
                    editor.putBoolean("syncing", false);
                    editor.commit();
                    //因网络中断 重试
                    doRetry(ui, config, retry, error);
                    return;
                }
                //上传
                new SMBUploadFile(smb, getSDCardDir(), localDirList, remoteRootDir, new SMBUploadFile.AsyncResponse() {
                    @Override
                    public void processFinish(boolean success, final String error) {
                        //上传完成
                        if (success) {
                            if (ui) {
                                editProgress.setText("all done");
                            }

                            //最后更新时间
                            long lastUploadTime = System.currentTimeMillis();
                            SharedPreferences.Editor editor = config.edit();
                            editor.putLong("LastUploadTime", lastUploadTime);
                            editor.putBoolean("syncing", false);
                            editor.commit();

                            if (ui) {
                                ShowLastUploadTime(lastUploadTime);
                            }
                            smb.close();

                        } else {
                            if (ui) {
                                editProgress.setText(error);
                            }
                            smb.close();

                            SharedPreferences.Editor editor = config.edit();
                            editor.putBoolean("syncing", false);
                            editor.commit();

                            //因网络中断 重试
                            doRetry(ui, config, retry, error);
                        }
                    }

                    @Override
                    public void progressUpdate(String progresses) {
                        if (ui) {
                            editProgress.setText(progresses);
                        }
                    }
                }).execute();
            }
        }).execute();
    }

    static void doRetry(final boolean ui, final SharedPreferences config, final int retry, final String lastError) {
        if (sContext == null)
            return ;
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (wifiTest(ui, lastError)) {
                    doSync(ui, config, retry + 1);
                }
            }
        }).start();
    }

    static boolean wifiTest(boolean ui, final String lastError) {
        if (sContext == null)
            return false;
        try {
            WifiManager wifiManager = (WifiManager) sContext.getSystemService(Context.WIFI_SERVICE);
            int i = 0;
            while (i < 5 * 60) {
                if (wifiManager.isWifiEnabled() && isWifiConnect()) {
                    if (i == 0) {
                        //不是网络原因引起的错误
                        return false;
                    }
                    return true;
                } else {
                    if (ui && editProgress != null) {
                        //防止在线程下 画UI会崩溃 要做POST操作
                        final int finalI = i;
                        editProgress.post(new Runnable() {
                            @Override
                            public void run() {
                                editProgress.setText(lastError + "\r\nwifi failed!! wait WiFi connect:" + finalI);
                            }
                        });
                    }
                    i++;
                    Thread.sleep(1000);
                }
            }
        } catch (InterruptedException e) {
            Log.d("mainactivity", "wifiTest exception", e);
        }
        return false;
    }

    static boolean isWifiConnect() {

        if (sContext == null)
            return false;
        ConnectivityManager connectivityManager = (ConnectivityManager) sContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifiNetworkInfo.getState() == NetworkInfo.State.CONNECTED;
    }

    // 获取本地目录
    static List<String> GetAllLocalDir(SharedPreferences config) {
        List<String> temp = new LinkedList<>();
        String dir = GetLocalDir("DCIM/");
        if (!dir.isEmpty())
            temp.add(dir);
        dir = GetLocalDir("tencent/MicroMsg/WeiXin/");
        if (!dir.isEmpty())
            temp.add(dir);
        dir = GetLocalDir("Pictures/WeiXin/");
        if (!dir.isEmpty())
            temp.add(dir);
        dir = GetLocalDir("Pictures/Screenshots/");
        if (!dir.isEmpty())
            temp.add(dir);
        dir = GetLocalDir("Pictures/VideoEditor/");
        if (!dir.isEmpty())
            temp.add(dir);

        dir = GetLocalDir(config.getString("PathOne", null));
        if (!dir.isEmpty())
            temp.add(dir);
        dir = GetLocalDir(config.getString("PathTwo", null));
        if (!dir.isEmpty())
            temp.add(dir);
        dir = GetLocalDir(config.getString("PathThree", null));
        if (!dir.isEmpty())
            temp.add(dir);

        return temp;
    }
//
//    // 获取本地文件
//    List<java.io.File> GetAllLocalFiles() {
//        List<File> localFileList = GetLocalFiles("DCIM/");
//        List<java.io.File> temp;
//        temp = GetLocalFiles("tencent/MicroMsg/WeiXin/");
//        if (temp != null) {
//            localFileList.addAll(temp);
//        }
//        temp = GetLocalFiles("Picture/WeiXin/");
//        if (temp != null) {
//            localFileList.addAll(temp);
//        }
//        temp = GetLocalFiles(config.getString("PathOne", null));
//        if (temp != null) {
//            localFileList.addAll(temp);
//        }
//        temp = GetLocalFiles(config.getString("PathTwo", null));
//        if (temp != null) {
//            localFileList.addAll(temp);
//        }
//        temp = GetLocalFiles(config.getString("PathThree", null));
//        if (temp != null) {
//            localFileList.addAll(temp);
//        }
//
////        for ( java.io.File file : localFileList )
////        {
////            Log.d("mainactivity",file.getAbsolutePath());
////        }
//        return localFileList;
//    }
//
//    List<java.io.File> GetLocalFiles(String strPath) {
//        if (strPath == null)
//            return null;
//        if (strPath.isEmpty())
//            return null;
//        String dir = GetLocalDir(strPath);
//        if (dir.isEmpty())
//            return null;
//        File localDir = new File(dir);
//        //List<java.io.File> localFileList;
//        return (List<java.io.File>) FileUtils.listFiles(localDir, null, true);
////        for ( java.io.File file : localFileList )
////        {
////            Log.d("mainactivity",file.getAbsolutePath());
////        }
//    }

    static String getSDCardDir() {
        return Environment.getExternalStorageDirectory().toString();
    }

    static String GetLocalDir(String strPath) {
        if (strPath == null)
            return "";
        if (strPath.isEmpty())
            return "";
        String mSDCardurl = getSDCardDir();
        mSDCardurl = mSDCardurl + "/" + strPath;
        File file = new File(mSDCardurl);
        if (file.exists()) {
            Log.d("mainactivity", "found " + mSDCardurl);
            return mSDCardurl;
        } else {
            Log.d("mainactivity", "not found " + mSDCardurl);
            return "";
        }
    }

    static void ShowLastUploadTime(long lastUploadTime) {
        if (lastUploadTime == 0) {
            editLastUploadTime.setText("LastUploadTime:none");
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
            Date curDate = new Date(lastUploadTime);
            editLastUploadTime.setText("LastUploadTime" + formatter.format(curDate));
        }
    }

    private void alert(String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(R.string.error_title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }


}
