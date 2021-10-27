package com.dxd.community.util;


import jdk.internal.dynalink.beans.StaticClass;

import java.util.Date;

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

    private static final String PREFIX_UV = "uv";

    private static final String PREFIX_DAU = "dau";
    //保存变化的帖子，用于计算分数
    private static final String PREFIX_POST = "post";

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

    //日期数据传入年月日就够了，不用时分秒
    // 单日UV
    public static String getUVKey(String date) {
        return PREFIX_UV + SPLIT + date;
    }

    // 区间UV
    public static String getUVKey(String startDate, String endDate) {
        return PREFIX_UV + SPLIT + startDate + SPLIT + endDate;
    }

    // 单日活跃用户
    public static String getDAUKey(String date) {
        return PREFIX_DAU + SPLIT + date;
    }

    // 区间活跃用户
    public static String getDAUKey(String startDate, String endDate) {
        return PREFIX_DAU + SPLIT + startDate + SPLIT + endDate;
    }
    public static String getPostScoreKey(){
        return PREFIX_POST + SPLIT + "score";
    }

}
