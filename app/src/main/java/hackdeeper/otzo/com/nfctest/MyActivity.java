package hackdeeper.otzo.com.nfctest;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class MyActivity extends Activity
{
    private IntentFilter[] intentFiltersArray;
    private PendingIntent pendingIntent;
    private String[][] techListsArray;
    private NfcAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        mAdapter = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        techListsArray = new String[][]{new String[]{MifareClassic.class.getName()}};
    }


    public void onPause()
    {
        super.onPause();
        mAdapter.disableForegroundDispatch(this);
    }

    public void onResume()
    {
        System.out.println("onResume");
        super.onResume();
        mAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray);
    }

    public void onNewIntent(Intent intent)
    {
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

        int smallSectorCount = 32;
        int largeSectorCount = 8;
        int totalSectorCount = smallSectorCount + largeSectorCount;
        int blockSizeBefore32 = 4;
        int blockSizeAfter32 = 16;
        int blockIndex = 0;

        for (int sectorIndex = 0; sectorIndex < totalSectorCount; sectorIndex++) {
            System.out.println("Sector : " + sectorIndex + " \r\n");
            //Sector Title
            if (sectorIndex < smallSectorCount) {
                //Sector contains 4 blocks
                for (int i = 0; i < blockSizeBefore32; i++) {
                    System.out.println("sectorIndex : " + sectorIndex + " blockIndex : " + blockIndex + "\r\n");
                    String allContent = NFCUltralightUtility.readTag(tagFromIntent, sectorIndex, blockIndex);
                    System.out.println(allContent + "\r\n");
                    blockIndex++;
                }
            } else if (sectorIndex >= smallSectorCount) {
                //Sector contains 16 blocks
                for (int i = 0; i < blockSizeAfter32; i++) {
                    System.out.println("sectorIndex : " + sectorIndex + " blockIndex : " + blockIndex + "\r\n");
                    String allContent = NFCUltralightUtility.readTag(tagFromIntent, sectorIndex, blockIndex);
                    System.out.println(allContent + "\r\n");
                    blockIndex++;
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
