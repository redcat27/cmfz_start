package com.baizhi.service;

import com.baizhi.dao.AlbumDao;
import com.baizhi.dao.StarDao;
import com.baizhi.entity.Album;
import com.baizhi.entity.Star;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.*;


@Service("albumService")
@Transactional
public class AlbumServiceImpl implements AlbumService {
    @Autowired
    private AlbumDao albumDao;
    @Autowired
    private StarDao starDao;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Map<String, Object> findAllByPage(Integer page, Integer rows) {
        Album album = new Album();
        RowBounds rowBounds = new RowBounds((page - 1) * rows, rows);
        List<Album> albums = albumDao.selectByRowBounds(album, rowBounds);
        int count = albumDao.selectCount(album);
        HashMap<String, Object> map = new HashMap<>();
        map.put("page",page);
        map.put("rows",albums);
        map.put("total",count/rows==0?count/rows:count/rows+1);
        map.put("records",count);
        return map;
    }


    /**
     * 编辑一个专辑
     * @param album
     * @param oper
     * @param request
     * @return
     */
    @Override
    public Map<String, Object> edit(Album album, String oper, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        if("edit".equals(oper)){
            //修改:判断是否上传了专辑新的封面
            if("".equals(album.getCover())){
                //说明没有上传新的文件，设置该属性为空，则不进行修改，
                // 并且给前台标记，map中put一个change属性，布尔类型，判断是否进行文件上传
                album.setCover(null);
                map.put("change",false);
            }else{
                //修改了图片，则删除原有的图片
                Album album1 = new Album().setId(album.getId());
                Album one = albumDao.selectOne(album1);
                //获取服务器真实路径
                String realPath = request.getServletContext().getRealPath("/album/image");
                File file = new File(realPath, one.getCover());
                //删除服务器中的图片数据
                file.delete();
                map.put("change",true);
            }
            int i = albumDao.updateByPrimaryKeySelective(album);
            if(i==0) throw new RuntimeException("修改失败");
            map.put("message",album.getId());
        }
        if("add".equals(oper)){
            //添加
            //1.根据starId查询一个明星对象,完善专辑的所属作者外键和作者名
            Star star = starDao.selectOne(new Star().setId(album.getAuthor()));
            String id = UUID.randomUUID().toString();
            album.setId(id)
                 .setCreateDate(new Date())
                 .setStarId(star.getId())
                 .setAuthor(star.getName())
                 .setCount(0);
            //将完整的数据添加到数据库中,图片稍后上传
            albumDao.insert(album);
            map.put("message",id);
        }
        if("del".equals(oper)){
            //删除-注意删除专辑要删除图片和专辑下所有章节（音乐）
            Album albumDB = albumDao.selectOne(album);
            //获取服务器真实路径
            String realPath = request.getServletContext().getRealPath("/album/image");
            File file = new File(realPath, albumDB.getCover());
            //删除服务器中的图片数据
            file.delete();
            //删除数据库中的数据
            int i = albumDao.delete(album);
            if (i==0) throw new RuntimeException("删除失败");
        }
        return map;
    }


    /**
     * 专辑封面的文件上传
     * @param album
     * @param cover
     * @param request
     * @return
     */
    @Override
    public Map<String, Object> upload(Album album, MultipartFile cover, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        //1.相对路径获取绝对路径
        String realPath = request.getServletContext().getRealPath("/album/image");
        //2.修改图片名称
        String filename = cover.getOriginalFilename();
        String suffix = filename.substring(filename.indexOf("."), filename.length());
        System.out.println(suffix);
        String newName = UUID.randomUUID().toString().replaceAll("-","")+suffix;
        //3.文件上传到服务器中，并且根据id修改数据库中的文件名称
        try {
            cover.transferTo(new File(realPath,newName));
            album.setCover(newName);
            int i = albumDao.updateByPrimaryKeySelective(album);
            if (i==0) throw new RuntimeException("更改图片路径失败");
            map.put("status",true);
        } catch (IOException e) {
            e.printStackTrace();
            map.put("status",false);
            map.put("message",e.getMessage());
        }
        return map;
    }
}
