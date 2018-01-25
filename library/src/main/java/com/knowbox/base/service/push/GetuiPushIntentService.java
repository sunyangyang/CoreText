package com.knowbox.base.service.push;

import android.content.Context;
import android.content.Intent;

import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.utils.BaseApp;
import com.hyena.framework.utils.MsgCenter;
import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;
import com.igexin.sdk.message.FeedbackCmdMessage;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTNotificationMessage;
import com.igexin.sdk.message.GTTransmitMessage;
import com.igexin.sdk.message.SetTagCmdMessage;
import com.knowbox.base.R;

/**
 * Created by yangzc on 17/7/23.
 */

public class GetuiPushIntentService extends GTIntentService {

    private static final String TAG = "GetuiPushIntentService";

    @Override
    public void onReceiveServicePid(Context context, int pid) {
        LogUtil.v(TAG, "onReceiveServicePid, pid: " + pid);
    }

    @Override
    public void onReceiveClientId(Context context, String clientid) {
        LogUtil.v(TAG, "onReceiveClientId, clientid: " + clientid);
        try {
            PushService pushService = (PushService) BaseApp.getAppContext()
                    .getSystemService(PushService.SERVICE_NAME);
            pushService.registerDevice(clientid);
        }catch (Exception e){}
    }

    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage msg) {
        String appid = msg.getAppid();
        String taskid = msg.getTaskId();
        String messageid = msg.getMessageId();
        byte[] payload = msg.getPayload();
        String pkg = msg.getPkgName();
        String cid = msg.getClientId();
        // 第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
        boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
        LogUtil.d(TAG, "call sendFeedbackMessage = " + (result ? "success" : "failed"));
        LogUtil.d(TAG, "onReceiveMessageData -> " + "appid = " + appid + "\ntaskid = " + taskid + "\nmessageid = " + messageid + "\npkg = " + pkg
                + "\ncid = " + cid);

        if (payload == null) {
            LogUtil.e(TAG, "receiver payload = null");
        } else {
            String data = new String(payload);
            LogUtil.d(TAG, "receiver payload package name : " + getPackageName());
            LogUtil.d(TAG, "receiver payload = " + data);

            Intent intent = new Intent(PushService.BROADCAST_PUSH);
            intent.putExtra(PushService.ARGS_MSG, data);
            intent.setPackage(getPackageName());
            MsgCenter.sendGlobalBroadcast(intent);

        }
        LogUtil.d(TAG, "----------------------------------------------------------------------------------------------");
    }

    @Override
    public void onReceiveOnlineState(Context context, boolean online) {
        LogUtil.v(TAG, "onReceiveOnlineState -> " + (online ? "online" : "offline"));
    }

    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage cmdMessage) {
        LogUtil.d(TAG, "onReceiveCommandResult -> " + cmdMessage);

        int action = cmdMessage.getAction();

        if (action == PushConsts.SET_TAG_RESULT) {
            setTagResult((SetTagCmdMessage) cmdMessage);
        } else if ((action == PushConsts.THIRDPART_FEEDBACK)) {
            feedbackResult((FeedbackCmdMessage) cmdMessage);
        }
    }

    @Override
    public void onNotificationMessageArrived(Context context, GTNotificationMessage gtNotificationMessage) {

    }

    @Override
    public void onNotificationMessageClicked(Context context, GTNotificationMessage gtNotificationMessage) {

    }

    private void setTagResult(SetTagCmdMessage setTagCmdMsg) {
        String sn = setTagCmdMsg.getSn();
        String code = setTagCmdMsg.getCode();

        int text = R.string.add_tag_unknown_exception;
        switch (Integer.valueOf(code)) {
            case PushConsts.SETTAG_SUCCESS:
                text = R.string.add_tag_success;
                break;

            case PushConsts.SETTAG_ERROR_COUNT:
                text = R.string.add_tag_error_count;
                break;

            case PushConsts.SETTAG_ERROR_FREQUENCY:
                text = R.string.add_tag_error_frequency;
                break;

            case PushConsts.SETTAG_ERROR_REPEAT:
                text = R.string.add_tag_error_repeat;
                break;

            case PushConsts.SETTAG_ERROR_UNBIND:
                text = R.string.add_tag_error_unbind;
                break;

            case PushConsts.SETTAG_ERROR_EXCEPTION:
                text = R.string.add_tag_unknown_exception;
                break;

            case PushConsts.SETTAG_ERROR_NULL:
                text = R.string.add_tag_error_null;
                break;

            case PushConsts.SETTAG_NOTONLINE:
                text = R.string.add_tag_error_not_online;
                break;

            case PushConsts.SETTAG_IN_BLACKLIST:
                text = R.string.add_tag_error_black_list;
                break;

            case PushConsts.SETTAG_NUM_EXCEED:
                text = R.string.add_tag_error_exceed;
                break;

            default:
                break;
        }
        LogUtil.d(TAG, "settag result sn = " + sn + ", code = " + code + ", text = " + getResources().getString(text));
    }

    private void feedbackResult(FeedbackCmdMessage feedbackCmdMsg) {
        String appid = feedbackCmdMsg.getAppid();
        String taskid = feedbackCmdMsg.getTaskId();
        String actionid = feedbackCmdMsg.getActionId();
        String result = feedbackCmdMsg.getResult();
        long timestamp = feedbackCmdMsg.getTimeStamp();
        String cid = feedbackCmdMsg.getClientId();

        LogUtil.d(TAG, "onReceiveCommandResult -> " + "appid = " + appid + "\ntaskid = " + taskid + "\nactionid = " + actionid + "\nresult = " + result
                + "\ncid = " + cid + "\ntimestamp = " + timestamp);
    }
}
