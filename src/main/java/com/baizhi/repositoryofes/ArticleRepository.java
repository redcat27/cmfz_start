package com.baizhi.repositoryofes;

import com.baizhi.entity.Article;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ArticleRepository extends ElasticsearchRepository<Article, String> {
    //在此可以自定义查询方法

    //自定义根据标题检索所有文章的方法
    //public List<Article> findAllByTitle(String keyword);
}
