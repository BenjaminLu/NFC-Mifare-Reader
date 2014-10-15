package hackdeeper.otzo.com.nfctest;

import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.util.Log;

import java.io.IOException;
import java.nio.charset.Charset;

public class NFCUltralightUtility
{
    private static final String TAG = NFCUltralightUtility.class.getSimpleName();

    public static void writeTag(Tag tag, String tagText)
    {
        MifareUltralight ultralight = MifareUltralight.get(tag);
        try {
            ultralight.connect();
            ultralight.writePage(4, "abcd".getBytes(Charset.forName("US-ASCII")));
            ultralight.writePage(5, "efgh".getBytes(Charset.forName("US-ASCII")));
            ultralight.writePage(6, "ijkl".getBytes(Charset.forName("US-ASCII")));
            ultralight.writePage(7, "mnop".getBytes(Charset.forName("US-ASCII")));
        } catch (IOException e) {
            Log.e(TAG, "IOException while closing MifareUltralight...", e);
        } finally {
            try {
                ultralight.close();
            } catch (IOException e) {
                Log.e(TAG, "IOException while closing MifareUltralight...", e);
            }
        }
    }

    public static String readTag(Tag tag, int sectorIndex, int blockIndex)
    {
        MifareClassic mfc = MifareClassic.get(tag);
        String blockData = null;
        try {
            mfc.connect();
            boolean auth = false;

            auth = mfc.authenticateSectorWithKeyA(sectorIndex, NFCAuthKey.keyA);

            if (auth) {
                byte[] payload = mfc.readBlock(blockIndex);
                blockData = byteAryToHexString(payload);
            } else {
                throw new IOException("Authenticate Fail.");
            }

        } catch (IOException e) {
            Log.e(TAG, "IOException while writing MifareClassic message...", e);
            blockData = "fail to read from card";
        } finally {
            try {
                mfc.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing tag...", e);
            }
        }
        return blockData;
    }

    public static byte[] hexStringToByteArray(String s)
    {
        int len = s.length();
        byte[] byteAry = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            byteAry[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
                    .digit(s.charAt(i + 1), 16));
        }
        return byteAry;
    }

    public static String byteAryToHexString(byte byteAry[])
    {
        StringBuffer sb = new StringBuffer(byteAry.length * 2);
        int i;
        for (i = 0; i < byteAry.length; i++) {
            long longData = (int) byteAry[i] & 0xff;
            if (longData < 0x10) {
                sb.append("0");
            }
            sb.append(Long.toString(longData, 16));
        }
        return sb.toString();
    }
}
