package com.quickdraw.quickdrawatm;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {
    NfcAdapter nfcAdpt;
    PendingIntent pendingIntent;
    IntentFilter writeTagFilters[];
    TextView tv;

    // Used to load the 'native-lib' library on application startup.

    //Write ATM code here
    void processMessage(String message) {
        Intent intent = new Intent(MainActivity.this, NFCTransaction.class);
        intent.putExtra("Quickdraw Transaction Details", message);
        tv.setText(tv.getText() + "\n" + message);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.rgb(61, 61, 101));
        }

        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.textView2);
        tv.setText("ANDROID BEAM\nTRANSACTION HERE");

        /* NFC */
        nfcAdpt = NfcAdapter.getDefaultAdapter(this);
        // Check if NFC is available on device, abort if not found
        if (nfcAdpt == null) {
            Toast.makeText(this, "NFC not supported", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        // Check if NFC is enabled
        if (!nfcAdpt.isEnabled()) {
            Toast.makeText(this, "Enable NFC before using the app", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        //Pending indent
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        writeTagFilters = new IntentFilter[] {tagDetected};
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent foundIntent = getIntent();

        // Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(foundIntent.getAction())) {
            setIntent(new Intent());
            processIntent(foundIntent);
        }
    }

    void processIntent(Intent intent) {
        Tag readTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        Ndef ndef = Ndef.get(readTag);
        NdefMessage ndefMessage = ndef.getCachedNdefMessage();

        NdefRecord[] records = ndefMessage.getRecords();
        for (NdefRecord ndefRecord : records) {
            try {
                String inputText = readText(ndefRecord);

                processMessage(inputText);
            } catch (UnsupportedEncodingException e) {

            }
        }
    }

    private String readText(NdefRecord record) throws UnsupportedEncodingException {
        byte[] payload = record.getPayload();

        return new String(payload);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
