package cn.stevekung.redis;

public interface KeyPrefix {
    public int expireSeconds();

    public String getPrefix();
}
