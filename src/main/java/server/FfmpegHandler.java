package server;

import shared.Enum.EndpointType;
import shared.Enum.ProtocolType;
import server.Enum.RTPStreamType;
import shared.Enum.Resolution;
import shared.Enum.VideoFormat;
import shared.SharedInfo;
import shared.Video;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FfmpegHandler {
    private static Process ffmpegProcess;
    private static String ffmpegPath = "ffmpeg-win\\bin\\ffmpeg.exe";
    private static File workingDirectory = new File("tmp\\server");

    public static void BeginStream(ProtocolType protocol, Video video, Runnable rtpSendSdpFile) {
        if ((new File("stream.sdp")).exists()) {
            new File("stream.sdp").delete();
        }
        ProcessBuilder builder;
        if (protocol != ProtocolType.RTP)
            builder = new ProcessBuilder(ffmpegPath, "-re",
                    "-i", video.getVideoPath(),
                    "-f", "mpegts",
                    SharedInfo.GetStreamUrl(protocol, EndpointType.SERVER));
        else {
            builder = new ProcessBuilder(ffmpegPath, "-re",
                    "-i", video.getVideoPath(),
                    "-map", "0:v", "-c:v", "copy", "-s", "2592x1080", "-f", "rtp", SharedInfo.GetStreamUrl(protocol, EndpointType.SERVER, RTPStreamType.VIDEO),
                    "-map", "0:a", "-c:a", "copy", "-f", "rtp", SharedInfo.GetStreamUrl(protocol, EndpointType.SERVER, RTPStreamType.AUDIO),
                    "-sdp_file", "stream.sdp");
            while (!new File("stream.sdp").exists()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            rtpSendSdpFile.run();
        }


        builder.inheritIO();
        try {
            ffmpegProcess = builder.start();
            //ffmpegProcess.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void StopStream() {
        if (ffmpegProcess != null && ffmpegProcess.isAlive()) {
            System.out.println("Stopping ffmpeg process...");
            ffmpegProcess.destroy();

            try {
                if (!ffmpegProcess.waitFor(2, TimeUnit.SECONDS)) {
                    System.out.println("Forcing ffmpeg to stop...");
                    ffmpegProcess.destroyForcibly();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static File[] getVideoFiles() {
        return new File("videos").listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (!pathname.isFile()) {
                    return false;
                }
                for (VideoFormat extension : SharedInfo.getVideoFormats()) {
                    if (pathname.getName().toLowerCase().endsWith(extension.getLabel())) {
                        return true;
                    }
                }
                return false;
            }
        });
    }

    public static void FfmpegMakeAllResAndFormat() {
        List<Video> highestResVideos = new ArrayList<>();
        for (File file : getVideoFiles()) {
            Video video = new Video(file);
            boolean videoExists = false;
            for (Video t_video : highestResVideos) {
                if (video.SameVideo(t_video)) {
                    if (video.HaveHigherOrEqResolutionthan(t_video)) if (video.HaveBetterFormatthan(t_video)) {
                        highestResVideos.remove(t_video);
                        highestResVideos.add(video);
                    }
                    videoExists = true;
                    break;
                }
            }
            if (!videoExists) highestResVideos.add(video);
        }

        ExecutorService executor = Executors.newFixedThreadPool(4); // max 4 transcodages en parallèle

        for (Video video : highestResVideos) {
            for (Resolution resolution : SharedInfo.getResolutions()) {
                for (VideoFormat format : SharedInfo.getVideoFormats()) {
                    Video newVideo = video.getVideoWithNew(format, resolution);
                    if (newVideo.HaveHigherResolutionthan(video))
                        continue;
                    File newfile = new File(newVideo.getVideoPath());
                    if (newfile.exists())
                        continue;
                    System.out.println(newfile.getPath());

                    // Submit task to thread pool
                    executor.submit(() -> FfmpegTranscode(new Video(video), newVideo));
                }
            }
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void FfmpegTranscode(Video input, Video output) {
        ProcessBuilder builder = new ProcessBuilder(ffmpegPath,
                "-i", input.getVideoPath(),
                "-c:v", output.getCodec(),
                "-preset", "slow", "-crf", output.getBitrateVariation(), "-minrate", output.getMinBitrate(),
                "-c:a", "aac", "-b:a", "128k", "-vf", String.format("scale=-2:%s", output.getIntResolution()), output.getVideoPath());

        System.out.println(builder.command());

        builder.inheritIO();
        try {
            Process ffmpegProcess = builder.start();
            ffmpegProcess.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
