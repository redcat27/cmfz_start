package com.baizhi.service;

import com.baizhi.annotation.RedisCache;
import com.baizhi.dao.ArticleDao;
import com.baizhi.dao.StarDao;
import com.baizhi.entity.Article;
import com.baizhi.repositoryofes.ArticleRepository;
import org.apache.commons.collections4.IterableUtils;
import org.apache.ibatis.session.RowBounds;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service("articleService")
@Transactional
public class ArticleServiceImpl implements ArticleService {
    @Autowired
    private ArticleDao articleDao;
    @Autowired
    private StarDao starDao;
    //注入操作es数据库的接口ArticleRepository

    //注入全文检索的依赖，操作es的对象
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;


    /**
     * 分页查所有
     * @param page
     * @param rows
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    @RedisCache
    public Map<String, Object> findAllByPage(Integer page, Integer rows) {
        Article article = new Article();
        RowBounds rowBounds = new RowBounds((page - 1) * rows, rows);
        List<Article> articles = articleDao.selectByRowBounds(article, rowBounds);
        int count = articleDao.selectCount(article);
        Map<String, Object> map = new HashMap<>();
        map.put("page",page);
        map.put("rows",articles);
        map.put("total",count%rows==0?count/rows:count/rows+1);
        map.put("records",rows);
        return map;
    }


    /**
     * 添加一个文章
     * @param article
     */
    @Override
    public void save(Article article) {
        article.setId(UUID.randomUUID().toString())
                .setCreateDate(new Date());
        int i = articleDao.insert(article);
        if (i==0) throw new RuntimeException("添加文章失败");
        //数据添加数据库成功后，再保存到es检索数据库中
        //索引和类型以及_id都在article实体类中指明了
        articleRepository.index(article);
    }


    /**
     * 修改一个文章
     * @param article
     */
    @Override
    public void update(Article article) {
        int i = articleDao.updateByPrimaryKeySelective(article);
        if (i==0) throw new RuntimeException("修改文章失败");
        //根据主键，从数据库中查询更改后的数据
        Article articleUpdated = articleDao.selectByPrimaryKey(article.getId());
        //将更改后的数据同步到es检索数据库中
        articleRepository.index(articleUpdated);
    }

    @Override
    public void delete(String id) {
        int i = articleDao.delete(new Article().setId(id));
        if (i==0) throw new RuntimeException("删除文章失败");
        //判断elasticsearch中是否存在要删除的检索文档
        //boolean b = articleRepository.existsById(id);
        //if (b){ //如果存在，则执行删除方法
        //删除数据库中的数据后，删除es检索数据库中的数据
        articleRepository.delete(new Article().setId(id));
        //}
    }


    /**
     * 全文检索执行的方法
     *
     * @param content
     * @return
     */
    @Override
    public List<Article> search(String content) {
        //判断用户输入的内容是否为空
        if ("".equals(content) || content == null) {
            //如果是空表示查所有
            Iterable<Article> all = articleRepository.findAll();
            List<Article> articles = IterableUtils.toList(all);
            return articles;
        } else {
            //否则根据条件查询
            //1. 定义高亮展示
            HighlightBuilder highlightBuilder = new HighlightBuilder()
                    .field("*")
                    .preTags("<span style='color:red'>")
                    .postTags("</span>")
                    .requireFieldMatch(false);

            NativeSearchQuery search = new NativeSearchQueryBuilder()
                    .withQuery(QueryBuilders.queryStringQuery(content)
                            .field("title").field("author")
                            .field("brief").field("content"))
                    .withSort(SortBuilders.scoreSort())
                    .withHighlightBuilder(highlightBuilder)
                    .build();

            AggregatedPage<Article> articles =
                    elasticsearchTemplate.queryForPage(search, Article.class, new SearchResultMapper() {
                        //对查询回来的结果进行处理
                        @Override
                        public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                            SearchHits hits = searchResponse.getHits();
                            SearchHit[] hits1 = hits.getHits();
                            List<Article> list = new ArrayList<>();
                            for (SearchHit hit : hits1) {
                                Article article = new Article();
                                Map<String, Object> map = hit.getSourceAsMap();
                                article.setId(map.get("id").toString());
                                article.setTitle(map.get("title").toString());
                                article.setAuthor(map.get("author").toString());
                                article.setBrief(map.get("brief").toString());
                                article.setContent(map.get("content").toString());
                                String createDate = map.get("createDate").toString();
                                article.setCreateDate(new Date(Long.valueOf(createDate)));

                                //高亮，即返回的结果中高亮的字段是否有数据，有，则返回带高亮的
                                Map<String, HighlightField> fieldMap = hit.getHighlightFields();
                                if (fieldMap.get("title") != null) {
                                    article.setTitle(fieldMap.get("title").getFragments()[0].toString());
                                }
                                if (fieldMap.get("author") != null) {
                                    article.setAuthor(fieldMap.get("author").getFragments()[0].toString());
                                }
                                if (fieldMap.get("prief") != null) {
                                    article.setBrief(fieldMap.get("privef").getFragments().toString());
                                }
                                if (fieldMap.get("content") != null) {
                                    article.setContent(fieldMap.get("content").getFragments()[0].toString());
                                }
                                list.add(article);
                            }
                            return new AggregatedPageImpl<T>((List<T>) list);
                        }

                        @Override
                        public <T> T mapSearchHit(SearchHit searchHit, Class<T> aClass) {
                            return null;
                        }
                    });
            List<Article> articleList = articles.getContent();
            return articleList;
        }

    }


}
