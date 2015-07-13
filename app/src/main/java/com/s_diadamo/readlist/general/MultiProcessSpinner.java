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
    private String completeMessage;

    private MultiProcessSpinner() {
        this.runningThreads = 0;
    }

    public static MultiProcessSpinner getInstance() {
        if (instance == null) {
            instance = new MultiProcessSpinner();
        }
        return instance;
    }

    public void setInfo(Context context, String syncMessage, String completeMessage) {
        if (instance != null) {
            instance.context = context;
            instance.syncMessage = syncMessage;
            instance.completeMessage = completeMessage;
        }
    }

    public void addThread() {
        runningThreads++;
        if (runningThreads == 1) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(syncMessage);
            progressDialog.show();
        }
    }

    public void endThread() {
        runningThreads--;
        if (runningThreads == 0) {
            progressDialog.dismiss();
            Toast.makeText(context, completeMessage, Toast.LENGTH_SHORT).show();
        }
    }
}
