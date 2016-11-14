package com.malalaoshi.android.exception;

/**
 * Created by kang on 16/11/14.
 */

public class MalaRuntimeException extends RuntimeException {
    public <T> MalaRuntimeException(Class<T> t, String msg){
        super(t.getName()+":"+msg);
    }

}
