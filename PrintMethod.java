import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * 负责主界面的内容输出
 */
public class PrintMethod {
    /*
     * 打印文件目录
     */
    public void printFiles(File[] files) {
        if(files.length == 0){
            System.out.println("无匹配项");
            return;
        }
        System.out.println("");
        System.out.println("   Directory: " + files[0].getParent());
        System.out.println("");
        System.out.printf("%s\t %s\t %17s\t %s\n", "Mode", "LastWriteTime", "Length", "Name");
        System.out.println("----     --------------             ------        ----");
        if(files.length != 0){
            for (File file : files) {
                String filePremisson = getFilePermission(file);

                System.out.printf("%s\t %s\t %9s\t %s\n", filePremisson, getFileLastModifiedTime(file), (file.isDirectory() ?  "" : String.valueOf(file.length())), file.getName());
            }
        }
    }

    /*
     * 获取文件权限
     */
    public String getFilePermission(File file){
        return (file.isDirectory() ? "d" : "-") + (Files.isWritable(file.toPath()) ? "w" : "-") + (Files.isReadable(file.toPath()) ? "r" : "-") + (Files.isExecutable(file.toPath()) ? "x" : "-") + (Files.isSymbolicLink(file.toPath()) ? "l" : "-");
    }

    /*
     * 获取文件最后修改时间
     */
    public String getFileLastModifiedTime(File file){
        try {
            Date date = new Date(Files.getLastModifiedTime(file.toPath()).toMillis());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            return sdf.format(date);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    /*
     * 打印文本文件
     */
    public void printTextFile(String filePath){
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath),"UTF-8"));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
