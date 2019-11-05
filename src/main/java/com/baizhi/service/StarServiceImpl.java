package com.baizhi.service;

import com.baizhi.dao.StarDao;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service("starService")
@Transactional
public class StarServiceImpl implements StarService {
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
        Star star = new Star();
        RowBounds rowBounds = new RowBounds((page-1)*rows,rows);
        List<Star> stars = starDao.selectByRowBounds(star, rowBounds);
        int count = starDao.selectCount(star);
        HashMap<String, Object> map = new HashMap<>();
        map.put("page",page);
        map.put("rows",stars);
        map.put("total",count%rows==0?count/rows:count/rows+1);
        map.put("records",count);
        return map;
    }


    /**
     * 编辑一条数据
     * @param star
     * @param oper
     * @return
     */
    @Override
    public Map<String, Object> edit(Star star, String oper,HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        if("add".equals(oper)){
            String id = UUID.randomUUID().toString();
            star.setId(id);
            int i = starDao.insert(star);
            if(i==0) throw new RuntimeException("添加失败");
            map.put("message",id);
        }
        if("edit".equals(oper)){
            if ("".equals(star.getPhoto())){
                star.setPhoto(null);
                map.put("message",null);
            }else{
                map.put("message",star.getId());
            }
            int i = starDao.updateByPrimaryKeySelective(star);
            if (i==0) throw new RuntimeException("修改失败");
        }
        if("del".equals(oper)){
            //删除服务器中的图片
            Star starDB = starDao.selectOne(star);
            //1.获取服务器中的真实路径
            String realPath = request.getServletContext().getRealPath("/star/image");
            File file = new File(realPath, starDB.getPhoto());
            file.delete();
            int i = starDao.delete(star);
            if (i==0) throw new RuntimeException("删除失败");
        }
        return map;
    }


    /**
     * 明星图片上传
     * @param star
     * @param photo
     * @param request
     * @return
     */
    @Override
    public Map<String, Object> upload(Star star, MultipartFile photo, HttpServletRequest request){
        Map<String, Object> map = new HashMap<>();
        //1.相对路径获取绝对路径
        String realPath = request.getServletContext().getRealPath("/star/image");
        //2.修改图片名称
        String filename = photo.getOriginalFilename();
        String suffix = filename.substring(filename.indexOf("."), filename.length());
        System.out.println(suffix);
        String newName = UUID.randomUUID().toString().replaceAll("-","")+suffix;
        //3.文件上传到服务器中，并且根据id修改数据库中的文件名称
        try {
            photo.transferTo(new File(realPath,newName));
            star.setPhoto(newName);
            int i = starDao.updateByPrimaryKeySelective(star);
            if (i==0) throw new RuntimeException("更改图片路径失败");
            map.put("status",true);
        } catch (IOException e) {
            e.printStackTrace();
            map.put("status",false);
            map.put("message",e.getMessage());
        }
        return map;
    }

    /**
     * 查询所有的歌手或者叫作者
     * @return
     */
    @Override
    public Map<String, Object> findAllOnlyStarName() {
        List<Star> stars = starDao.selectAll();
        Map<String, Object> map = new HashMap<>();
        stars.forEach(star -> {
            map.put(star.getId(),star.getName());
        });
        return map;
    }
}
