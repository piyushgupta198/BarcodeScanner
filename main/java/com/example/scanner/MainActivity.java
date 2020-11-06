package com.example.scanner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    TextView textView;
    ImageView copyButton;
    String result;
    Button openBrowser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView) findViewById(R.id.imageView);
        textView = (TextView) findViewById(R.id.textView);
        copyButton = (ImageView) findViewById(R.id.copyButton);
        result = null;
        openBrowser = (Button) findViewById(R.id.button2);
        copyButton.setVisibility(View.INVISIBLE);
        openBrowser.setVisibility(View.INVISIBLE);
        textView.setMovementMethod(new ScrollingMovementMethod());
    }

    public void scan(View view){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK){
            try {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");

                imageView.setImageBitmap(bitmap);

                BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this)
                        .setBarcodeFormats(Barcode.CODE_39 | Barcode.UPC_E | Barcode.UPC_A | Barcode.QR_CODE | Barcode.DATA_MATRIX | Barcode.CODE_39 | Barcode.CODE_93)
                        .build();

                if (!barcodeDetector.isOperational()) {
                    textView.setText("Could not set up the detector!");
                    return;
                }

                Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                SparseArray<Barcode> sparseArray = barcodeDetector.detect(frame);

                Barcode barcode = sparseArray.valueAt(0);
                textView.setText(barcode.rawValue);
                result = textView.getText().toString();
                copyButton.setVisibility(View.VISIBLE);
                openBrowser.setVisibility(View.VISIBLE);
            }catch (Exception e){
                textView.setText("Sorry! we are unable to read, Can you plese try again.");
                e.printStackTrace();
            }
        }
    }

    public void copy(View view){
        try {
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("data", result);
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(this, "Copied!!", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void openBrowser(View view){

        try {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(result));
            startActivity(i);
        }catch (Exception w){
            w.printStackTrace();
            Toast.makeText(this, "It cannot open in browser", Toast.LENGTH_SHORT);
        }
    }
}