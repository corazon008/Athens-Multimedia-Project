package server;

import shared.Enum.ProtocolType;
import shared.Enum.Resolution;
import shared.Enum.VideoFormat;
import shared.ServerInfo;
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

    public static void BeginStream(ProtocolType protocol, Video video) {
        ProcessBuilder builder = new ProcessBuilder( ffmpegPath, "-re", // Real-time streaming
                "-i", video.getVideoPath(), "-f", "mpegts", GetStreamURl(protocol));

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
            ffmpegProcess.destroy(); // envoie SIGTERM

            try {
                // Attend un peu pour voir s’il s’arrête gentiment
                if (!ffmpegProcess.waitFor(2, TimeUnit.SECONDS)) {
                    System.out.println("Forcing ffmpeg to stop...");
                    ffmpegProcess.destroyForcibly(); // envoie SIGKILL
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static String GetStreamURl(ProtocolType protocol) {
        switch (protocol) {
            case UDP:
                return String.format("udp://%s:%d", ServerInfo.GetFfmpegListenSocketIP(), ServerInfo.GetFfmpegPort());
            case TCP:
                return String.format("tcp://%s:%d", ServerInfo.GetFfmpegListenSocketIP(), ServerInfo.GetFfmpegPort());
            case RTP:
                return String.format("rtp://%s:%d", ServerInfo.GetFfmpegListenSocketIP(), ServerInfo.GetFfmpegPort());
            default:
                return String.format("udp://%s:%d", ServerInfo.GetFfmpegListenSocketIP(), ServerInfo.GetFfmpegPort());
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
                    if (newVideo.HaveHigherResolutionthan(video)) continue;
                    File newfile = new File(newVideo.getVideoPath());
                    if (newfile.exists()) continue;
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
        ProcessBuilder builder = new ProcessBuilder(ffmpegPath, "-i", input.getVideoPath(), "-c:v", output.getCodec(), "-preset", "slow", "-crf", output.getBitrateVariation(), "-c:a", "aac", "-b:a", "128k", "-vf", String.format("scale=-2:%s", output.getIntResolution()), output.getVideoPath());

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
