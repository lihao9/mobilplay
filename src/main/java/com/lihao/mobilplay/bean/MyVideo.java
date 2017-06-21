package com.lihao.mobilplay.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by hbm on 2017/3/27.
 */

public class MyVideo implements Parcelable {
    private String ivUri;//视屏图片地址
    private String name;//视频文件在sdcard的名称
    private long duration;//视频总时长
    private long size;//视频的文件大小
    private String data;//视频的绝对地址
    private String artist;//歌曲的演唱者
    private String desc;//描述

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getIvUri() {
        return ivUri;
    }

    public void setIvUri(String ivUri) {
        this.ivUri = ivUri;
    }

    public static final Creator<MyVideo> CREATOR = new Creator<MyVideo>() {
        @Override
        public MyVideo createFromParcel(Parcel in) {
            MyVideo video = new MyVideo();
            video.name = in.readString();
            video.data = in.readString();
            video.artist = in.readString();
            video.duration = in.readLong();
            video.size = in.readLong();
            video.ivUri = in.readString();
            video.desc = in.readString();
            return video;
        }

        @Override
        public MyVideo[] newArray(int size) {
            return new MyVideo[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getArtist() {
        return artist;
    }

    @Override
    public String toString() {
        return "MyVideo{" +
                "name='" + name + '\'' +
                ", duration=" + duration +
                ", size=" + size +
                ", data='" + data + '\'' +
                ", artist='" + artist + '\'' +
                '}';
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. For example, if the object will
     * include a file descriptor in the output of {@link #writeToParcel(Parcel, int)},
     * the return value of this method must include the
     * {@link #CONTENTS_FILE_DESCRIPTOR} bit.
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     * @see #CONTENTS_FILE_DESCRIPTOR
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(data);
        dest.writeString(artist);
        dest.writeLong(duration);
        dest.writeLong(size);
        dest.writeString(ivUri);
        dest.writeString(desc);
    }

}
