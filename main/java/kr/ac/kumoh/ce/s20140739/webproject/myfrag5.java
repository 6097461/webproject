package kr.ac.kumoh.ce.s20140739.webproject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 60974 on 2017-05-30.
 */

public class myfrag5 extends Fragment {
    String result = "";
    protected ArrayList<checklistinfo> rArray = new ArrayList<checklistinfo>();
    protected ListView mList;
    protected CheckAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.myfrag5, container, false);
        rArray = new ArrayList<checklistinfo>();
        mAdapter = new CheckAdapter(getActivity(), R.layout.listitem3, rArray);
        mList = (ListView) rootView.findViewById(R.id.listview5);
        mList.setAdapter(mAdapter);

        back task = new back();
        task.execute("http://192.168.123.107:3008/note");
        return  rootView;
    }
    private class back extends AsyncTask<String, Integer, String> {
        @Override
        public String doInBackground(String... urls) {
            Log.i("task", "실행?");

            try {
                URL myFileUrl = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                conn.setRequestMethod("GET");
                if (Login.cookieString != "")
                    conn.setRequestProperty("Cookie", Login.cookieString);
                conn.setDoInput(true);

                Log.i("task", "연결?");
                conn.connect();
                Log.i("task", "연결!");

                Log.i("task", "비트맵?");
                InputStream inputStream = conn.getInputStream();

                if (inputStream != null)
                    result = convertInputStreamToString(inputStream);
                else
                    result = "Did not work!";

            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String str) {
            try {
                JSONObject jsResult = new JSONObject(str);
                JSONArray jsonMainNode = jsResult.getJSONArray("list");
                for (int i = 0; i < jsonMainNode.length(); i++) {
                    JSONObject jsonChildNode = jsonMainNode.getJSONObject(i);
                    String note= jsonChildNode.getString("note");

                    rArray.add(new checklistinfo(note));
                }
                mAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                Toast.makeText(getActivity(), "Error" + e.toString(), Toast.LENGTH_LONG).show();
            }
        }

        private String convertInputStreamToString(InputStream inputStream) throws IOException {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line = "";
            String result = "";
            while ((line = bufferedReader.readLine()) != null)
                result += line;
            inputStream.close();
            Log.i("실행", "완료");
            return result;
        }
    }
    public class checklistinfo {
     String note;

        public checklistinfo(String note) {
            this.note=note;

        }



        public String getNote() {
            return note;
        }



    }
    static class CheckViewHolder {
        TextView note;

    }

    public class CheckAdapter extends ArrayAdapter<checklistinfo> {

        public CheckAdapter(Context context, int resource, List<checklistinfo> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CheckViewHolder holder;
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.listitem3, parent, false);
                holder = new CheckViewHolder();
                holder.note = (TextView) convertView.findViewById(R.id.note);

                convertView.setTag(holder);

            } else {
                holder = (CheckViewHolder) convertView.getTag();
            }
            holder.note.setText(getItem(position).getNote());

            return convertView;
        }
    }


}
