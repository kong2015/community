package com.dxd.community.service;

import com.dxd.community.dao.elasticsearch.DiscussPostRepository;
import com.dxd.community.entity.DiscussPost;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dxd
 * @create 2021-07-18 17:31
 */
@Service
public class ElasticsearchService {

    @Autowired
    private DiscussPostRepository discussPostRepository;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    public void saveDiscussPost(DiscussPost post) {
        discussPostRepository.save(post);
    }

    public void deleteDiscussPost(int id) {
        discussPostRepository.deleteById(id);
    }

    public Page<DiscussPost> searchDiscussPost(String keyword, int current, int limit){
        PageRequest pageable = PageRequest.of(current, limit);
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(QueryBuilders.multiMatchQuery(keyword, "title", "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(pageable)
                .withHighlightFields(new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>"))
                .build();

        SearchHits<DiscussPost> searchHits = elasticsearchRestTemplate.search(searchQuery, DiscussPost.class);
        if (searchHits.getTotalHits() <= 0){
            return null;
        }
        ArrayList<DiscussPost> discussPosts = new ArrayList<>();
        for (SearchHit<DiscussPost> searchHit : searchHits) {
            DiscussPost rowPost = searchHit.getContent();
            DiscussPost newPost = new DiscussPost();
            BeanUtils.copyProperties(rowPost, newPost);
            List<String> title = searchHit.getHighlightFields().get("title");
            if (title != null){
                newPost.setTitle(title.get(0));
            }
            List<String> content = searchHit.getHighlightFields().get("content");
            if (content != null){
                newPost.setContent(content.get(0));
            }
            discussPosts.add(newPost);
        }
        return new PageImpl<>(discussPosts, pageable, searchHits.getTotalHits());

    }
}