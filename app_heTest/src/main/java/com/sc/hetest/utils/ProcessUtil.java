package com.sc.hetest.utils;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.webkit.ValueCallback;
//import com.thehelper.th_app_android.TLog;
//import com.thehelper.th_app_android.model.ProcessInfo;
//import com.thehelper.th_app_android.utils.AppUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sc
 * @date 2021/12/9 17:31
 */
public class ProcessUtil {

    private Context context;

//    private IPorcess iPorcess;
//
//    private List<ProcessInfo> processInfos;
//
//    private boolean canRefresh = true;
//
//    private long availSpace, totalSpace;

//    public ProcessUtil(Context context, IPorcess iPorcess){
//        this.context = context;
//        this.iPorcess = iPorcess;
//        init();
//    }

//    void init(){
//        refresh();
//    }

//    public void refresh(){
//        if (!canRefresh)return;
//        canRefresh = false;
//        TLog.d("zhs");
//        AppUtil.getRunningInfosAsync(context, new ValueCallback<List<ProcessInfo>>() {
//            @Override
//            public void onReceiveValue(List<ProcessInfo> infos) {
//                canRefresh = true;
//                processInfos = infos;
//                if (iPorcess != null)
//                    iPorcess.onLoaded(infos);
//            }
//        });
//        // 获取可用空间和总空间
//        availSpace = getAvailSpace(context);
//        totalSpace = getTotalSpace(context);
//        TLog.d(availSpace + " " + totalSpace);
//        if (iPorcess != null)
//            iPorcess.onAchieveMemory(availSpace, totalSpace);
//    }

//    public void clear(boolean clearSystem){
//        if (processInfos == null || processInfos.size() == 0)return;
//        for (int i = 0; i < processInfos.size(); i++) {
//            boolean isSelf = context.getPackageName().equals(processInfos.get(i).packageName);
//            if (processInfos.get(i).hasView && !isSelf)
//                killProcess(context, processInfos.get(i));
//        }
//        if (iPorcess != null)
//            iPorcess.onClear();
//        refresh();
//    }

    public void dispose() {

    }

    /**
     * 获取进程总数的方法
     *
     * @param context 上下文环境
     * @return
     */
    public static int getProcessCount(Context context) {
        // 1.获取ActivityManager对象
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        // 2.获取正在运行的进程的集合
        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = activityManager.getRunningAppProcesses();
        // 3.返回集合的总数
        return runningAppProcesses.size();
    }

    /**
     * 获取可用空间的大小
     *
     * @param context 上下文环境
     * @return 返回可用的内存数，bytes
     */
    public static long getAvailSpace(Context context) {
        // 1.获取ActivityManager对象
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        // 2.构建存储可用内存的对象
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        // 3.给MemoryInfo对象赋值（可用内存）
        activityManager.getMemoryInfo(memoryInfo);
        // 4.获取MemoryInfo中相应的可用内存大小
        return memoryInfo.availMem;
    }

