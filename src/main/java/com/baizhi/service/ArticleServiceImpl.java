package com.baizhi.service;

import com.baizhi.annotation.RedisCache;
import com.baizhi.dao.ArticleDao;
import com.baizhi.dao.StarDao;
import com.baizhi.entity.Article;
import com.baizhi.repositoryofes.ArticleRepository;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private ArticleRepository articleRepository;

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

}
