package com.s_diadamo.readlist.book;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.s_diadamo.readlist.R;
import com.s_diadamo.readlist.general.SyncData;
import com.s_diadamo.readlist.general.Utils;
import com.s_diadamo.readlist.shelf.Shelf;
import com.s_diadamo.readlist.shelf.ShelfOperations;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BookManualAddActivity extends AppCompatActivity {

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final String IMAGE_DIRECTORY_NAME = "readlist_bookcovers";
    private Shelf shelf;
    private Uri fileUri;
    private ImageView bookImageCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manually_add_book);

        bookImageCamera = (ImageView) findViewById(R.id.manual_add_cover_photo);

        setUpShelf();

        bookImageCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                if (context.getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                    captureImage();
                } else {
                    Toast.makeText(context, "Camera is not supported on this device.", Toast.LENGTH_LONG).show();
                }
            }
        });

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.ActionBarColor)));
        actionBar.show();
        actionBar.setTitle("Add Book");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 8;
                final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);
                ImageView bookCover = (ImageView) findViewById(R.id.manual_add_book_background);
                bookCover.setImageBitmap(bitmap);
                bookImageCamera.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_general_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                addBook();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addBook() {
        final EditText bookTitleEditText = (EditText) findViewById(R.id.manual_add_title);
        final EditText bookAuthorEditText = (EditText) findViewById(R.id.manual_add_author);
        final EditText bookPagesEditText = (EditText) findViewById(R.id.manual_add_pages);
        String bookTitle = bookTitleEditText.getText().toString().trim();
        String bookAuthor = bookAuthorEditText.getText().toString().trim();
        String pages = bookPagesEditText.getText().toString();

        if (bookTitle.isEmpty()) {
            Toast.makeText(this, "Please add a title", Toast.LENGTH_LONG).show();
        } else {
            pages = pages.isEmpty() ? "0" : pages;
            bookAuthor = bookAuthor.isEmpty() ? "" : bookAuthor;
            Book book;
            if (fileUri != null) {
                book = new Book(bookTitle, bookAuthor, shelf.getId(), Utils.getCurrentDate(), Integer.parseInt(pages), 0, shelf.getColour(), 0, "", fileUri.getPath());
            } else {
                book = new Book(bookTitle, bookAuthor, shelf.getId(), Utils.getCurrentDate(), Integer.parseInt(pages), 0, shelf.getColour(), 0, "", "");
            }

            if (Utils.checkUserIsLoggedIn(this)){
                new SyncData(this).syncBook(book);
            }

            new BookOperations(this).addBook(book);
            finish();
        }
    }

    private File getOutputMediaFile() {
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());

        return new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
    }

    private void setUpShelf() {
        Bundle args = getIntent().getExtras();
        String stringShelfId = "";
        int shelfId;
        if (args != null) {
            stringShelfId = args.getString(Shelf.SHELF_ID);
        }
        if (!stringShelfId.isEmpty()) {
            shelfId = Integer.parseInt(stringShelfId);
        } else {
            shelfId = Shelf.DEFAULT_SHELF_ID;
        }

        shelf = new ShelfOperations(this).getShelf(shelfId);
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    private Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
    }
}

