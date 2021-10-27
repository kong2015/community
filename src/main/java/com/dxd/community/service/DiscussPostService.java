package com.dxd.community.service;

import com.dxd.community.dao.DiscussPostMapper;
import com.dxd.community.entity.DiscussPost;
import com.dxd.community.util.SensitiveFilter;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author dxd
 * @create 2021-06-18 16:32
 */
@Slf4j
@Service
public class DiscussPostService {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter  sensitiveFilter;

    @Value("${caffeine.posts.max-size}")
    private int maxSize;

    @Value("${caffeine.posts.expire-seconds}")
    private int expireSeconds;

    // Caffeine核心接口: Cache, LoadingCache, AsyncLoadingCache

    // 帖子列表缓存。缓存都是按key缓存value
    private LoadingCache<String, List<DiscussPost>> postListCache;
    // 帖子总数缓存
    private LoadingCache<Integer, Integer> postRowsCache;
    
    @PostConstruct
    public void init(){
        postListCache = Caffeine.newBuilder().maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<DiscussPost>>() {
                    @Nullable
                    @Override
                    //build()表示没有数据的时候从哪来获取
                    public List<DiscussPost> load(@NonNull String key) throws Exception {
                        if (key == null || key.length() == 0){
                            throw new IllegalArgumentException("参数错误");
                        }
                        String[] params = key.split(":");
                        if (params == null || params.length != 2){
                            throw new IllegalArgumentException("参数错误");
                        }
                        Integer offset = Integer.valueOf(params[0]);
                        Integer limit = Integer.valueOf(params[1]);

                        // 二级缓存: Redis -> mysql
                        log.debug("load post list from DB.");
                        return discussPostMapper.selectDiscussPosts(0, offset, limit, 1);
                    }
                });
        postRowsCache = Caffeine.newBuilder().maximumSize(maxSize)
                .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                .build(new CacheLoader<Integer, Integer>() {
                    @Nullable
                    @Override
                    public Integer load(@NonNull Integer key) throws Exception {
                        log.debug("load post rows from DB.");
                        return discussPostMapper.selectDiscussPostRows(key);
                    }
                });
    }

    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit, int orderMode){
        if (userId == 0 && orderMode == 1){
            return postListCache.get(offset + ":" + limit);
        }
        log.debug("load post rows from DB.");
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(userId, offset, limit, orderMode);
        return discussPosts;
    }

    public int findDiscussPostRows(int userId) {
        if (userId == 0){
            return postRowsCache.get(userId);
        }
        log.debug("load post rows from DB.");
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    public int addDiscussPost(DiscussPost discussPost){
        if (discussPost == null){
            throw new IllegalArgumentException("帖子为空");
        }
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
        discussPost.setContent(sensitiveFilter.filter(discussPost.getContent()));
        discussPost.setTitle(sensitiveFilter.filter(discussPost.getTitle()));
        //异常后续统一处理
        return discussPostMapper.insertDiscussPost(discussPost);
    }

    public DiscussPost findDiscussPostById(int id){
        return discussPostMapper.selectDiscussPostById(id);
    }

    public int updateCommentCount(int id, int commentCount){
        return discussPostMapper.updateCommentCount(id, commentCount);
    }


    public int updateType(int id, int type){
        return discussPostMapper.updateType(id, type);
    }

    public int updateStatus(int id, int status){
        return discussPostMapper.updateStatus(id, status);
    }

    public int updateScore(int id, double score){
        return discussPostMapper.updateScore(id, score);
    }
}
