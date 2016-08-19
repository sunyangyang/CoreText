/**
 * Copyright (C) 2015 The AndroidRCStudent Project
 */
package com.knowbox.base.service.upload;

import java.io.File;
import java.util.UUID;

/**
 * 上传任务
 * @author yangzc on 15/9/28.
 */
public class UploadTask {

	public static final int TYPE_PICTURE = 1;
    public static final int TYPE_RECORDER = 2;
	
    private String taskId = "";
    private int type = TYPE_PICTURE;
    
    public String filePath;
    public byte[] buf;

    public UploadTask(int type, String filePath) {
        this.filePath = filePath;
        taskId = UUID.randomUUID().toString();
    }

    public UploadTask(int type, byte[] buf) {
        this.buf = buf;
        taskId = UUID.randomUUID().toString();
    }

    public String getTaskId() {
        return taskId;
    }
    
    /**
     * 获得上传文件类型
     * @return
     */
    public int getType(){
    	return type;
    }

    /**
     * 数据是否为空
     * @return
     */
    public boolean isEmpty() {
        if ((filePath != null && new File(filePath).exists())
        		|| buf != null) {
            return false;
        }
        return true;
    }
}
