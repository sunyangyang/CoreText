package com.knowbox.base.online;

import org.json.JSONObject;

import com.hyena.framework.datacache.BaseObject;

/**
 * 七牛云上传信息
 * @author yangzc
 */
public class OnlineUploadInfo extends BaseObject {

	public String mToken;
	public long mExpiredTime;
	public String mDomain;

	@Override
	public void parse(JSONObject json) {
		super.parse(json);
		if(json.has("data")){
			json = json.optJSONObject("data");
		}
		mToken = json.optString("token");
		mExpiredTime = json.optLong("expiredTimeStamp");
		mDomain = json.optString("domainName");
	}
}
