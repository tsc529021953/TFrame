package com.sc.lib_system.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 1.判断有无root权限
 * 2.shell 执行
 */
public class CommandUtil {

    // 以root权限运行
    public static final String COMMAND_SU = "su";
    // 不以root权限运行
    public static final String COMMAND_SH = "sh";
    public static final String COMMAND_EXIT = "exit\n";
    public static final String COMMAND_LINE_END = "\n";


    public static CommandResult runCommand(String command) {
        return runCommand(command, false);
    }

    public static CommandResult runCommand(String[] commands) {
        return runCommand(commands, false);
    }

    public static CommandResult runCommand(String command, boolean isRoot) {
        return runCommand(new String[]{command}, isRoot);
    }

    public static CommandResult runCommand(String[] commands , boolean isRoot){
        CommandResult res = new CommandResult();
        Process process = null;
        DataOutputStream os = null;
        StringBuilder successMsg = null;
        StringBuilder errorMsg = null;
        try {
            process = Runtime.getRuntime().exec(
                    isRoot ? COMMAND_SU : COMMAND_SH
            );

            if (commands.length == 0 && commands[0].equals("")) {
                // 只是检测吧
                return res;
            }
            os = new DataOutputStream(process.getOutputStream());
            for (String command: commands){
                if (command == null)continue;
                os.write(command.getBytes());
                os.writeBytes(COMMAND_LINE_END);
                os.flush();
            }
            os.writeBytes(COMMAND_EXIT);
            os.flush();
            res.result = process.waitFor();
            BufferedReader successReader = new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(
                    process.getErrorStream()));
            String s;
            if (successReader != null) {
                successMsg = new StringBuilder();
                while ((s = successReader.readLine()) != null) {
                    successMsg.append(s);
                }
                successReader.close();
                if (successMsg != null)
                    res.successMsg = successMsg.toString();
            }

            if (errorReader != null) {
                errorMsg = new StringBuilder();
                while ((s = errorReader.readLine()) != null) {
                    errorMsg.append(s);
                }
                errorReader.close();
                if (errorMsg != null)
                    res.errorMsg = errorMsg.toString();
            }
        } catch (IOException e) {
            res.result = -1;
            res.errorMsg = e.toString();
            return res;
        }
        catch (InterruptedException e) {
            res.result = -3;
            res.errorMsg = e.toString();
            return res;
        }
        finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (process != null) {
                process.destroy();
            }
        }

        return res;
    }

    public static String apkPorcess(String cmd){
        Process process = null;
        DataOutputStream os = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = null;
        StringBuilder errorMsg = null;
        try {
            //卸载也需要root权限
            process =Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.write(cmd.getBytes());
            os.writeBytes("\n");
            os.writeBytes("exit\n");
            os.flush();
            //执行命令
            process.waitFor();
            //获取返回结果
            successMsg = new StringBuilder();
            errorMsg = new StringBuilder();
            successResult = new BufferedReader(new InputStreamReader
                    (process.getInputStream()));
            errorResult = new BufferedReader
                    (new InputStreamReader(process.getErrorStream()));
            String s;
            while ((s =successResult.readLine()) != null) {
                successMsg.append(s);
            }
            while ((s = errorResult.readLine())!= null) {
                errorMsg.append(s);
            }
        } catch (Exception e) {
            // 此处应该是没有权限
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (process != null) {
                    process.destroy();
                }
                if (successResult != null) {
                    successResult.close();
                }
                if (errorResult != null) {
                    errorResult.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //显示结果
        return "成功消息：" + (successMsg == null ? "" : successMsg.toString()) +"\n" + "错误消息: " +  (errorMsg == null ? "" : errorMsg.toString()) ;
    }

    /**
     * 检测root权限
     * @return
     */
    public static boolean checkRoot(){
        CommandResult res = runCommand("", true);
        if (res.result == -1)return false;
        return true;
    }

    public static class CommandResult {
        /** 运行结果 **/
        public int result = 0;
        /** 运行成功结果 **/
        public String successMsg;
        /** 运行失败结果 **/
        public String errorMsg;
        public CommandResult() {
        }
        public CommandResult(int result, String successMsg, String errorMsg) {
            this.result = result;
            this.successMsg = successMsg;
            this.errorMsg = errorMsg;
        }

        @Override
        public String toString() {
            return "CommandResult{" +
                    "result=" + result +
                    ", successMsg='" + successMsg + '\'' +
                    ", errorMsg='" + errorMsg + '\'' +
                    '}';
        }
    }
}
