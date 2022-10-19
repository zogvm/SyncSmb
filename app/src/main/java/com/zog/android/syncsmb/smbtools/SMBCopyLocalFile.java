package com.zog.android.syncsmb.smbtools;

import android.os.AsyncTask;

import java.io.File;
import java.io.IOException;


import android.util.Log;
public class SMBCopyLocalFile extends AsyncTask<File, String, Boolean> {

    private final AsyncResponse delegate;

    private String error = "";
    private final SMBTools smb;
    private final String destinationPath;

    public interface AsyncResponse {
        void processFinish(boolean success, String error);
    }

    public SMBCopyLocalFile(SMBTools smb, String destinationPath, AsyncResponse delegate) {
        this.smb = smb;
        this.destinationPath = destinationPath;
        this.delegate = delegate;
    }

    @Override
    protected Boolean doInBackground(File... localFile)  {
           Log.d("SMBCopyLocalFile:","["+localFile[0].getAbsolutePath()+"]to["+destinationPath+"]");
        try {
            smb.copyToRemote(localFile[0],destinationPath);
            //smb.copyToRemote(localFile,"assinados\\" + getIntent().getExtras().getString("source"));
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
