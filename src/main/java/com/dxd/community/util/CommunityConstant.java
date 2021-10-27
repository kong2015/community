package com.dxd.community.util;

import java.security.PrivateKey;

/**
 * @author dxd
 * @create 2021-06-19 21:58
 */
public interface CommunityConstant {
    /**
     * 激活成功 重复 失败
     */
    int ACTIVATION_SUCCESS = 0;
    int ACTIVATION_REPEAT = 1;
    int ACTIVATION_FAILURE = 2;

    //设置cookie生命周期
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;
    int REMEMBER_EXPIRED_SECONDS = 3600 * 12 * 7;

    /**
     * 查询的是帖子还是评论
     */
    int ENTITY_TYPE_POST = 1;//帖子
    int ENTITY_TYPE_COMMENT = 2;//评论
    int ENTITY_TYPE_USER = 3;//作者

    /**
     * 主题：关注，点赞，评论
     */
    String TOPIC_COMMENT = "comment";
    String TOPIC_LIKE = "like";
    String TOPIC_FOLLOW = "follow";
    String TOPIC_PUBLISH = "publish";
    String TOPIC_DELETE = "delete";
    String TOPIC_SHARE = "share";

    int SYSTEM_USER_ID = 1;

    /**
     * 权限: 普通用户,数据库对应type  0
     */
    String AUTHORITY_USER = "user";

    /**
     * 权限: 管理员,数据库对应type  1
     */
    String AUTHORITY_ADMIN = "admin";

    /**
     * 权限: 版主,数据库对应type  2
     */
    String AUTHORITY_MODERATOR = "moderator";


}
