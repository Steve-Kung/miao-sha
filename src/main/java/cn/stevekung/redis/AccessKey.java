package cn.stevekung.redis;

public class AccessKey extends BasePrefix{

	private AccessKey( int expireSeconds, String prefix) {
		super(expireSeconds, prefix);
	}
	
	public static AccessKey withExpire(int expireSeconds) {
		return new AccessKey(expireSeconds, "access");
	}

	// 有限期 5s
	public static AccessKey access = new AccessKey(5, "access");
	
}