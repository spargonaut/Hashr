package com.misker.mike.hasher;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.Adler32;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;


/**
 * Created by Mike Bailey on 12/9/16.
 * Class for calculating hashes of Strings or file-like objects
 */

class HashRunnable extends AsyncTask<Uri, Void, String> {

    private final MainView mainView;
    private String output = "Nice job buddy, you broke my code!";
    private final String type;
    private String toHash;
    private ContentResolver cr;


    public HashRunnable(String hashtype, ContentResolver cr, MainView mainView){
        type = hashtype;
        this.cr = cr;
        this.mainView = mainView;
    }

    public HashRunnable(String hashtype, String toHash, MainView mainView){
        type = hashtype;
        this.toHash = toHash;
        this.mainView = mainView;
    }

    @Override
    public void onPreExecute() {
        mainView.displayWaitProgress();
    }

    @Override
    public void onPostExecute(String result) {
        mainView.displayResults(result);
    }

    @Override
    protected String doInBackground(Uri... uris) {
        if(cr != null) {
            try {
                InputStream is = cr.openInputStream(uris[0]);

                if(is == null)
                    throw new IOException();

                switch (type) {
                    case "MD5":
                        output = new String(Hex.encodeHex(DigestUtils.md5(is)));
                        break;
                    case "SHA1":
                        output = new String(Hex.encodeHex(DigestUtils.sha1(is)));
                        break;
                    case "SHA256":
                        output = new String(Hex.encodeHex(DigestUtils.sha256(is)));
                        break;
                    case "SHA384":
                        output = new String(Hex.encodeHex(DigestUtils.sha384(is)));
                        break;
                    case "CRC32b": {
                        //Thanks to Redditor /u/thurask for finding a bug here!
                        CheckedInputStream cis = new CheckedInputStream(is, new CRC32());
                        byte[] tempBuf = new byte[128];
                        //noinspection StatementWithEmptyBody
                        while (cis.read(tempBuf) >= 0);
                        long checksum = cis.getChecksum().getValue();
                        output = Long.toHexString(checksum);
                        break;
                    }
                    case "Adler32": {
                        CheckedInputStream cis = new CheckedInputStream(is, new Adler32());
                        byte[] tempBuf = new byte[128];
                        //noinspection StatementWithEmptyBody
                        while (cis.read(tempBuf) >= 0);
                        long checksum = cis.getChecksum().getValue();
                        output = Long.toHexString(checksum);
                        break;
                    }
                    default:
                        output = new String(Hex.encodeHex(DigestUtils.sha512(is)));
                        break;
                }
            } catch (IOException e) {
                Log.e("FileDebug", e.getMessage());
            }
        }
        else{
            try {
                InputStream is = new ByteArrayInputStream(toHash.getBytes("UTF-8"));

                switch (type) {
                    case "MD5":
                        output = new String(Hex.encodeHex(DigestUtils.md5(is)));
                        break;
                    case "SHA1":
                        output = new String(Hex.encodeHex(DigestUtils.sha1(is)));
                        break;
                    case "SHA256":
                        output = new String(Hex.encodeHex(DigestUtils.sha256(is)));
                        break;
                    case "SHA384":
                        output = new String(Hex.encodeHex(DigestUtils.sha384(is)));
                        break;
                    case "CRC32b": {
                        //Thanks to Redditor /u/thurask for finding a bug here!
                        CheckedInputStream cis = new CheckedInputStream(is, new CRC32());
                        byte[] tempBuf = new byte[128];
                        //noinspection StatementWithEmptyBody
                        while (cis.read(tempBuf) >= 0);
                        long checksum = cis.getChecksum().getValue();
                        output = Long.toHexString(checksum);
                        break;
                    }
                    case "Adler32": {
                        CheckedInputStream cis = new CheckedInputStream(is, new Adler32());
                        byte[] tempBuf = new byte[128];
                        //noinspection StatementWithEmptyBody
                        while (cis.read(tempBuf) >= 0);
                        long checksum = cis.getChecksum().getValue();
                        output = Long.toHexString(checksum);
                        break;
                    }
                    default:
                        output = new String(Hex.encodeHex(DigestUtils.sha512(is)));
                        break;
                }
            }
            catch (IOException e){
                Log.e("FileDebug", e.getMessage());
            }
        }

        return output;
    }

}
