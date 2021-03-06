package com.twitter.terngame.util;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by jchong on 1/26/14.
 */

public class JSONFileReaderTask extends AsyncTask<InputStream, Void, JSONObject > {

    public interface JSONFileReaderCompleteListener {
        public void onJSONFileReaderComplete();
    }

    private JSONObject mData;
    private JSONFileResultHandler mResultHandler;
    private JSONFileReaderCompleteListener mCompleteListener;

    public JSONFileReaderTask(JSONFileResultHandler resultHandler,
            JSONFileReaderCompleteListener jFRCL) {
        mData = null;
        mResultHandler = resultHandler;
        mCompleteListener = jFRCL;
    }

    protected JSONObject doInBackground(InputStream... in) {
        try {
            String dataString = FullFileReader.readFully(in[0], "UTF-8");
            //        Log.d("terngame", dataString);

            mData = new JSONObject(dataString);
            return mData;

        } catch (FileNotFoundException e) {
            Log.e("terngame", "JSONFileReaderTask: File not found");
        } catch (IOException e) {
            Log.e("terngame", "IOException trying to read in a JSON File");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected void onPostExecute(JSONObject jo) {
        mResultHandler.saveResult(jo);
        if (mCompleteListener != null) {
            mCompleteListener.onJSONFileReaderComplete();
        }
    }
}
