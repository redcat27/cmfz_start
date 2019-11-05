package com.baizhi.service;

import com.baizhi.dao.AlbumDao;
import com.baizhi.dao.ChapterDao;
import com.baizhi.entity.Album;
import com.baizhi.entity.Chapter;
import it.sauronsoftware.jave.Encoder;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@Service("chapterService")
@Transactional
public class ChapterServiceImpl implements ChapterService {
    @Autowired
    private ChapterDao chapterDao;
    @Autowired
    private AlbumDao albumDao;


    /**
     * 分页查所有音频，根据专辑id查询
     * @param chapter
     * @param page
     * @param rows
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Map<String, Object> findAllByPageAndAlbumId(Chapter chapter,Integer page, Integer rows) {
        RowBounds rowBounds = new RowBounds((page - 1) * rows, rows);
        List<Chapter> chapters = chapterDao.selectByRowBounds(chapter, rowBounds);
        int count = chapterDao.selectCount(chapter);
        Map<String, Object> map = new HashMap<>();
        map.put("page",page);
        map.put("rows",chapters);
        map.put("total",count/rows==0?count/rows:count/rows+1);
        map.put("records",count);
        return map;
    }


    /**
     * 编辑一个章节
     * @param chapter
     * @param oper
     * @param request
     * @return
     */
    @Override
    public Map<String, Object> edit(Chapter chapter, String oper, String albumId, HttpServletRequest request) {
        HashMap<String, Object> map = new HashMap<>();
        if ("add".equals(oper)){
            //1.根据所属专辑id查询一个专辑对象
            Album albumDb = albumDao.selectOne(new Album().setId(albumId));
            String author = albumDb.getAuthor();
            //添加一个章节（歌曲）
            chapter.setId(UUID.randomUUID().toString())
                   .setCreateDate(new Date())
                   .setAlbumId(albumId)
                   .setFolder(albumDb.getTitle())
                   .setSinger(author);
            int i = chapterDao.insert(chapter);
            if(i==0) throw new RuntimeException("添加章节失败");
            map.put("message",chapter.getId());
        }
        if ("edit".equals(oper)){
            //判断是否修改音频（章节）
            if("".equals(chapter.getName())){
                chapter.setName(null);
                map.put("change",false);
            }else{
                //删除服务器之前的音频文件
                Chapter one = chapterDao.selectOne(new Chapter().setId(chapter.getId()));
                File file = new File(request.getServletContext().getRealPath("/chapter/audio/" + one.getFolder()), one.getName());
                file.delete();
                //2.根据所属专辑id查询一个专辑对象
                Album albumDb = albumDao.selectOne(new Album().setId(albumId));
                chapter.setFolder(albumDb.getTitle());
                map.put("change",true);
            }
            //修改一个章节
            int i = chapterDao.updateByPrimaryKeySelective(chapter);
            if (i==0) throw new RuntimeException("修改章节事变");
            map.put("message",chapter.getId());
        }
        if ("del".equals(oper)){
            //删除服务器中的数据
            Chapter one = chapterDao.selectOne(chapter);
            File file = new File(request.getServletContext().getRealPath("/chapter/audio/" + one.getFolder()), one.getName());
            file.delete();
            //删除数据库中的数据
            int i = chapterDao.delete(chapter);
            if (i==0) throw new RuntimeException("删除失败");
        }
        return map;
    }


    /**
     * 章节（歌曲）音频上传
     * @param id
     * @param name
     * @param request
     * @return
     */
    @Override
    public Map<String, Object> upload(String id, MultipartFile name, String albumId, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        //1.相对路径获取绝对路径
        String realPath = request.getServletContext().getRealPath("/chapter/audio");
        //1.5获取chapter对象
        Chapter chapterDB = chapterDao.selectOne(new Chapter().setId(id));
        //2.生成专辑的文件夹
        File file = new File(realPath, chapterDB.getFolder());
        //判断专辑文件夹是否存在
        if(!file.exists()){
            file.mkdirs();  //不存在创建专辑文件夹
        }
        //3.文件上传到服务器中，并且根据id修改数据库中的文件名称
        try {
            File realFile = new File(file, name.getOriginalFilename());
            name.transferTo(realFile);
            //获取文件名不带后缀，用于章节标题赋值
            String s = name.getOriginalFilename();
            String title = s.substring(0, s.indexOf("."));
            //获取文件大小
            BigDecimal size = new BigDecimal(name.getSize());
            BigDecimal mod = new BigDecimal(1024);
            BigDecimal realSize = size.divide(mod)
                    .divide(mod)
                    .setScale(2, BigDecimal.ROUND_HALF_UP);
            //获取文件时长
            Encoder encoder = new Encoder();
            long duration = encoder.getInfo(realFile).getDuration();
            //此时duration获取到了文件的时长，但是单位是毫秒，除1000变成秒
            Chapter chapter = new Chapter().setId(id)
                    .setName(name.getOriginalFilename())
                    .setSize(realSize+"MB")
                    .setDuration(duration/1000/60+":"+duration/1000%60)
                    .setTitle(title);
            int i = chapterDao.updateByPrimaryKeySelective(chapter);
            if (i==0) throw new RuntimeException("更改图片路径失败");
            //更新专辑下的章节数量1.获取专辑对象根据id
            Album albumDB = albumDao.selectOne(new Album().setId(albumId));
            albumDao.updateByPrimaryKeySelective(albumDB.setCount(albumDB.getCount()+1));
            map.put("status",true);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("status",false);
            map.put("message",e.getMessage());
        }
        return map;
    }


}
