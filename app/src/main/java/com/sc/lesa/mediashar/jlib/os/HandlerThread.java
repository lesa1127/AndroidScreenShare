package com.sc.lesa.mediashar.jlib.os;

import android.os.Handler;
import android.os.Message;

import com.sc.lesa.mediashar.jlib.util.TempBufferList;

public class HandlerThread extends Thread{

    private int sleeptime;
    private boolean exit;
    private TempBufferList<Message> tempBufferList;
    private Handler.Callback callback;

    public HandlerThread(){
        this(200,10,null);
    }

    public HandlerThread(int sleeptime){
        this(sleeptime,10,null);
    }

    public HandlerThread(int sleepTime,int maxMessage){
       this(sleepTime,maxMessage,null);
    }

    public HandlerThread(int sleepTime,int maxMessage, Handler.Callback callback){
        this.sleeptime=sleepTime;
        tempBufferList=new TempBufferList<>(maxMessage);
        this.callback=callback;
    }

    @Override
    public void run() {
        while (!exit){
            Message message = tempBufferList.lastValue();
            if (message!=null){
                if (callback==null) handleMessage(message);
                else callback.handleMessage(message);
            }
            try {
                if (sleeptime>0)sleep(sleeptime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void handleMessage(Message msg) {}

    public final boolean hasMessages(int what) {
        Message message =new Message();
        message.what=what;
        return hasMessages(message);
    }
    public final boolean hasMessages(int what, Object object) {
        Message message =new Message();
        message.what=what;
        message.obj=object;
        return hasMessages(message);

    }
    public final boolean hasMessages(Message message){
        if (tempBufferList.size()!=tempBufferList.getMaxLength()) {
            tempBufferList.push(message);
            return true;
        }
        return false;
    }
    public void exit(){
        this.exit=true;
    }

}
