package com.betmansmall.game.desktop;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class MultipleRunner {
    public static void main(String[] args) {
        try {
            Object[] objects = {BulletTest1.class, BulletTest2.class, BulletTest3.class, BulletTest4.class, BulletTest5.class, BulletTest6.class, BulletTest7.class, BulletTest8.class};
//            Object[] objects = {DynamicBulletTest1.class, DynamicBulletTest2.class, DynamicBulletTest3.class, DynamicBulletTest4.class, DynamicBulletTest5.class, DynamicBulletTest6.class};
            for (int o = 0; o < objects.length; o++) {
                Object object = objects[o];
                int finalO = o;
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        int res = JavaProcess.exec((Class) object, "-p " + finalO);
                        System.out.println("res:" + res);
                    }
                };
                new Thread(r).start();
            }
//            int count = args.length != 0 ? Integer.parseInt(args[0]) : 4;
//            for (int k = 0; k <= count; k++) {
//                int finalK = k;
//                Runnable r = new Runnable() {
//                    @Override
//                    public void run() {
//                        int res = JavaProcess.exec(DesktopLauncher.class, "-p " + finalK);
//                        System.out.println("res:" + res);
//                    }
//                };
//                new Thread(r).start();
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final class JavaProcess {
        public static int exec(Class klass, String args) {
            try {
                String javaHome = System.getProperty("java.home");
                String javaBin = javaHome +
                        File.separator + "bin" +
                        File.separator + "java";
                String classpath = System.getProperty("java.class.path");
                String className = klass.getName();

                List<String> command = new LinkedList<String>();
                command.add(javaBin);
                command.add("-cp");
                command.add(classpath);
                command.add(className);
                if (args != null) {
                    command.add(args);
                }
//                System.out.println("command:" + command);
                ProcessBuilder builder = new ProcessBuilder(command);
                Process process = builder.inheritIO().start();
                process.waitFor();
                return process.exitValue();
            } catch (Exception exception) {
                exception.printStackTrace();
                return -1;
            }
        }
    }
}
