package server;

import shared.Enum.Resolution;
import shared.Enum.VideoFormat;
import shared.SharedInfo;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Video implements Cloneable {
    private Resolution resolution;
    private VideoFormat videoFormat;
    private String videoName;
    private String videoPath;

    public Video(File file) {
        this.videoPath = file.getParent();

        // Set VideoFormat
        for (VideoFormat videoFormat : SharedInfo.getVideoFormats()) {
            if (file.getName().endsWith(videoFormat.toString().toLowerCase())) {
                this.videoFormat = videoFormat;
                break;
            }
        }

        //Set Resolution
        for (Resolution res : SharedInfo.getResolutions()) {
            if (file.getName().contains(res.getLabel())) {
                resolution = res;

                break;
            }
        }

        //Set VideoName
        int indexOfRes = file.getName().indexOf(resolution.getLabel());
        if (indexOfRes != -1) {
            videoName = file.getName().substring(0, indexOfRes);
        } else {
            videoName = file.getName();
        }
    }

    public Video(Video other) {
        this.resolution = other.resolution;
        this.videoFormat = other.videoFormat;
        this.videoName = other.videoName;
        this.videoPath = other.videoPath;
    }

    public boolean HavehigherQualitythan(Video other) {
        return this.getIntResolution() > other.getIntResolution();
    }

    public int getIntResolution() {
        return Integer.parseInt(resolution.getLabel().replace("p", ""));
    }

    public Resolution getResolution() {
        return resolution;
    }

    public VideoFormat getVideoFormat() {
        return videoFormat;
    }

    public boolean SameVideo(Video other) {
        return this.videoName.equals(other.videoName);
    }

    public Video getVideoWithNew(VideoFormat format, Resolution res) {
        Video copy = new Video(this);
        if (format != null)
            copy.videoFormat = format;
        if (res != null)
            copy.resolution = res;

        return copy;
    }

    public String getVideoFileName() {
        return videoName + resolution.getLabel() + "." + videoFormat.toString().toLowerCase();
    }

    public String getVideoPath() {
        return videoPath + File.separator + getVideoFileName();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String toString() {
        return getVideoPath();
    }
}
