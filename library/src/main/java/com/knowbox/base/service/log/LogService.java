package com.knowbox.base.service.log;

import com.hyena.framework.service.BaseService;

/**
 * Created by yangzc on 17/9/19.
 */
public interface LogService extends BaseService {

    //服务名称
    String SERVICE_NAME = "srv_log";

    /**
     * 同步本地日志到服务器
     */
    void syncLogToServer();

    /**
     * 发送在线日志
     * @param log 日志
     */
    void sendNetLog(String log);

    /**
     * 保存本地日志
     * @param log 日志
     */
    void writeLocalLog(String log);
}
