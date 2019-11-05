package com.baizhi.service;

import com.baizhi.dao.ArticleDao;
import com.baizhi.dao.StarDao;
import com.baizhi.entity.Article;
import com.baizhi.entity.Star;
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

    /**
     * 分页查所有
     * @param page
     * @param rows
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
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
    }


    /**
     * 修改一个文章
     * @param article
     */
    @Override
    public void update(Article article) {
        int i = articleDao.updateByPrimaryKeySelective(article);
        if (i==0) throw new RuntimeException("修改文章失败");
    }

    @Override
    public void delete(String id) {
        int i = articleDao.delete(new Article().setId(id));
        if (i==0) throw new RuntimeException("删除文章失败");
    }

}
