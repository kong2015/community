package com.dxd.community.dao;//package com.dxd.community.dao;

import com.dxd.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author dxd
 * @create 2021-06-16 18:08
 */
@Mapper
public interface DiscussPostMapper {
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit);
    //用于给参数取别名
    //如果方法只有一个参数，且该参数需要在<if>中使用，则必须使用@param
    int selectDiscussPostRows(@Param("userId") int userId);

    int insertDiscussPost(DiscussPost discussPost);

    DiscussPost selectDiscussPostById(int id);

    int updateCommentCount(int id, int commentCount);

}