    /**
     * 获取所有空间的大小
     *
     * @param context 上下文环境
     * @return 返回可用的内存数，单位为bytes
     */
    public static long getTotalSpace(Context context) {
        if (Build.VERSION.SDK_INT >= 16) {
            // api版本大于16的方式
            // 1.获取ActivityManager对象
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            // 2.构建存储可用内存的对象
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            // 3.给MemoryInfo对象赋值（所有内存）
            activityManager.getMemoryInfo(memoryInfo);
            // 4.获取MemoryInfo中相应的所有内存大小
            return memoryInfo.totalMem;
        } else {
            // api版本小于16的方式
            // 如果你的api版本在16以下，需要读取proc/meminfo文件，读取第一行，获取数字字符，转换成bytes信息返回
            FileReader fileReader = null;
            BufferedReader bufferedReader = null;
            try {
                fileReader = new FileReader("proc/meminfo");
                bufferedReader = new BufferedReader(fileReader);
                String lineOne = bufferedReader.readLine();
                // 将字符串转换成字符数组
                char[] chars = lineOne.toCharArray();
                // 循环遍历每一个字符，如果此字符的ASCII码在0到9的区域内，说明此字符有效
                StringBuffer stringBuffer = new StringBuffer();
                for (char c : chars) {
                    if (c >= '0' && c <= '9') {
                        stringBuffer.append(c);
                    }
                }
                // 将字符数组转化成字符串，解析成long类型，并且乘1024转换成byte
                return Long.parseLong(stringBuffer.toString()) * 1024;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                try {
                    if (fileReader != null && bufferedReader != null) {
                        fileReader.close();
                        bufferedReader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }

//    public void clearItem(ProcessInfo item) {
//        killProcess(context, item);
//        if (iPorcess != null)
//            iPorcess.onClear();
//        refresh();
//    }
//
//    /**
//     * 杀死进程的方法
//     * @param context 上下文环境
//     * @param processInfo 传递进来的进程信息
//     */
//    public static void killProcess(Context context,ProcessInfo processInfo) {
//        // 1.获取ActivityManager对象
//        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        // 2.杀死指定包名进程（需要声明权限）
//        activityManager.killBackgroundProcesses(processInfo.getPackageName());
//    }

    // 获得可用的内存
    public static long getMemUnused(Context mContext) {
        long memUnused;
        // 得到ActivityManager
        ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        // 创建ActivityManager.MemoryInfo对象
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memInfo);

        // 取得剩余的内存空间
        memUnused = memInfo.availMem / 1024;
        return memUnused;
    }

    // 获得总内存
    public static long getMemTotal() {
        long memTotal;
        // /proc/meminfo读出的内核信息进行解释
        String path = "/proc/meminfo";
        String content = null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(path), 8);
            String line;
            if ((line = br.readLine()) != null) {
                content = line;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // beginIndex
        int begin = content.indexOf(':');
        // endIndex
        int end = content.indexOf('k');
        // 截取字符串信息
        content = content.substring(begin + 1, end).trim();
        long mTotal = Integer.parseInt(content);
        return mTotal;
    }

    /**
     * 获取手机内部空间总大小
     *
     * @return 大小，字节为单位
     */
    static public long getTotalInternalMemorySize() {
        //获取内部存储根目录
        File path = Environment.getDataDirectory();
        //系统的空间描述类
        StatFs stat = new StatFs(path.getPath());
        //每个区块占字节数
        long blockSize = stat.getBlockSizeLong();
        //区块总数
        long totalBlocks = stat.getBlockCountLong();
        return totalBlocks * blockSize;
    }

    /**
     * 获取手机内部可用空间大小
     *
     * @return 大小，字节为单位
     */
    static public long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        //获取可用区块数量
        long availableBlocks = stat.getAvailableBlocksLong();
        return availableBlocks * blockSize;
    }

    /**
     * 判断SD卡是否可用
     *
     * @return true : 可用<br>false : 不可用
     */
    public static boolean isSDCardEnable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 获取手机外部总空间大小
     *
     * @return 总大小，字节为单位
     */
    static public long getTotalExternalMemorySize() {
        if (isSDCardEnable()) {
            //获取SDCard根目录
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return totalBlocks * blockSize;
        } else {
            return -1;
        }
    }

    /**
     * 获取SD卡剩余空间
     *
     * @return SD卡剩余空间
     */
//    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static long getFreeSpace() {
        if (!isSDCardEnable()) return 0;
        File path=Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize, availableBlocks;
        availableBlocks = stat.getAvailableBlocksLong();
        blockSize = stat.getBlockSizeLong();
        long size = availableBlocks * blockSize / 1024L;
        return size;
    }

    /**
     * 获取SD卡信息
     *
     * @return SDCardInfo
     */
//    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static String getSDCardInfo() {
        SDCardInfo sd = new SDCardInfo();
        if (!isSDCardEnable()) return "sdcard unable!";
        sd.isExist = true;
        StatFs sf = new StatFs(Environment.getExternalStorageDirectory().getPath());
        sd.totalBlocks = sf.getBlockCountLong();
        sd.blockByteSize = sf.getBlockSizeLong();
        sd.availableBlocks = sf.getAvailableBlocksLong();
        sd.availableBytes = sf.getAvailableBytes();
        sd.freeBlocks = sf.getFreeBlocksLong();
        sd.freeBytes = sf.getFreeBytes();
        sd.totalBytes = sf.getTotalBytes();
        return sd.toString();
    }

    public static class SDCardInfo {
        boolean isExist;
        long totalBlocks;
        long freeBlocks;
        long availableBlocks;
        long blockByteSize;
        long totalBytes;
        long freeBytes;
        long availableBytes;

        @Override
        public String toString() {
            return "isExist=" + isExist +
                    "\ntotalBlocks=" + totalBlocks +
                    "\nfreeBlocks=" + freeBlocks +
                    "\navailableBlocks=" + availableBlocks +
                    "\nblockByteSize=" + blockByteSize +
                    "\ntotalBytes=" + totalBytes +
                    "\nfreeBytes=" + freeBytes +
                    "\navailableBytes=" + availableBytes;
        }
    }
}
