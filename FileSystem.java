import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
/*
 * 文件系统类
 * 负责直接调用File库，包含本项目最底层的文件操作
 */
public class FileSystem {
    private String currentDirectory;
    private ProgressBar progressBar = new ProgressBar();
    private byte key = 42;
    private static final int  BUFFER_SIZE = 2 * 1024;
    public FileSystem() {
        currentDirectory = System.getProperty("user.dir");
    }

    public void setCurrentDirectory(String directory) {
        File dir  = new File(directory);
        if (dir.exists() && dir.isDirectory()) {
            currentDirectory = dir.getAbsolutePath();
        } else {
            System.out.println("无效目录: " + directory);
        }
    }

    public String getCurrentDirectory() {
        return currentDirectory;
    }

    public void createDirectory(String directory) {
        File folder = new File(directory);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    public void createFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void moveFile(String sourceFilePath, String destinationFilePath){
        File sourceFile = new File(sourceFilePath);
        File destinationFile = new File(destinationFilePath);
        if (sourceFile.exists()) {
            if (destinationFile.exists()) {
                try {
                    deleteDirectory(destinationFile.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            sourceFile.renameTo(destinationFile);
        }
    }

    public File[] listFiles() {
        File directory = new File(currentDirectory);
        File[] files = directory.listFiles();
        return files;
    }

    public List<File> listFilesFiltered(String filterBy, String filterValue) {
        File directory = new File(currentDirectory);
        List<File> files = Arrays.stream(directory.listFiles())
                .filter(file -> matchesFilter(file, filterBy, filterValue))
                .collect(Collectors.toList());
        return files;
    }

    private boolean matchesFilter(File file, String filterBy, String filterValue) {
        switch (filterBy.toLowerCase()) {
            case "name":
                return file.getName().contains(filterValue);
            case "size":
                return file.length() == Long.parseLong(filterValue);
            case "type":
                return file.isDirectory() == "directory".equals(filterValue);
            case "date":
                // Implement date filtering logic
                return false;
            default:
                return false;
        }
    }

    public void sortFiles(String sortBy) {
        // Implement file sorting logic
    }

    public void deleteDirectory(String directoryPath) throws IOException {
        Path directoryToDelete = Paths.get(directoryPath);
        Files.walkFileTree(directoryToDelete, new SimpleFileVisitor<Path>() {
           @Override
           public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
               Files.delete(file);
               return FileVisitResult.CONTINUE;
           }

           @Override
           public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
               Files.delete(dir);
               return FileVisitResult.CONTINUE;
           }
        });
    }

    public void copyFile(String sourcePath, String destinationPath) throws IOException {
        Files.copy(Paths.get(sourcePath), Paths.get(destinationPath));
    }

    public void copyDirectory(String sourcePath, String destinationPath) throws IOException {
        Path sourceDir = Paths.get(sourcePath);
        Path destDir = Paths.get(destinationPath);
        try (Stream<Path> stream = Files.walk(sourceDir)) {
            stream.forEach(source -> {
                Path destination = destDir.resolve(sourceDir.relativize(source));
                if (Files.isDirectory(source)) {
                    try {
                        Files.createDirectories(destination);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    
    public void copyDirectoryWithProgress(String sourcePath, String destinationPath) throws IOException {
        Path sourceDir = Paths.get(sourcePath);
        Path destDir = Paths.get(destinationPath);
        long startTime = System.nanoTime();
        long[] totalSize = {0};
        try (Stream<Path> stream = Files.walk(sourceDir)) {
            totalSize[0] = stream.mapToLong(value -> {
                try {
                    return Files.size(value);
                } catch (IOException e) {
                    e.printStackTrace();
                    return 0;
                }
            }).sum();
            System.out.println("Total size: " + totalSize[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (Stream<Path> stream = Files.walk(sourceDir)) {
            long[] copiedSize = {0};
            stream.forEach(source -> {
                Path destination = destDir.resolve(sourceDir.relativize(source));
                if (Files.isDirectory(source)) {
                    try {
                        Files.createDirectories(destination);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    long Size = 0;
                    try {
                        Size = Files.size(source);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    copiedSize[0] += Size;
                }
                progressBar.printProgress(copiedSize[0] , totalSize[0]);
            });
            progressBar.printProgress(totalSize[0], totalSize[0]);
        } catch (IOException e){
            e.printStackTrace();
        }
        long endTime = System.nanoTime();
        long copyTime = endTime - startTime;
        System.out.println("\n Copy time: " + copyTime / 1000000000 + " s");
    }

    public void encryptFile(String source, String destination, String password) throws IOException {
        try (InputStream in = Files.newInputStream(Paths.get(source));
            OutputStream out = Files.newOutputStream(Paths.get(destination))) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                for (int i = 0; i < bytesRead; i++) {
                    buffer[i] ^= key;
                }
                out.write(buffer, 0, bytesRead);
            }
        }
    }

    public void decryptFile(String source, String destination, String password) throws IOException {
        try (InputStream in = Files.newInputStream(Paths.get(source));
            OutputStream out = Files.newOutputStream(Paths.get(destination))) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                for (int i = 0; i < bytesRead; i++) {
                    buffer[i] ^= key;
                }
                out.write(buffer, 0, bytesRead);
            }
        }
    }

    public void compressDirectory(String sourceDir, String compressedDir) throws IOException {
        
        long start = System.currentTimeMillis();
        ZipOutputStream zos = null ;
        try {
            zos = new ZipOutputStream(new FileOutputStream(compressedDir));
            File sourceFile = new File(sourceDir);
            compress(sourceFile,zos,sourceFile.getName(), true);
            long end = System.currentTimeMillis();
            System.out.println("压缩完成，耗时：" + (end - start) +" ms");
        } catch (Exception e) {
            throw new RuntimeException("zip error from ZipUtils",e);
        }finally{
            if(zos != null){
                try {
                    zos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }
    }

    private static void compress(File sourceFile, ZipOutputStream zos, String name, boolean KeepDirStructure) throws Exception{
        byte[] buf = new byte[BUFFER_SIZE];
        if(sourceFile.isFile()){
            // 向zip输出流中添加一个zip实体，构造器中name为zip实体的文件的名字
            zos.putNextEntry(new ZipEntry(name));
            // copy文件到zip输出流中
            int len;
            FileInputStream in = new FileInputStream(sourceFile);
            while ((len = in.read(buf)) != -1){
                zos.write(buf, 0, len);
            }
            // Complete the entry
            zos.closeEntry();
            in.close();
        } else {
            File[] listFiles = sourceFile.listFiles();
            if(listFiles == null || listFiles.length == 0){
                if(KeepDirStructure){
                    zos.putNextEntry(new ZipEntry(name + "/"));
                    // 没有文件，不需要文件的copy,仅仅做文件标记
                    zos.closeEntry();
                }
                
            }else {
                for (File file : listFiles) {
                    // 判断是否需要保留原来的文件结构
                    if (KeepDirStructure) {
                        // 注意：file.getName()前面需要带上父文件夹的名字加一斜杠,
                        // 不然最后压缩包中就不能保留原来的文件结构,即：所有文件都跑到压缩包根目录下了
                        compress(file, zos, name + "/" + file.getName(),KeepDirStructure);
                    } else {
                        compress(file, zos, file.getName(),KeepDirStructure);
                    }
                    
                }
            }
        }
	}

    public void decompressDirectory(String sourceDir, String extractedFile) throws FileNotFoundException, IOException{
        long start = System.currentTimeMillis();
        File folder = new File(extractedFile);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(sourceDir))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    File dir = new File(folder, entry.getName());
                    dir.mkdirs();
                } else {
                    File file = new File(folder, entry.getName());
                    file.getParentFile().mkdirs();
                    try (OutputStream os = new FileOutputStream(file)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = zis.read(buffer)) != -1) {
                            os.write(buffer, 0, len);
                        }
                    }
                }
                zis.closeEntry();
            }
            long end = System.currentTimeMillis();
            System.out.println("解压完成，耗时：" + (end - start) +" ms");
        }
    }
}
