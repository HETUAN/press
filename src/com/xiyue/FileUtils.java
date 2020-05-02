package com.xiyue;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * 文件相关操作
 */
public class FileUtils {
    public static List<String> readAllLines(String path) {
        if (!Files.exists(Paths.get(path))) {
            System.out.println("file not exits " + path);
            return null;
        }
        try {
            return Files.readAllLines(Paths.get(path));
        } catch (Exception ex) {
            System.out.println("ERROR " + ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }
}
