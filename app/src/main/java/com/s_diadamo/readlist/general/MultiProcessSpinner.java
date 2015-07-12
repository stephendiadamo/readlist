package com.s_diadamo.readlist.general;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

public class MultiProcessSpinner {
    private Context context;
    private int runningThreads;
    private ProgressDialog progressDialog;
    private String syncMessage;
    private String completeMessage;


    public MultiProcessSpinner(Context context, String syncMessage, String completeMessage) {
        this.context = context;
        this.runningThreads = 0;
        this.syncMessage = syncMessage;
        this.completeMessage = completeMessage;
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
            Toast.makeText(context, completeMessage, Toast.LENGTH_LONG).show();
        }
    }
}
