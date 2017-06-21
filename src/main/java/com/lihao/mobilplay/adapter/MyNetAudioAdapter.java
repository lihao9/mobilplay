package com.lihao.mobilplay.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.felipecsl.gifimageview.library.GifImageView;
import com.lihao.mobilplay.R;
import com.lihao.mobilplay.bean.AudioData;
import com.lihao.mobilplay.customview.JCVideoPlayerStandard;
import com.lihao.mobilplay.tools.DensityUtil;
import com.lihao.mobilplay.tools.Utils;
import com.squareup.picasso.Picasso;

import java.util.List;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;


/**
 * Created by hbm on 2017/4/11.
 */

public class MyNetAudioAdapter extends BaseAdapter{
    private static final int TYPE_IMAGE = 1;
    private static final int TYPE_TEXT = 2;
    private static final int TYPE_GIF = 3;
    private static final int TYPE_AD = 4;
    private Utils utils;
    private Context context;
    private List<AudioData.ListBean> datas;
    private static final int  TYPE_VIDEO = 0;
    public MyNetAudioAdapter(Context content,List<AudioData.ListBean> datas){
        this.context = content;
        this.datas = datas;
        utils = new Utils();
    }

    public int getItemViewType(int position) {

        AudioData.ListBean listBean = datas.get(position);
        String type = listBean.getType();//video,text,image,gif,ad
        int itemViewType = -1;
        if ("video".equals(type)) {
            itemViewType = TYPE_VIDEO;
        } else if ("image".equals(type)) {
            itemViewType = TYPE_IMAGE;
        } else if ("text".equals(type)) {
            itemViewType = TYPE_TEXT;
        } else if ("gif".equals(type)) {
            itemViewType = TYPE_GIF;
        } else {
            itemViewType = TYPE_AD;//广告
        }
        return itemViewType;
    }

    @Override
    public int getViewTypeCount() {
        return 5;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        ViewHoulder viewHolder;

        if (convertView==null) {
            viewHolder = new ViewHoulder();
            //初始化item界面项
            convertView = initItemView(convertView, type,viewHolder);
            //初始化公共的视图
            initCommonView(convertView, type, viewHolder);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHoulder) convertView.getTag();
        }
        bindData(getItemViewType(position),viewHolder,datas.get(position));

