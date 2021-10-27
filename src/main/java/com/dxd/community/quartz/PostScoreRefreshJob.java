package com.dxd.community.quartz;

import com.dxd.community.entity.DiscussPost;
import com.dxd.community.service.CommentService;
import com.dxd.community.service.DiscussPostService;
import com.dxd.community.service.ElasticsearchService;
import com.dxd.community.service.LikeService;
import com.dxd.community.util.CommunityConstant;
import com.dxd.community.util.RedisKeyUtil;
import lombok.experimental.PackagePrivate;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author dxd
 * @create 2021-07-19 9:43
 */
@Slf4j
public class PostScoreRefreshJob implements Job, CommunityConstant {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ElasticsearchService elasticsearchService;
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private LikeService likeService;

    private static final Date EPOCH;

    static {
        try {
            EPOCH = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-08-01 00:00:00");
        } catch (ParseException e) {
            throw new RuntimeException("初始化牛客纪元失败！", e);
        }
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String redisKey = RedisKeyUtil.getPostScoreKey();
        BoundSetOperations operations = redisTemplate.boundSetOps(redisKey);
        if (operations.size() == 0){
            log.info("任务取消，还没有需要刷新的帖子");
            return;
        }
        log.info("[任务开始] 正在刷新帖子分数：" + operations.size());
        while (operations.size() > 0){
            this.refresh((Integer) operations.pop());
        }
        log.info("[任务结束] 帖子分数刷新完毕！");
    }

    public void refresh(int postId){
        DiscussPost post = discussPostService.findDiscussPostById(postId);
        if (post == null) {
            log.error("该帖子不存在：id = " + postId);
            return;
        }
        int status = post.getStatus() == 1 ? 75 : 0;
        int commentCount = post.getCommentCount();
        long entityLikeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, postId);
        double weight = status + commentCount * 10 + entityLikeCount * 2;
        double score = Math.log10(Math.max(1, weight)) + (post.getCreateTime().getTime() - EPOCH.getTime()) / (1000 * 3600 * 24);

        discussPostService.updateScore(postId, score);
        post.setScore(score);
        elasticsearchService.saveDiscussPost(post);
    }
}