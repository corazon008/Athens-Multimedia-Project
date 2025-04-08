package server;

import shared.Enum.Resolution;
import shared.Enum.VideoFormat;
import shared.SharedInfo;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Video implements Cloneable {
    private static Map<Resolution, Integer> bitrates = Map.of(Resolution.P240, 400, Resolution.P360, 750, Resolution.P480, 1000, Resolution.P720, 2500, Resolution.P1080, 4500);

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

    public boolean HaveHigherResolutionthan(Video other) {
        return this.getIntResolution() > other.getIntResolution();
    }

    public boolean HaveHigherOrEqResolutionthan(Video other) {
        return this.getIntResolution() >= other.getIntResolution();
    }

    public boolean HaveBetterFormatthan(Video other) {
        if (other.videoFormat == VideoFormat.AVI)
            return true;
        if (this.videoFormat == VideoFormat.AVI)
            return false;
        return true;
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
    public String getCodec(){
        return (videoFormat == VideoFormat.AVI) ? "libx264" : "libx265";
    }

    // e.g 400k
    public String getBitrate() {
        return String.format("%sk", bitrates.get(resolution));
    }

    public String getBitrateVariation() {
        return getCodec().equals("libx264") ? "23" : "27";
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
