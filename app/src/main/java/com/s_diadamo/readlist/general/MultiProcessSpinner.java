package com.s_diadamo.readlist.general;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

public class MultiProcessSpinner {

    private static MultiProcessSpinner instance = null;
    private Context context;
    private int runningThreads;
    private ProgressDialog progressDialog;
    private String syncMessage;

    private MultiProcessSpinner() {
        this.runningThreads = 0;
    }

    public static MultiProcessSpinner getInstance() {
        if (instance == null) {
            instance = new MultiProcessSpinner();
        }
        return instance;
    }

    public void setInfo(Context context, String syncMessage) {
        if (instance != null) {
            instance.context = context;
            instance.syncMessage = syncMessage;
        }
    }

    public void addThread() {
        runningThreads++;
        if (runningThreads == 1) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(syncMessage);
            progressDialog.setCancelable(true);
            progressDialog.setCanceledOnTouchOutside(true);
            progressDialog.show();
        }
    }

    public void endThread() {
        runningThreads--;
        if (runningThreads == 0) {
            progressDialog.dismiss();
        }
    }
}
