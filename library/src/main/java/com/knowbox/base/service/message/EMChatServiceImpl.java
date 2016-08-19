/**
 * Copyright (C) 2015 The WorkBox Project
 */
package com.knowbox.base.service.message;

import java.util.List;

import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.GroupChangeListener;
import com.easemob.chat.OnMessageNotifyListener;
import com.easemob.exceptions.EaseMobException;
import com.hyena.framework.clientlog.LogUtil;
import com.hyena.framework.debug.DebugUtils;
import com.hyena.framework.utils.BaseApp;

/**
 * 环信消息服务实现类
 * @author yangzc
 */
public class EMChatServiceImpl implements EMChatService {

	private static final boolean DEBUG = true;
	
	//SDK是否已经初始化
	private static boolean mSdkInited = false;
	//监听器
	private EMChatServiceObserver mChatServiceObserver = new EMChatServiceObserver();
	
	@Override
	public boolean initEMChat() {
		if (mSdkInited) {
			return true;
		}
		// 初始化EM聊天
		EMChat.getInstance().init(BaseApp.getAppContext());
		// 设置沙箱模式 慎用
//		EMChat.getInstance().setEnv(EMEnvMode.EMSandboxMode);
		// 开始Debug模式
		EMChat.getInstance().setDebugMode(true);
//		EMChat.getInstance().setAutoLogin(false);
		// 获取到EMChatOptions对象
		EMChatOptions options = EMChatManager.getInstance().getChatOptions();
		// 默认添加好友时，是不需要验证的，改成需要验证
		options.setAcceptInvitationAlways(false);
		// 默认环信是不维护好友关系列表的，如果app依赖环信的好友关系，把这个属性设置为true
		options.setUseRoster(false);
		// 设置收到消息是否有新消息通知(声音和震动提示)，默认为true
		options.setNotifyBySoundAndVibrate(true);
		// 设置收到消息是否有声音提示，默认为true
		options.setNoticeBySound(true);
		// 设置收到消息是否震动 默认为true
		options.setNoticedByVibrate(true);
		// 设置语音消息播放是否设置为扬声器播放 默认为true
		options.setUseSpeaker(true);
		// 设置是否需要已读回执
		options.setRequireAck(true);
		// 设置是否需要已送达回执
		options.setRequireDeliveryAck(true);
		// 设置notification消息点击时，跳转的intent为自定义的intent
//		options.setOnNotificationClickListener(getNotificationClickListener());
		options.setNotifyText(new OnMessageNotifyListener() {
			@Override
			public int onSetSmallIcon(EMMessage message) {
				return 0;
			}
			
			@Override
			public String onSetNotificationTitle(EMMessage message) {
				return null;
			}

			@Override
			public String onLatestMessageNotify(EMMessage message, int arg1, int arg2) {
				return null;
			}
			
			@Override
			public String onNewMessageNotify(EMMessage message) {
				if(message == null)
					return null;
				return message.getStringAttribute("userName", "") + "发来一条消息";
			}
			
		});
		//注册消息监听器
		registListener();
        mSdkInited = true;
		return true;
	}
	
	@Override
	public void loginEMChat(String userId, String password, final String userName) {
		//验证是否已经登录
		DebugUtils.addAssert(BaseApp.isLogin());
		EMChatManager.getInstance().login(userId, password, new EMCallBack() {
			@Override
			public void onSuccess() {
				try {
					//第一次登录或者之前logout后再登录，加载所有本地群和回话
					EMGroupManager.getInstance().loadAllGroups();
					EMChatManager.getInstance().loadAllConversations();
					//获取群聊列表(群聊里只有GroupId和GroupName等简单信息，不包含members),SDK会把群组存入到内存和DB中
					EMGroupManager.getInstance().getGroupsFromServer();
					//注册连接状态监听器
					EMChatManager.getInstance().addConnectionListener(mEmConnectionListener);
					//添加联系人监听器
					EMContactManager.getInstance().setContactListener(mEmContactListener);
					//注册群组状态变化监听器
					EMGroupManager.getInstance().addGroupChangeListener(mGroupChangeListener);
					//通知SDK，UI 已经初始化完毕，注册了相应的receiver和listener, 可以接受broadcast了
					EMChat.getInstance().setAppInited();
					//更新昵称
					EMChatManager.getInstance().updateCurrentUserNick(userName);
				} catch (EaseMobException e) {
					e.printStackTrace();
				} finally {
					getObserver().notifyEMChatLoginSuccess();
				}
			}
			
			@Override
			public void onProgress(int arg0, String arg1) {}
			@Override
			public void onError(int errorCode, String message) {
				getObserver().notifyEMChatLoginError(errorCode, message);
			}
		});
	}
	
