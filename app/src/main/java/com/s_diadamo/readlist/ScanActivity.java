package com.s_diadamo.readlist;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.s_diadamo.readlist.book.BookAdapter;
import com.s_diadamo.readlist.book.BookFragment;
import com.s_diadamo.readlist.book.BookOperations;
import com.s_diadamo.readlist.search.Search;


public class ScanActivity extends Activity {

    private BookAdapter bookAdapter;
    private BookOperations bookOperations;
    private Context context;

    public ScanActivity() {
    }

    public ScanActivity(Context context, BookAdapter bookAdapter, BookOperations bookOperations) {
        this.context = context;
        this.bookAdapter = bookAdapter;
        this.bookOperations = bookOperations;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        integrator.setOrientationLocked(true);
        integrator.setPrompt("");
        integrator.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Scan failed", Toast.LENGTH_LONG).show();
            } else {
                String bookISBN = result.getContents();
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("BOOK_ISBN", bookISBN);
                startActivity(intent);
            }
        }
    }
}
