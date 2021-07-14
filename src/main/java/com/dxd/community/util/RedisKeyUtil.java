package com.dxd.community.util;


/**
 * @author dxd
 * @create 2021-06-25 21:49
 */
public class RedisKeyUtil {
    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER_LIKE = "like:user";
    //followee 键表示我，值表示我关注了谁
    private static final String PREFIX_FOLLOWEE = "followee";
    //follower 键表示被关注者，值表示被谁关注了
    private static final String PREFIX_FOLLOWER = "follower";

    //缓存验证码的key
    private static final String PREFIX_KAPTCHA  ="kaptcha";
    //存储登录凭证
    private static final String PREFIX_TICKET = "ticket";
    //缓存用户信息
    private static final String PREFIX_USER = "user";

    public static String getEntityLikeKey(int entityType, int entityId){
        return PREFIX_ENTITY_LIKE + SPLIT + entityType + SPLIT + entityId;
    }

    public static String getUserLikeKey(int userId){
        return PREFIX_USER_LIKE + SPLIT + userId;
    }


    // 某个用户关注的实体
    // followee:userId:entityType -> zset(entityId,now)
    public static String getFolloweeKey(int userId, int entityType) {
        return PREFIX_FOLLOWEE + SPLIT + userId + SPLIT + entityType;
    }

    // 某个实体拥有的粉丝
    // follower:entityType:entityId -> zset(userId,now)
    public static String getFollowerKey(int entityType, int entityId) {
        return PREFIX_FOLLOWER + SPLIT + entityType + SPLIT + entityId;
    }

    public static String  getKaptchaKey(String kaptchaOwner){
        return PREFIX_KAPTCHA + SPLIT + kaptchaOwner;
    }

    public static String getTicketKey(String ticket){
        return PREFIX_TICKET + SPLIT + ticket;
    }

    public static String getUserKey(int userId){
        return PREFIX_USER + SPLIT + userId;
    }

}
