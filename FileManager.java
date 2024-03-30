import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileManager {
    private FileSystem fileSystem;

    public FileManager() {
        fileSystem = new FileSystem();
    }

    /*
     * 文件地址解析
     */
    public String parseAddress(String directoryPath){
        String dir = "";
        directoryPath = (directoryPath.endsWith(":\\") || directoryPath.endsWith("..\\")) ? directoryPath : directoryPath.endsWith("\\") ? directoryPath.substring(0, directoryPath.length() - 1) : directoryPath;
        if(directoryPath.startsWith("..\\")){
            dir = getCurrentDirectory();
            while(directoryPath.startsWith("..\\")){
                directoryPath = directoryPath.substring(3);
                dir = dir.substring(0, dir.lastIndexOf("\\"));
            }
            if(directoryPath.length() > 0){
                dir = dir + "\\" + directoryPath;
            }
        } else{
            if(directoryPath.contains("\\")){
                dir = directoryPath;
            } else { dir = getCurrentDirectory() + "\\" + directoryPath;}

        }
        return dir;
    }

    public String getCurrentDirectory() {
        return fileSystem.getCurrentDirectory();
    }

    public void createDirectory(String directoryPath) {
        fileSystem.createDirectory(parseAddress(directoryPath));
    }

    public void createFile(String filePath) {
        fileSystem.createFile(parseAddress(filePath));
    }

    public void moveFile(String sourcePath, String destinationPath){
        fileSystem.moveFile(parseAddress(sourcePath), parseAddress(destinationPath));
    }

    public void setCurrentDirectory(String directoryPath){
        fileSystem.setCurrentDirectory(parseAddress(directoryPath));
    }

    public File[] listFiles() {
        return fileSystem.listFiles();
    }

    public File[] orderByTime(Boolean s){
        File[] listFiles = listFiles();
        List<File> fileList = Arrays.asList(listFiles);
        Collections.sort(fileList, new Comparator<File>() {
                public int compare(File file, File newFile) {
                    if(s){
                        if (file.lastModified() < newFile.lastModified()) {
                            return 1;
                        } else if (file.lastModified() == newFile.lastModified()) {
                            return 0;
                        } else {
                            return -1;
                        }
                    } 
                    else{
                        if (file.lastModified() > newFile.lastModified()) {
                            return 1;
                        } else if (file.lastModified() == newFile.lastModified()) {
                            return 0;
                        } else {
                            return -1;
                        }
                    }
 
                }
            });
        listFiles = fileList.toArray(new File[fileList.size()]);
        return listFiles;
    }

    public File[] orderByName(Boolean s) {
        File[] listFiles = listFiles();
        List<File> fileList = Arrays.asList(listFiles);
        Collections.sort(fileList, new Comparator<File>() {
                public int compare(File o1, File o2) {
                    if(s){
                        if (o1.isDirectory() && o2.isFile())
                            return -1;
                        if (o1.isFile() && o2.isDirectory())
                            return 1;
                        return o1.getName().compareTo(o2.getName());
                    }
                    else{
                        if (o1.isDirectory() && o2.isFile())
                            return -1;
                        if (o1.isFile() && o2.isDirectory())
                            return 1;
                        return (o1.getName().compareTo(o2.getName()) == -1) ? 1 : (o1.getName().compareTo(o2.getName()) == 1) ? -1 : 0;
                    }
                }
            });
        listFiles = fileList.toArray(new File[fileList.size()]);
        return listFiles;
    }

    public File[] orderBySize(Boolean s){
        File[] listFiles = listFiles();
        List<File> fileList = Arrays.asList(listFiles);
        Collections.sort(fileList, new Comparator<File>() {
                public int compare(File o1, File o2) {
                    if(s){
                        long diff = o1.length() - o2.length();
                        if (diff > 0)
                            return 1;
                        else if (diff == 0)
                            return 0;
                        else
                            return -1;
                    }
                    else{
                        long diff = o1.length() - o2.length();
                        if (diff > 0)
                            return -1;
                        else if (diff == 0)
                            return 0;
                        else
                            return 1;
                    }
                }
                public boolean equals(Object obj) {
                    return true;
                }
            });
        listFiles = fileList.toArray(new File[fileList.size()]);
        return listFiles;
    }
    
    public File[] filesFilter(String filter, String key){
        File[] listFiles = fileSystem.listFiles();
        List<File> fileList = Arrays.asList(listFiles);
        List<File> filteredList = new LinkedList<>();
        for(File f: fileList){
            if(filter == "type"){
                if(f.getName().toLowerCase().contains(key)){
                    filteredList.add(f);
                }
            } else if(filter == "name"){
                if(f.getName().toUpperCase().contains(key.toUpperCase())){
                    filteredList.add(f);
                }
            } else if(filter == "size"){
                if(f.length() == Long.parseLong(key)){
                    filteredList.add(f);
                }
            } else if(filter == "time"){
                if(getFileLastModifiedTime(f).contains(key)){
                    filteredList.add(f);
                }
            }
        }
        listFiles = filteredList.toArray(new File[filteredList.size()]);
        return listFiles;
    }
    
    public void deleteDirectory(String directoryPath){
        try {
            fileSystem.deleteDirectory(parseAddress(directoryPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void encryptFile(String source, String destination, String password){
        try {
            fileSystem.encryptFile(parseAddress(source), parseAddress(destination), password);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void decryptFile(String source, String destination, String password){
        try {
            fileSystem.decryptFile(parseAddress(source), parseAddress(destination), password);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void compressDirectory(String sourceDir, String compressedDir){
        try {
            fileSystem.compressDirectory(parseAddress(sourceDir), parseAddress(compressedDir));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void decompressDirectory(String compressedDir, String destinationDir){
        try {
            fileSystem.decompressDirectory(parseAddress(compressedDir), parseAddress(destinationDir));
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

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
    // 这里有一个线程池
    private ExecutorService executorService = Executors.newFixedThreadPool(10);

    public void copyDirectoryAsync(String sourcePath, String destinationPath) {
        executorService.submit(() -> {
            try {
                fileSystem.copyDirectory(parseAddress(sourcePath), parseAddress(destinationPath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void copyDirectoryWithProgress(String sourcePath, String destinationPath){
        try {
            fileSystem.copyDirectoryWithProgress(parseAddress(sourcePath), parseAddress(destinationPath));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
