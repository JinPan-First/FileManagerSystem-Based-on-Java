/*
 * 各模块测试类
 */
public class Test {
    private static FileManager fileManager = new FileManager();
    public static void main(String[] args) throws InterruptedException {
        // progressBar.printProgress(2);
        fileManager.copyDirectoryWithProgress("D:\\VMware", "C:\\Users\\luul\\Desktop\\shiyan\\java\\copy");
    }
}
