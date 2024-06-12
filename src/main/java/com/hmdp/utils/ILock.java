package com.hmdp.utils;

public interface ILock {
    boolean tyrLock(long timeoutSec);

    void unlock();
}
    