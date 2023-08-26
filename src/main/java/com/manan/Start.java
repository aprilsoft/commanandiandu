package com.manan;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.StrUtil;
import com.manan.entiry.WH;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Start {
    private static String dirPath = "D:\\work\\IdeaProject\\my_wg\\src\\main\\resources";
    private static MimetypesFileTypeMap map = new MimetypesFileTypeMap();
    private static WH wh = new WH(400, 400);
    private static final String RESIZE = "resize";
    private static final String RENAME_SORT = "rename_sort_";
    private static List<String> dataList = new ArrayList<>(50);

    static {
        map.addMimeTypes("image png tif jpg jpeg bmp");
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            dirPath = args[0];
        }
        File[] dirList = FileUtil.file(dirPath).listFiles(File::isDirectory);
        if (dirList == null) {
            return;
        }
        for (File dir : dirList) {
            List<File> imgList = FileUtil.loopFiles(FileUtil.file(dir), Start::isImage);
            if (imgList.size() == 0) {
                continue;
            }
            Console.log("enter dir: [{}]", dir.getName());
            // 图片改名
            int start = 100001;
            String path = dir.getAbsolutePath();
            String destDirPath = path + File.separator + RENAME_SORT;
            for (int i = 0; i < imgList.size(); i++) {
                if (i == 0) {
                    dataList.add("@rename1\t@rename2");
                }
                File imgFile = imgList.get(i);
                String srcName = imgFile.getName();
                String[] split = srcName.split("\\.");
                String srcSuffix = "." + split[split.length - 1];
                // 按照小册子排序
                String rename = rename(i + 1, start, srcSuffix, imgList.size());
                if (StrUtil.isBlank(rename)) {
                    Console.log("do not rename get src name is [{}]", srcName);
                    rename = Start.RENAME_SORT + (start + i) + srcSuffix;
                }
                dataList.add(rename);
                String renameDestPath = destDirPath + File.separator + rename;
                Console.log("rename:[{}]---->[{}]", srcName, rename);
                FileUtil.copy(imgFile, FileUtil.file(renameDestPath), true);
            }
            Collections.sort(dataList);
            createDataFile(dataList, destDirPath);
            dataList.clear();
        }

    }

    private static void createDataFile(List<String> dataList, String destDirPath) {
        String dataFilePath = destDirPath + File.separator + "rename.txt";
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i += 2) {
            if (i == 0) {
                list.add(dataList.get(i));
            } else {
                list.add(dataList.get(i - 1) + "\t" + dataList.get(i));
            }
        }
        FileUtil.writeLines(list, dataFilePath, StandardCharsets.UTF_8);
        list.clear();
    }

    private static String rename(int i, int start, String suffix, int total) {
        if (total % 4 != 0) {
            return "";
        }
        int sort;
        int half = total / 2;
        if (i <= half) {
            if (i % 2 == 0) {
                sort = 2 * i - 1;
            } else {
                sort = 2 * i;
            }
        } else {
            if (i % 2 == 0) {
                sort = 2 * (total - i) + 1;
            } else {
                sort = 2 * (total - i) + 2;
            }
        }
        sort = start + sort - 1;
        return Start.RENAME_SORT + sort + suffix;
    }

    private static String resizeName(int i, int start, String suffix) {
        return Start.RESIZE + (start + i) + suffix;
    }

    private static boolean isImage(File file) {
        String contentType = map.getContentType(file).split("/")[0];
        String name = file.getName();
        return StrUtil.equalsIgnoreCase(contentType, "image") && !name.startsWith(RENAME_SORT);
    }

}
