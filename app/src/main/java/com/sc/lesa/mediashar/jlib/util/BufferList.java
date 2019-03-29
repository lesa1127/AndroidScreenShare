package com.sc.lesa.mediashar.jlib.util;

public abstract class BufferList<T> {
    protected int length;
    protected int maxLength;

    public BufferList(int len){
        this.maxLength=len;
    }

    public abstract void push(T t);

    public abstract T pop();

    public abstract T lastValue();

    public abstract int getMaxLength();
    public abstract int size();
    public abstract T[] getAll(T[] tmp);
    public abstract void clear();
}
