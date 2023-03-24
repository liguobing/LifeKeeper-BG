package com.lixyz.lifekeeper.util;

import com.jhlabs.image.BoxBlurFilter;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;

public class FileUtil {
    public static long getRecordFileCallTime(String fileName) {
        String time = fileName.split("_")[1];
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.parseInt(time.substring(0, 4)));
        calendar.set(Calendar.MONTH, Integer.parseInt(time.substring(4, 6)));
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(time.substring(6, 8)));
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time.substring(8, 10)));
        calendar.set(Calendar.MINUTE, Integer.parseInt(time.substring(10, 12)));
        calendar.set(Calendar.SECOND, Integer.parseInt(time.substring(12, 14)));
        return calendar.getTimeInMillis();
    }

    public static String getRecordFilePhoneNumber(String fileName) {
        return fileName.split("\\(")[1].split("\\)")[0];
    }

    public static String getRecordFileContactName(String fileName) {
        return fileName.replaceAll("通话录音@+", "").split("\\(")[0];
    }

    public static synchronized boolean getCoverFile(File sourceFile, File coverFile) {
        try {
            BufferedImage sourceImg = ImageIO.read(new FileInputStream(sourceFile));
            int width = sourceImg.getWidth();
            int height = sourceImg.getHeight();
            float scale = 1;
            if (width > height) {
                if (width > 540) {
                    scale = 540f / width;
                }
            } else {
                if (height > 1124) {
                    scale = 1124f / height;
                }
            }
            Thumbnails.of(sourceFile)
                    .scale(scale)
                    .toFile(coverFile);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static synchronized boolean getThumbnailFile(File sourceFile, File thumbnailFile) {
        try {
            Thumbnails.of(sourceFile)
                    .sourceRegion(Positions.CENTER, 300, 300)
                    .size(300, 300)
                    .keepAspectRatio(false)
                    .toFile(thumbnailFile);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static synchronized boolean getBlurFile(File sourceFile, File blurFile) {
        try {
            BufferedImage sourceImg = ImageIO.read(new FileInputStream(sourceFile));
            BoxBlurFilter filter = new BoxBlurFilter();
            filter.setRadius(30);
            BufferedImage dst = filter.filter(sourceImg, null);
            ImageIO.write(dst, FileUtil.getFileFormat(blurFile.getName()), blurFile);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static synchronized boolean multipartFileTransferToFile(MultipartFile multipartFile, File file) throws IOException {
        try {
            multipartFile.transferTo(file);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取文件后缀名
     */
    public static String getFileFormat(String fileName) {
        return FilenameUtils.getExtension(fileName);
    }

    /**
     * 获取文件 SHA1
     */
    public static synchronized String getFileSha1(File file) throws OutOfMemoryError,
            IOException, NoSuchAlgorithmException {
        MessageDigest messagedigest = MessageDigest.getInstance("SHA-1");
        FileInputStream in = new FileInputStream(file);
        FileChannel ch = in.getChannel();
        MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY, 0,
                file.length());
        messagedigest.update(byteBuffer);
        return bufferToHex(messagedigest.digest(), 0, messagedigest.digest().length);
    }

    private static String bufferToHex(byte bytes[], int m, int n) {
        StringBuffer stringbuffer = new StringBuffer(2 * n);
        int k = m + n;
        for (int l = m; l < k; l++) {
            appendHexPair(bytes[l], stringbuffer);
        }
        return stringbuffer.toString();
    }

    private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
        char c0 = hexDigits[(bt & 0xf0) >> 4];
        char c1 = hexDigits[bt & 0xf];
        stringbuffer.append(c0);
        stringbuffer.append(c1);
    }

    protected static char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
}
