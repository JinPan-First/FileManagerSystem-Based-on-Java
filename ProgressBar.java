public class ProgressBar {
    private String finish;
    private String unFinish;
    private int progress;

    // 进度条粒度
    private final int PROGRESS_SIZE = 50;
    private int BITE = 2;

    private String getNChar(int num, char ch){
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < num; i++){
            builder.append(ch);
        }
        return builder.toString();
    }
    public void printProgress(long currentsize, long totalsize){
        int old = 0;
        int index = (int) (currentsize * 100 / totalsize);
        finish = getNChar(index / BITE, '█');
        unFinish = getNChar(PROGRESS_SIZE - index / BITE, '─');
        String target = String.format("%3d%%[%s%s]  |current:%10d | total:%10d|", index, finish, unFinish, 0, totalsize);
        if(progress == 0){
            System.out.print("Progress:");            
            System.out.print(target);
            progress = 1;
        }
        if(old != index) {
            finish = getNChar(index / BITE, '█');
            unFinish = getNChar(PROGRESS_SIZE - index / BITE, '─');

            target = String.format("%3d%%├%s%s┤  |current:%10d | total:%10d|", index, finish, unFinish, currentsize, totalsize);
            System.out.print(getNChar(PROGRESS_SIZE + 47, '\b'));
            System.out.print(target);
            old = index;
        }
        if(index == 100){
            progress = 0;
        }
    }
}
