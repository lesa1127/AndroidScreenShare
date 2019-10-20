package com.sc.lesa.mediashar.jlib.util;


public class TempBufferList<T> extends BufferList<T> {

    Object[] array ;

    AtomicBoolean atomicBoolean = new AtomicBoolean(false);


    public void setLock(){
        atomicBoolean.weakCompareAndSet(false,true);
    }
    public void unLock(){
        atomicBoolean.set(false);
    }

    public TempBufferList(int len) {
        super(len);
        array = new Object[maxLength];
    }

    @Override
    public void push(T t) {
        setLock();
        moveToBackOne(array);
        array[0]=t;
        if (length<maxLength){
            length++;
        }
        unLock();
    }

    @Override
    public T pop() {
        setLock();
        if (length>0) {
            T tmp = (T) array[0];
            moveToForground(array);
            length--;
            unLock();
            return tmp;
        }else {
            unLock();
            return null;
        }
    }

    @Override
    public T lastValue() {
        setLock();
        if (length>0) {
            length--;
            unLock();
            return (T) array[length];
        }
        else {
            unLock();
            return null;
        }
    }

    @Override
    public int size() {
        return length;
    }

    @Override
    public T[] getAll(T[] tmp) {
        setLock();
        if (tmp.length>=length) {
            for (int i = 0;i<length;i++){
                tmp[i]=(T) array[i];
            }
            unLock();
            return tmp;
        }
        unLock();
        return null;
    }

    private void moveToBackOne(Object[] array){
        for (int i = maxLength-2;i>=0;i--){
            array[i+1]=array[i];
        }
    }

    private void moveToForground(Object[] array){
        for(int i = 1;i<array.length;i++){
            array[i-1]=array[i];
        }
    }

    @Override
    public String toString() {
        setLock();
        StringBuffer buffer = new StringBuffer();
        buffer.append("[");
        for(int i = 0 ;i<length;i++){
            if (i==length-1){
                buffer.append(((T)array[i]).toString());
            }
            else {
                buffer.append(((T)array[i]).toString());
                buffer.append(",");
            }
        }
        buffer.append("]");
        unLock();
        return buffer.toString();
    }

    @Override
    public void clear() {
        setLock();
        array = new Object[maxLength];
        length=0;
        unLock();
    }

    @Override
    public int getMaxLength() {
        return this.maxLength;
    }
}
