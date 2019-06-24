package com.ttms.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.*;

public class ProductCode {
    public static String getMessagenumber() {
        String dataString="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuffer buffer=new StringBuffer();
        Random random=new Random();
        for(int i=0;i<4;i++) {
            int index=random.nextInt(52);
            String msg = dataString.substring(index, index+1);
            buffer.append(msg);

        }
        return buffer.toString().toUpperCase();
    }
    public static String generateCode(int len){
        len = Math.min(len, 8);
        int min = Double.valueOf(Math.pow(10, len - 1)).intValue();
        int num = new Random().nextInt(Double.valueOf(Math.pow(10, len + 1)).intValue() - 1) + min;
        return String.valueOf(num).substring(0,len);
    }

    public static void main(String[] args) {
        GetCode getCode=new GetCode();
        StringBuilder sb=new StringBuilder();
        GetDate getDate=new GetDate();
        FutureTask<String> futureTask1=new FutureTask<String>(getCode);
        FutureTask<String> futureTask2=new FutureTask<String>(getDate);
        FutureTask<String> futureTask4=new FutureTask<String>(getDate);
        FutureTask<String> futureTask3=new FutureTask<String>(getCode);
        FutureTask<String> futureTask5=new FutureTask<String>(getCode);
        try {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(futureTask1);
            executorService.execute(futureTask2);
            executorService.execute(futureTask3);
            executorService.execute(futureTask4);
            executorService.execute(futureTask5);
            while (true) {
                try {
                    if(futureTask1.isDone() && futureTask2.isDone() && futureTask3.isDone() && futureTask4.isDone() && futureTask5.isDone()){//  两个任务都完成
                        System.out.println("Done");
                        StringBuilder append = sb.append(futureTask1.get()).append("-")
                                .
                                append(futureTask3.get().substring(0, 3)).append("-").
                                append(futureTask2.get()).append("-").append(futureTask5.get().substring(0, 3)).append("-").append(futureTask4.get().substring(3, 5));
                        System.out.println(append);
                        executorService.shutdown();                          // 关闭线程池和服务
                        return;
                    }

                    if(!futureTask1.isDone()){ // 任务1没有完成，会等待，直到任务完成
                        System.out.println("FutureTask1 output="+futureTask1.get());
                    }
                    System.out.println("Waiting for FutureTask2 to complete");
                    String s = futureTask2.get(200L, TimeUnit.MILLISECONDS);
                    if(s !=null){
                        System.out.println("FutureTask2 output="+s);
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }catch(TimeoutException e){
                    //do nothing
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
    class GetCode implements Callable<String> {
        @Override
        public String call() throws Exception {
            String dataString = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
            StringBuffer buffer = new StringBuffer();
            Random random = new Random();
            for (int i = 0; i < 4; i++) {
                int index = random.nextInt(52);
                String msg = dataString.substring(index, index + 1);
                buffer.append(msg);
            }
            return buffer.toString().toUpperCase();
        }
    }

class GetDate implements Callable<String> {
    @Override
    public String call() throws Exception {
        SimpleDateFormat sd=new SimpleDateFormat("yyyyMMdd");
        String format = sd.format(new Date());
        return format;
    }
}
