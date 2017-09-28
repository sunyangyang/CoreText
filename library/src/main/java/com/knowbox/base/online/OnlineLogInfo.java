package com.knowbox.base.online;

import com.hyena.framework.datacache.BaseObject;

import org.json.JSONObject;

/**
 * Created by yangzc on 17/9/28.
 */

public class OnlineLogInfo extends BaseObject {

    public int mSize;//kb
    public int mInterval;//s
    public int mNumber;

    @Override
    public void parse(JSONObject json) {
        super.parse(json);
        JSONObject data = json.optJSONObject("data");
        if (data != null) {
            this.mSize = data.optInt("size");
            this.mInterval = data.optInt("interval");
            this.mNumber = data.optInt("number");
        }
    }
}
