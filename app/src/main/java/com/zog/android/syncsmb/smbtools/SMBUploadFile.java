package com.zog.android.syncsmb.smbtools;

import android.os.AsyncTask;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;


import android.util.Log;

import org.apache.commons.io.FileUtils;

public class SMBUploadFile extends AsyncTask<String, String, Boolean> {

    private final AsyncResponse delegate;

    private String error = "";
    private final SMBTools smb;
    private final String localRootDir;
    private final String remoteRootDir;
    private final List<String> localDirs;

    //回调协议
    public interface AsyncResponse {
        void processFinish(boolean success, String error);

        void progressUpdate(String progress);
    }

    //入口
    public SMBUploadFile(SMBTools smb, String localRootDir, List<String> localDirs, String remoteRootDir, AsyncResponse delegate) {
        this.smb = smb;
        this.localRootDir = localRootDir;
        this.localDirs = localDirs;
        this.remoteRootDir = remoteRootDir;
        this.delegate = delegate;
    }

    //后台执行主体
    @Override
    protected Boolean doInBackground(String... nullString) {
        if (localDirs.isEmpty()) {
            error = "localDirs is null";
            return false;
        }

        //遍历目录
        List<java.io.File> localFiles = new LinkedList<>();
        for (String dir : localDirs) {
            if (dir.isEmpty())
                continue;
            File localDir = new File(dir);
            List<java.io.File> t = (List<java.io.File>) FileUtils.listFiles(localDir, null, true);
            if (t != null) {
                localFiles.addAll(t);
            }
        }

        if (localFiles.isEmpty()) {
            error = "localFiles is null";
            return false;
        }

        try {
            //创建远端文件夹
            smb.createDir(remoteRootDir);

            long i = 0;
            long total = localFiles.size();
            //循环
            for (java.io.File file : localFiles) {
                String tempLocalpath = file.getAbsolutePath();
                long tempLocalSize = file.length();
                String tempRemoteParent = file.getParent().replace(localRootDir, remoteRootDir);
                String tempRemotePath = tempLocalpath.replace(localRootDir, remoteRootDir);

                if (tempLocalpath.contains("DCIM/.thumbnails/")
                        || tempLocalpath.contains("DCIM/.android/")
                        || tempLocalpath.contains("DCIM/.tmfs/")
                        || tempLocalpath.contains("DCIM/Camera/.escheck.tmp")
                        || tempLocalpath.contains("DCIM/.globalTrash")
                        || tempLocalpath.contains("DCIM/.")
                        || tempLocalpath.contains("DCIM/Creative/temp")
                        || tempLocalpath.contains("DCIM/Camera/cache/")) {
                    i++;
                    publishProgress(i + "/" + total + ":" + tempRemotePath);
                    continue;
                }

                boolean needCopy = true;
                //判断本地尺寸和远端尺寸是否一致
                long tempRemoteSize = smb.fileSize(tempRemotePath);
                if (tempRemoteSize == tempLocalSize && tempRemoteSize > 0) {
                    needCopy = false;
                }

                Log.d("SMBUploadFile", tempLocalpath + " ][ " + tempRemoteParent + " ][ " + tempRemotePath + "] rs:" + tempRemoteSize + " ls:" + tempLocalSize);

                //上传到远端
                if (needCopy) {
                    smb.createAllDir(tempRemoteParent);
                    smb.copyToRemote(file, tempRemotePath);
                }

                i++;
                publishProgress(i + "/" + total + ":" + tempRemotePath);
            }
        } catch (IOException ex) {
            error = ex.toString();
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        delegate.processFinish(success, error);
    }

    @Override
    protected void onProgressUpdate(String... progresses) {
        delegate.progressUpdate(progresses[0]);
    }

}
