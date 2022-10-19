package com.zog.android.syncsmb.smbtools;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

public class SMBServerConnectV2 extends AsyncTask<String, String, SMBTools> {

    private final AsyncResponse delegate;

    private String error = "";
    private String server;
    private String username;
    private String password;
    private String share;
    private String domain;

    public interface AsyncResponse {
        void processFinish(SMBTools smb, String error);
    }

    public SMBServerConnectV2(String server, String username, String password, String share, String domain, AsyncResponse delegate) {
        this.delegate = delegate;
        this.server = server;
        this.username = username;
        this.password = password;
        this.share = share;
        this.domain = domain;
    }

    @Override
    protected SMBTools doInBackground(String... nullString) {
        try {
            SMBTools smb = new SMBTools();
            smb.connect(server, username, password, domain);
            smb.openShare(share);

            return smb;
        } catch (Exception ex) {
            Log.e("catch", ex.toString());
            error = ex.toString();
            return null;
        }
    }

    @Override
    protected void onPostExecute(SMBTools result) {
        delegate.processFinish(result, error);
    }

}
