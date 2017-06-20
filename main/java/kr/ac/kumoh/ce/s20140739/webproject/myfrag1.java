package kr.ac.kumoh.ce.s20140739.webproject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.Button;
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
import org.w3c.dom.Text;

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

public class myfrag1 extends Fragment implements AdapterView.OnItemClickListener {
    String result = "";
    protected ArrayList<listinfo> rArray = new ArrayList<listinfo>();


    protected ListView mList;
    protected AllAdapter mAdapter;
    protected RequestQueue mQueue = null;
    protected ImageLoader mImageLoader = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.myfrag1, container, false);
        rArray = new ArrayList<listinfo>();
        mAdapter = new AllAdapter(getActivity(), R.layout.listitem, rArray);
        mList = (ListView) rootView.findViewById(R.id.listview1);
        mList.setAdapter(mAdapter);
        mList.setOnItemClickListener(this);

        Cache cache = new DiskBasedCache(getActivity().getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        mQueue = new RequestQueue(cache, network);
        mQueue.start();
        mImageLoader = new ImageLoader(mQueue, new LruBitmapCache(LruBitmapCache.getCacheSize(getActivity())));
        back task = new back();
        task.execute("http://192.168.123.107:3008/video/all");
        return rootView;
    }

    private class back extends AsyncTask<String, Integer, String> {
        @Override
        public String doInBackground(String... urls) {


            try {
                URL myFileUrl = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                conn.setRequestMethod("GET");

                if (Login.cookieString != "")
                    conn.setRequestProperty("Cookie", Login.cookieString);
                conn.setDoInput(true);

                conn.connect();


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
                    String id = jsonChildNode.getString("id");

                    String image = jsonChildNode.getString("thumbnail");

                    String name = jsonChildNode.getString("title");


                    rArray.add(new listinfo(id, image, name));
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

            return result;
        }
    }

    private class front extends AsyncTask<String, Integer, String> {
        @Override
        public String doInBackground(String... urls) {


            try {
                URL myFileUrl = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                conn.setRequestMethod("GET");

                if (Login.cookieString != "")
                    conn.setRequestProperty("Cookie", Login.cookieString);
                conn.setDoInput(true);

                conn.connect();


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


        private String convertInputStreamToString(InputStream inputStream) throws IOException {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line = "";
            String result = "";
            while ((line = bufferedReader.readLine()) != null)
                result += line;
            inputStream.close();

            return result;
        }
    }

    public class listinfo {
        String id;
        String image;
        String name;


        public listinfo(String id, String image, String name) {
            this.id = id;
            this.image = image;
            this.name = name;

        }

        public String getId() {
            return id;
        }

        public String getImage() {
            return image;
        }

        public String getName() {
            return name;
        }


    }

    static class RoomViewHolder {
        TextView title;

        NetworkImageView imimage;
        Button heart;
        Button check;
    }

    public class AllAdapter extends ArrayAdapter<listinfo> {

        public AllAdapter(Context context, int resource, List<listinfo> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            RoomViewHolder holder;

            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.listitem, parent, false);
                holder = new RoomViewHolder();
                holder.title = (TextView) convertView.findViewById(R.id.name);
                holder.imimage = (NetworkImageView) convertView.findViewById(R.id.thumnail);
                holder.heart = (Button) convertView.findViewById(R.id.heart);
                holder.heart.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        String id = getItem(position).getId();
                        front tsk = new front();
                        tsk.execute("http://192.168.123.107:3008/video/love/" + id);
                        Toast.makeText(getActivity(), "♡버튼을 눌렀습니다", Toast.LENGTH_SHORT).show();

                    }
                });
                holder.check = (Button) convertView.findViewById(R.id.check);

                holder.check.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        String id = getItem(position).getId();

                        front tk = new front();
                        tk.execute("http://192.168.123.107:3008/video/check/" + id);
                        Toast.makeText(getActivity(), "Check버튼을 눌렀습니다", Toast.LENGTH_SHORT).show();

                    }
                });
                convertView.setTag(holder);

            } else {
                holder = (RoomViewHolder) convertView.getTag();
            }
            holder.title.setText(getItem(position).getName());
            holder.imimage.setImageUrl(getItem(position).getImage(), mImageLoader);

            return convertView;
        }
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {

        String id = mAdapter.getItem(pos).getId();
        Intent intent = new Intent(getActivity(), myfrag1_1.class);
        intent.putExtra("videoId", id);
        startActivity(intent);


    }


}
