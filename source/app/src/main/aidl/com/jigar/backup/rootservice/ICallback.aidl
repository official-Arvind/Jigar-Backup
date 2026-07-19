package com.jigar.backup.rootservice;

interface ICallback {
    void onProgress(long bytesWritten, long speed, float progress);
}
