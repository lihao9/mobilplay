package com.lihao.mobilplay.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lihao.mobilplay.R;
import com.lihao.mobilplay.bean.MyVideo;
import com.lihao.mobilplay.tools.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by hbm on 2017/3/28.
 */

public class MyVideoAdapter extends BaseAdapter {
    private List<MyVideo> videos;
    private Utils util;
    private boolean isVideo;
    private boolean isNet;
    private Context context;
    public MyVideoAdapter(Context context,List<MyVideo> videos, boolean b,boolean isNet){
        this.videos = videos;
        this.isNet = isNet;
        isVideo = b;
        this.context = context;
        util = new Utils();
    }
    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return videos.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return position;
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHoulder houlder;
        if (isNet){
            //是网络
            if (isVideo){
                //是网络video
                if (convertView==null){
                    convertView = View.inflate(context, R.layout.item_net_video,null);
                    houlder = new ViewHoulder();
                    convertView.setTag(houlder);
                    houlder.iv_icon = (ImageView) convertView.findViewById(R.id.item_new_video_iv);
                    houlder.tv_name = (TextView) convertView.findViewById(R.id.item_net_video_tv_name);
                    houlder.tv_desc = (TextView) convertView.findViewById(R.id.item_net_video_tv_desc);
                }else {
                    houlder = (ViewHoulder) convertView.getTag();
                }
                houlder.tv_name.setText(videos.get(position).getName());
                Picasso.with(context).load(videos.get(position).getIvUri()).into(houlder.iv_icon);
                houlder.tv_desc.setText(videos.get(position).getDesc());
            }
        }else {
            //本地video
            if (convertView==null){
                convertView = View.inflate(context, R.layout.lv_item_video,null);
                houlder = new ViewHoulder();
                convertView.setTag(houlder);
                houlder.iv_icon = (ImageView) convertView.findViewById(R.id.item_video_image);
                houlder.tv_name = (TextView) convertView.findViewById(R.id.item_video_tvname);
                houlder.tv_size = (TextView) convertView.findViewById(R.id.item_video_tvsize);
                houlder.tv_time = (TextView) convertView.findViewById(R.id.item_video_tvtime);
            }else {
                houlder = (ViewHoulder) convertView.getTag();
            }
            if(isVideo){
                houlder.iv_icon.setImageResource(R.drawable.video_default_icon);
            }else {
                houlder.iv_icon.setImageResource(R.drawable.music_default_bg);
            }
            houlder.tv_name.setText(videos.get(position).getName());
            houlder.tv_time.setText(util.stringForTime((int) videos.get(position).getDuration()));
            houlder.tv_size.setText(Formatter.formatFileSize(context,videos.get(position).getSize()));
        }

        return convertView;
    }

    static class ViewHoulder{
        TextView tv_desc;
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_time;
        TextView tv_size;
    }
}