        return convertView;
    }

    private void bindData(int itemViewType, ViewHoulder viewHolder, AudioData.ListBean data) {
        switch (itemViewType) {
            case TYPE_VIDEO://视频
                bindData(viewHolder, data);
                //第一个参数是视频播放地址，第二个参数是显示封面的地址，第三参数是标题
//                viewHolder.jcv_videoplayer.setUp(data.getVideo().getVideo().get(0), data.getVideo().getThumbnail().get(0), null);
//                viewHolder.tv_play_nums.setText(data.getVideo().getPlaycount() + "次播放");
//                viewHolder.tv_video_duration.setText(utils.stringForTime(data.getVideo().getDuration() * 1000) + "");

                 boolean setUp = viewHolder.jcv_videoplayer.setUp(
                        data.getVideo().getVideo().get(0), JCVideoPlayer.SCREEN_LAYOUT_LIST,
                        data.getText());
                if (setUp) {
//                    ImageLoader.getInstance().displayImage(data.getVideo().getThumbnail().get(0),
//                            viewHolder.iv_commant);
                    Glide.with(context).load(data.getVideo().getThumbnail().get(0)).
                            placeholder(R.drawable.bg_item).
                            error(R.drawable.bg_item).
                            diskCacheStrategy(DiskCacheStrategy.ALL).
                            into(viewHolder.jcv_videoplayer.thumbImageView);
                }
                viewHolder.tv_play_nums.setText(data.getVideo().getPlaycount() + "次播放");
                viewHolder.tv_video_duration.setText(utils.stringForTime(data.getVideo().getDuration() * 1000) + "");

                break;
            case TYPE_IMAGE://图片
                bindData(viewHolder, data);
                viewHolder.iv_image_icon.setImageResource(R.drawable.bg_item);
                int  height = data.getImage().getHeight()
                        <=DensityUtil.getScreenHeight(context)*0.75?data.getImage().getHeight(): (int) (DensityUtil.getScreenHeight(context) * 0.75);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,height);
                viewHolder.iv_image_icon.setLayoutParams(params);
                if(data.getImage() != null &&  data.getImage().getBig()!= null&&data.getImage().getBig().size() >0){
//                    x.image().bind(viewHolder.iv_image_icon, mediaItem.getImage().getBig().get(0));
//                    Glide.with(context).load(mediaItem.getImage().getBig().get(0)).placeholder(R.drawable.bg_item).error(R.drawable.bg_item).diskCacheStrategy(DiskCacheStrategy.ALL).into(viewHolder.iv_image_icon);
                    Picasso.with(context).load(data.getImage().getBig().get(0)).
                            placeholder(R.drawable.bg_item).error(R.drawable.bg_item).
                            into(viewHolder.iv_image_icon);
                }
                break;
            case TYPE_TEXT://文字
                bindData(viewHolder, data);
                break;
            case TYPE_GIF://gif
                bindData(viewHolder, data);
                System.out.println("mediaItem.getGif().getImages().get(0)" + data.getGif().getImages().get(0));
//                Glide.with(context).load(data.getGif().getImages().get(0)).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(viewHolder.iv_image_gif);
                Picasso.with(context).load(data.getGif().getImages().get(0)).into(viewHolder.iv_image_gif);
                break;
            case TYPE_AD://软件广告
                break;
        }

        //设置文本
        viewHolder.tv_context.setText(data.getText());
    }

    private void bindData(ViewHoulder viewHolder,AudioData.ListBean data) {
        if(data.getU()!=null&& data.getU().getHeader()!=null&&data.getU().getHeader().get(0)!=null){
//            x.image().bind(viewHolder.iv_headpic, mediaItem.getU().getHeader().get(0));
//            Log.i("info", "bindData: "+data.getU().getHeader().get(0));
            Picasso.with(context).load(data.getU().getHeader().get(0)).into(viewHolder.iv_headpic);
        }
        if(data.getU() != null&&data.getU().getName()!= null){
            viewHolder.tv_name.setText(data.getU().getName()+"");
        }

        viewHolder.tv_time_refresh.setText(data.getPasstime());

        //设置标签
        List<AudioData.ListBean.TagsBean> tagsEntities = data.getTags();
        if (tagsEntities != null && tagsEntities.size() > 0) {
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < tagsEntities.size(); i++) {
                buffer.append(tagsEntities.get(i).getName() + " ");
            }
            viewHolder.tv_video_kind_text.setText(buffer.toString());
        }

        //设置点赞，踩,转发
        viewHolder.tv_shenhe_ding_number.setText(data.getUp());
        viewHolder.tv_shenhe_cai_number.setText(data.getDown() + "");
        viewHolder.tv_posts_number.setText(data.getForward()+"");
    }

    //初始化公共布局
    private void initCommonView(View convertView, int type, ViewHoulder viewHolder) {
        switch (type) {
            case TYPE_VIDEO://视频
            case TYPE_IMAGE://图片
            case TYPE_TEXT://文字
            case TYPE_GIF: //gif
                //加载除开广告部分的公共部分视图
                //user info
                viewHolder.iv_headpic = (ImageView) convertView.findViewById(R.id.iv_headpic);
                viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
                viewHolder.tv_time_refresh = (TextView) convertView.findViewById(R.id.tv_time_refresh);
                viewHolder.iv_right_more = (ImageView) convertView.findViewById(R.id.iv_right_more);
                //bottom
                viewHolder.iv_video_kind = (ImageView) convertView.findViewById(R.id.iv_video_kind);
                viewHolder.tv_video_kind_text = (TextView) convertView.findViewById(R.id.tv_video_kind_text);
                viewHolder.tv_shenhe_ding_number = (TextView) convertView.findViewById(R.id.tv_shenhe_ding_number);
                viewHolder.tv_shenhe_cai_number = (TextView) convertView.findViewById(R.id.tv_shenhe_cai_number);
                viewHolder.tv_posts_number = (TextView) convertView.findViewById(R.id.tv_posts_number);
                viewHolder.ll_download = (LinearLayout) convertView.findViewById(R.id.ll_download);

                break;
        }
        //中间公共部分 -所有的都有
        viewHolder.tv_context = (TextView) convertView.findViewById(R.id.tv_context);
    }
    //初始化各种类型的item
    private View initItemView(View convertView, int type, ViewHoulder viewHolder) {
        switch (type){
            case TYPE_VIDEO://视频
                convertView = View.inflate(context, R.layout.all_video_item, null);
                //在这里实例化特有的
                viewHolder.tv_play_nums = (TextView) convertView.findViewById(R.id.tv_play_nums);
                viewHolder.tv_video_duration = (TextView) convertView.findViewById(R.id.tv_video_duration);
                viewHolder.iv_commant = (ImageView) convertView.findViewById(R.id.iv_commant);
                viewHolder.tv_commant_context = (TextView) convertView.findViewById(R.id.tv_commant_context);
                viewHolder.jcv_videoplayer = (JCVideoPlayerStandard) convertView.findViewById(R.id.jcv_video_player);
                break;
            case TYPE_IMAGE://图片
                convertView = View.inflate(context, R.layout.all_image_item, null);
                viewHolder.iv_image_icon = (ImageView) convertView.findViewById(R.id.iv_image_icon);
                break;
            case TYPE_TEXT://文字
                convertView = View.inflate(context, R.layout.all_text_item, null);
                break;
            case TYPE_GIF://gif
                convertView = View.inflate(context, R.layout.all_gif_item, null);
                viewHolder.iv_image_gif = (GifImageView) convertView.findViewById(R.id.iv_image_gif);
                break;
            case TYPE_AD://软件广告
                convertView = View.inflate(context, R.layout.all_ad_item, null);
                viewHolder.btn_install = (Button) convertView.findViewById(R.id.btn_install);
                viewHolder.iv_image_icon = (ImageView) convertView.findViewById(R.id.iv_image_icon);
                break;
        }
        return convertView;
    }

    static class ViewHoulder{
        //user_info
        ImageView iv_headpic;
        TextView tv_name;
        TextView tv_time_refresh;
        ImageView iv_right_more;
        //bottom
        ImageView iv_video_kind;
        TextView tv_video_kind_text;
        TextView tv_shenhe_ding_number;
        TextView tv_shenhe_cai_number;
        TextView tv_posts_number;
        LinearLayout ll_download;

        //中间公共部分 -所有的都有
        TextView tv_context;


        //Video
//        TextView tv_context;
        TextView tv_play_nums;
        TextView tv_video_duration;
        ImageView iv_commant;
        TextView tv_commant_context;
        JCVideoPlayerStandard jcv_videoplayer;

        //Image
        ImageView iv_image_icon;
//        TextView tv_context;

        //Text
//        TextView tv_context;

        //Gif
        GifImageView iv_image_gif;
//        TextView tv_context;

        //软件推广
        Button btn_install;
//        TextView iv_image_icon;
        //TextView tv_context;
    }
}
