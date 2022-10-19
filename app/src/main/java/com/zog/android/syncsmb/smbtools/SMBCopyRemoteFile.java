package com.zog.android.syncsmb.smbtools;

import android.os.AsyncTask;

import java.io.File;
import java.io.IOException;

import android.util.Log;

public class SMBCopyRemoteFile extends AsyncTask<String, String, String> {

    private final AsyncResponse delegate;

    private boolean error = false;
    private final SMBTools smb;

    public interface AsyncResponse {
        void processFinish(String output,boolean error);
    }

    public SMBCopyRemoteFile(SMBTools smb, AsyncResponse delegate) {
        this.smb = smb;
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(String... remoteFile)  {

        Log.d("SMBCopyRemoteFile:",remoteFile[0]);
        try {
            File tmpFile = smb.copyToLocal(remoteFile[0], ".pdf"); //gqtodo
            return tmpFile.getAbsolutePath();
        } catch (IOException ex) {
            error = true;
            return ex.toString();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        delegate.processFinish(result,error);
    }

}