	@Override
	public void logoutEMChat() {
		EMChatManager.getInstance().logout();
		EMChatManager.getInstance().removeConnectionListener(mEmConnectionListener);
		//添加联系人监听器
		EMContactManager.getInstance().removeContactListener();
		//注册群组状态变化监听器
		EMGroupManager.getInstance().removeGroupChangeListener(mGroupChangeListener);
	}

	@Override
	public EMChatServiceObserver getObserver() {
		return mChatServiceObserver;
	}
	
	/**
	 * 注册消息监听器
	 */
	private void registListener(){
		EMChatManager.getInstance().registerEventListener(
				mListener,
				new EMNotifierEvent.Event[] {
						EMNotifierEvent.Event.EventNewMessage,
						EMNotifierEvent.Event.EventDeliveryAck,
						EMNotifierEvent.Event.EventReadAck,
						EMNotifierEvent.Event.EventNewCMDMessage });
	}
	
	/**
	 * 解注册消息监听器
	 */
	private void unRegistReceiver(){
		EMChatManager.getInstance().unregisterEventListener(mListener);
	}
	
	@Override
	public void releaseAll() {
		unRegistReceiver();
	}
	
	private EMEventListener mListener = new EMEventListener() {

		@Override
		public void onEvent(EMNotifierEvent event) {
			//获取到message
			EMMessage message = (EMMessage) event.getData();
			switch (event.getEvent()) {
			case EventNewMessage:
				getObserver().notifyNewMessage(message);
				break;
			case EventDeliveryAck:
			case EventReadAck:
				if (message != null) {
					message.isAcked = true;
				}
				getObserver().notifyMessageStateChange(message);
				break;
			case EventNewCMDMessage:
				getObserver().notifyNewCMDMessage(message);
				break;
			default:
				break;
			}
		}
	};
	
	private EMContactListener mEmContactListener = new EMContactListener() {
		
		@Override
		public void onContactAdded(List<String> usernameList) {
		}

		@Override
		public void onContactDeleted(final List<String> usernameList) {
		}

		@Override
		public void onContactInvited(String username, String reason) {
		}

		@Override
		public void onContactAgreed(String username) {
		}

		@Override
		public void onContactRefused(String username) {
			// 参考同意，被邀请实现此功能
		}
	};
	
	private EMConnectionListener mEmConnectionListener = new EMConnectionListener() {
		
		@Override
		public void onDisconnected(final int error) {
			debug("onDisconnected");
			getObserver().notifyEMDisConnection(error);
		}
		
		@Override
		public void onConnected() {
			debug("onConnected");
			getObserver().notifyEMConnectioned();
		}
	};
	
	private GroupChangeListener mGroupChangeListener = new GroupChangeListener() {
		
		@Override
		public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {
		}

		@Override
		public void onInvitationAccpted(String groupId, String inviter, String reason) {

		}

		@Override
		public void onInvitationDeclined(String groupId, String invitee, String reason) {

		}

		@Override
		public void onUserRemoved(String groupId, String groupName) {
			// 提示用户被T了，demo省略此步骤
		}

		@Override
		public void onGroupDestroy(String groupId, String groupName) {
			// 群被解散
			// 提示用户群被解散,demo省略
		}

		@Override
		public void onApplicationReceived(String groupId, String groupName, String applyer, String reason) {
			// 用户申请加入群聊
		}

		@Override
		public void onApplicationAccept(String groupId, String groupName, String accepter) {
			// 加群申请被同意
		}

		@Override
		public void onApplicationDeclined(String groupId, String groupName, String decliner, String reason) {
			// 加群申请被拒绝，demo未实现
		}
	};

	/**
	 * 输出debug信息
	 * @param txt
	 */
	private void debug(String txt){
		if (DEBUG) {
			LogUtil.v("EMChatService", txt);
		}
	}
}
