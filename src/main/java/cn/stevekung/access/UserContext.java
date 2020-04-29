package cn.stevekung.access;


import cn.stevekung.domain.MiaoshaUser;

public class UserContext {

	// 每个线程独有的ThreadLocal 不共享
	private static ThreadLocal<MiaoshaUser> userHolder = new ThreadLocal<MiaoshaUser>();
	
	public static void setUser(MiaoshaUser user) {
		userHolder.set(user);
	}
	
	public static MiaoshaUser getUser() {
		return userHolder.get();
	}

}
