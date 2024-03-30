import java.util.Scanner;
/*
 * 主函数类
 */
public class MainActivity {
    private static FileManager fileManager = new FileManager();
    private static Scanner scanner = new Scanner(System.in);
    private static PrintMethod printMethod = new PrintMethod();
    public static void main(String[] args) {
        System.out.println("文件管理系统(仿Shell风格)");
        System.out.println("--------------------");
        System.out.println("作者: ZhouZhengpan");
        System.out.println("项目文件地址: www.github.com");
        System.out.println("版本: 1.5");
        System.out.println("日期: 2024/03/29");
        System.out.println("--------------------");
        while (true) {
            System.out.print(fileManager.getCurrentDirectory() + "> ");
            String next;
            switch (scanner.next()) {
                case "ls":
                    next = scanner.next();
                    switch (next) {
                        case "-t":
                            printMethod.printFiles(fileManager.orderByTime(true));
                            break;
                        case "-rt":
                            printMethod.printFiles(fileManager.orderByTime(false));
                            break;
                        case "-n":
                            printMethod.printFiles(fileManager.orderByName(true));
                            break;
                        case "-rn":
                            printMethod.printFiles(fileManager.orderByName(false));
                            break;
                        case "-s":
                            printMethod.printFiles(fileManager.orderBySize(true));
                            break;
                        case "-rs":
                            printMethod.printFiles(fileManager.orderBySize(false));
                            break;
                        case "-l":
                            printMethod.printFiles(fileManager.listFiles());
                            break;
                        default:
                            System.out.println(">>> 不支持的指令: ls " + next);
                            break;
                    }
                    break;
                case "cd":
                    fileManager.setCurrentDirectory(scanner.next());
                    break;
                case "cat":
                    printMethod.printTextFile(fileManager.parseAddress(scanner.next()));
                    break;
                case "cp":
                    next = scanner.next();
                    if (next.equals("-async")){
                        fileManager.copyDirectoryAsync(scanner.next(), scanner.next());
                    } else{
                        fileManager.copyDirectoryWithProgress(next, scanner.next());
                    }
                    break;
                case "mv":
                    fileManager.moveFile(scanner.next(), scanner.next());
                    break;
                case "mkdir":
                    fileManager.createDirectory(scanner.next());
                    break;
                case "touch":
                    fileManager.createFile(scanner.next());
                    break;
                case "rm":
                    fileManager.deleteDirectory(scanner.next());
                    break;
                case "enc":
                    fileManager.encryptFile(scanner.next(), scanner.next(), "");
                    break;
                case "dnc":
                    fileManager.decryptFile(scanner.next(), scanner.next(), "");
                    break;
                case "zip":
                    if(scanner.next().equals("-r")){
                        next = scanner.next();
                        fileManager.compressDirectory(next, scanner.next());
                    } else{
                        next = scanner.next();
                        String des = fileManager.parseAddress(next);
                        des = des.substring(0, des.lastIndexOf("\\")) + des.substring(des.lastIndexOf("\\"), des.length()) + ".zip";
                        fileManager.compressDirectory(next, des);
                    }
                    break;
                case "uzip":
                    fileManager.decompressDirectory(scanner.next(), scanner.next());
                    break;
                case "grep":
                    switch (scanner.next()){
                        case "-name":
                            printMethod.printFiles(fileManager.filesFilter("name", scanner.next()));
                            break;
                        case "-type":
                            printMethod.printFiles(fileManager.filesFilter("type", scanner.next()));
                            break;
                        case "-size":
                            printMethod.printFiles(fileManager.filesFilter("size", scanner.next()));
                            break;
                        case "-time":
                            printMethod.printFiles(fileManager.filesFilter("time", scanner.next()));
                            break;
                        default:
                            System.out.println(">>> Invalid command" );
                            break;
                    }
                    break;
                case "exit":
                    return;
                case "help":
                    switch (scanner.next()) {
                        case "all":
                            System.out.println(">>> 支持指令如下所示: ");
                            System.out.println("        - ls - 列出本文件夹下的所有文件");
                            System.out.println("        - cd - 切换到指定文件夹");
                            System.out.println("        - mkdir - 创建新文件夹");
                            System.out.println("        - rmdir - 删除空文件夹");
                            System.out.println("        - touch - 创建新文件");
                            System.out.println("        - rm - 删除指定文件");
                            System.out.println("        - mv - 移动文件");
                            System.out.println("        - cp - 复制文件");
                            System.out.println("        - cat - 查看文件内容");
                            System.out.println("        - pwd - 显示当前路径");
                            System.out.println("        - grep- 进行文件筛选与匹配");
                            System.out.println("        - zip - 对指定文件或文件夹进行压缩");
                            System.out.println("        - unzip - 对指定文件或文件夹进行解压缩");
                            System.out.println("        - enc - 对指定文件执行加密");
                            System.out.println("        - dec - 对指定文件执行解密");
                            System.out.println("        - help - 显示帮助信息");
                            System.out.println("        - exit - 退出系统");
                            break;
                        case "ls":        
                            System.out.println(">>> 支持参数如下所示: ");
                            System.out.println("        - l - 列出所有文件，包括隐藏文件");
                            System.out.println("        - t - 列出所有文件，按照最近一次修改时间顺序排列");
                            System.out.println("        - s - 列出所有文件，按照文件大小顺序排列");
                            System.out.println("        - n - 列出所有文件，按照文件名顺序排列");
                            System.out.println("        - r - 逆序列出所有文件，上面三种参数加上r，例如'-rn'， 记为按照文件名逆序排列");
                        case "cp":
                            System.out.println(">>> 支持参数如下所示: ");
                            System.out.println("        - r - 复制文件夹并强制覆盖目标文件");
                            System.out.println("        - async - 异步复制");
                        case "grep":
                            System.out.println(">>> 支持参数如下所示: ");
                            System.out.println("        - size - 按照文件大小进行筛选");
                            System.out.println("        - name - 按照文件名进行筛选");
                            System.out.println("        - type - 按照文件类型进行筛选");
                            System.out.println("        - time - 按照文件最后修改日期进行筛选");
                        case "zip":
                            System.out.println(">>> 支持参数如下所示: ");
                            System.out.println("        - r - 压缩文件夹到指定路径并强制覆盖目标文件");
                        default:
                            break;
                    }
                    break;
                default:
                    System.out.println(">>> Invalid command");
                    break;
            }
        }
    }
}