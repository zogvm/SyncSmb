package com.zog.android.syncsmb.smbtools;

import android.os.AsyncTask;

import java.io.IOException;

import android.util.Log;

public class SMBDeleteRemoteFile extends AsyncTask<String, String, Boolean> {

    private final AsyncResponse delegate;

    private String error = "";
    private final SMBTools smb;

    public interface AsyncResponse {
        void processFinish(boolean success, String error);
    }

    public SMBDeleteRemoteFile(SMBTools smb, AsyncResponse delegate) {
        this.smb = smb;
        this.delegate = delegate;
    }

    @Override
    protected Boolean doInBackground(String... remotePath)  {
        Log.d("SMBDeleteRemoteFile:",remotePath[0]);
        try {
            smb.deleteRemote(remotePath[0]);
            return true;
        } catch (IOException ex) {
            error = ex.toString();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean success) {
        delegate.processFinish(success,error);
    }

}
