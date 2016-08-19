/**
 * Copyright (C) 2015 The AndroidRCStudent Project
 */
package com.knowbox.base.service.login;

import cn.sharesdk.framework.Platform;

/**
 * @author yangzc on 15/10/10.
 */
public interface ThirdPartyLoginListener {

    public void onLogin(Platform platform);

    public void onFail(Platform platform);

}
