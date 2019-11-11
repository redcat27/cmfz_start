package com.baizhi.controller;

import com.baizhi.entity.Article;
import com.baizhi.repositoryofes.ArticleRepository;
import com.baizhi.service.ArticleService;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.io.FilenameUtils;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("article")
public class ArticleController {
    @Autowired
    private ArticleService articleService;
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
    @RequestMapping("findAllByPage")
    public Map<String ,Object> findAllByPage(Integer page, Integer rows){
        return articleService.findAllByPage(page,rows);
    }


    /**
     * 添加一个文章
     * @param article
     * @return
     */
    @RequestMapping("save")
    public Map<String ,Object> save(Article article){
        Map<String, Object> map = new HashMap<>();
        try {
            articleService.save(article);
            map.put("status",true);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("status",false);
            map.put("message",e.getMessage());
        }
        return map;
    }


    /**
     * 修改文章
     * @param article
     * @return
     */
    @RequestMapping("update")
    public Map<String ,Object> update(Article article){
        Map<String, Object> map = new HashMap<>();
        try {
            articleService.update(article);
            map.put("status",true);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("status",false);
            map.put("message",e.getMessage());
        }
        return map;
    }


    /**
     * 删除一个文章的方法
     * @param id
     * @return
     */
    @RequestMapping("delete")
    public Map<String ,Object> delete(String id){
        Map<String, Object> map = new HashMap<>();
        try {
            articleService.delete(id);
            map.put("status",true);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("status",false);
            map.put("message",e.getMessage());
        }
        return map;
    }


    /**
     * 文章的图片上传方法
     * @param articleImg
     * @param request
     * @return
     */
    @RequestMapping("upload")
    public Map<String ,Object> upload(MultipartFile articleImg, HttpServletRequest request){
        HashMap<String, Object> map = new HashMap<>();
        String realPath = request.getServletContext().getRealPath("/article/image");
        try {
            articleImg.transferTo(new File(realPath, articleImg.getOriginalFilename()));
            map.put("error",0);
            map.put("url","http://127.0.0.1:8989/start/article/image/"+articleImg.getOriginalFilename());
        } catch (IOException e) {
            e.printStackTrace();
            map.put("error",1);
        }
        return map;
    }


    /**
     * 编辑器中的图片空间，回显图片空间中的所有图片的方法
     *
     * @param request
     * @return
     */
    @RequestMapping("imageSpace")
    public Map<String ,Object> imageSpace(HttpServletRequest request){
        Map<String, Object> map = new HashMap<>();
        File file = new File(request.getServletContext().getRealPath("/article/image"));
        File[] files = file.listFiles();
        ArrayList<Object> list = new ArrayList<>();
        for (File img : files) {
            Map<String, Object> imgObject = new HashMap<>();
            imgObject.put("is_dir",false);
            imgObject.put("has_file",false);
            imgObject.put("filesize",img.length());
            imgObject.put("is_photo",true);
            imgObject.put("filetype", FilenameUtils.getExtension(img.getName()));
            imgObject.put("filename",img.getName());
            imgObject.put("datetime","2018-06-06 00:36:39");
            list.add(imgObject);
        }
        map.put("file_list",list);
        map.put("total_count",list.size());
        map.put("current_url","http://127.0.0.1:8989/start/article/image/");
        return map;
    }


    /**
     * 全文检索执行的方法
     *
     * @param content
     * @return
     */
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
