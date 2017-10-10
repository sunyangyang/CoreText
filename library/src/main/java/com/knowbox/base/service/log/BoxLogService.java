package com.knowbox.base.service.log;

/**
 * Created by yangzc on 17/10/10.
 */
public interface BoxLogService extends LogService {

    void sendNetLog(String actionCode, String extensionJson, String pageCode, String pageFrom);

    void writeLocalLog(String actionCode, String extensionJson, String pageCode, String pageFrom);

}
