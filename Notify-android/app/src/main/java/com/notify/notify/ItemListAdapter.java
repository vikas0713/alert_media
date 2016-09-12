package com.notify.notify;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.notify.network.LoopjClient;
import com.notify.network.loopjResponseHandler;
import com.notify.storage.PreferenceHelper;
import com.notify.utils.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

/**
 * Created by K2A on 10/09/16.
 */
public class ItemListAdapter extends ArrayAdapter {

    private Context context;
    private ArrayList<HashMap<String, Object>> itemList;
    private PreferenceHelper preference;

    public ItemListAdapter(Context context, int resource, ArrayList<HashMap<String, Object>> objects) {
        super(context, resource);
        this.context = context;
        this.itemList = objects;
    }

    class ViewHolder {
        public TextView description;
        public TextView location;
        public ImageView post_image;
        public ImageView like;
        public TextView like_count;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        preference = new PreferenceHelper(context);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, null);
            holder = new ViewHolder();
            holder.description = (TextView) convertView.findViewById(R.id.description);
            holder.post_image = (ImageView) convertView.findViewById(R.id.post_image);
            holder.location = (TextView) convertView.findViewById(R.id.location);
            holder.like = (ImageView) convertView.findViewById(R.id.like);
            holder.like_count = (TextView) convertView.findViewById(R.id.like_count);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final HashMap<String, Object> jObject = itemList.get(position);

        holder.description.setText(String.valueOf(jObject.get("description")));
        holder.location.setText(String.valueOf(jObject.get("location")));
        holder.like_count.setText(String.valueOf(jObject.get("like_count")));
        if(!jObject.get("image_url").toString().equals("")){
            Picasso.with(context).load(jObject.get("image_url").toString()).placeholder(R.drawable.progress_animation ).into(holder.post_image);
        }

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject params = new JSONObject();
                try {
                    params.put("post_id",String.valueOf(jObject.get("post_id")));
                    params.put("user_id",preference.getStringValue("user_id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                LoopjClient.post(context, "api/upvote", params, new loopjResponseHandler(context) {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);

                        try {
                            if(response.getString("status").equals("201")){
                                Toast.makeText(context,response.getString("msg"),Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                holder.like_count.setText(String.valueOf(Integer.parseInt(jObject.get("like_count").toString())+1));
                                Toast.makeText(context,"UpVoted !!",Toast.LENGTH_LONG).show();
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                });
            }
        });

        return convertView;
    }
}
