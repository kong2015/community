package com.dxd.community.dao.elasticsearch;

import com.dxd.community.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @author dxd
 * @create 2021-07-18 11:24
 */
@Repository
// ElasticsearchRepository接口(已经实现了对实体类的增删改查)处理的实体类是DiscussPost，主键是Integer
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost, Integer> {
}