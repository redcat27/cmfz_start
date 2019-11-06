package com.baizhi.service;

import com.baizhi.annotation.RedisCache;
import com.baizhi.dao.BannerDao;
import com.baizhi.entity.Banner;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("bannerService")
@Transactional
public class BannerServiceImpl implements BannerService {
    @Autowired
    private BannerDao bannerDao;


    /**
     * 分页查询所有轮播图
     *
     * @param page
     * @param rows
     * @return
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    @RedisCache
    public Map<String, Object> findAllByPage(Integer page, Integer rows) {
        Map<String, Object> map = new HashMap<>();
        RowBounds rowBounds = new RowBounds((page - 1) * rows, rows);
        Banner banner = new Banner();
        List<Banner> banners = bannerDao.selectByRowBounds(banner, rowBounds);
        int count = bannerDao.selectCount(banner);
        map.put("rows", banners);
        map.put("page", page);
        map.put("total", count % rows == 0 ? count / rows : count / rows + 1);   //总页数
        map.put("records", count); //总记录数
        return map;
    }

    /**
     * 添加一个轮播图
     *
     * @param banner
     */
    @Override
    public String edit(Banner banner, String oper, HttpServletRequest request){
        if ("add".equals(oper)) {
            //添加
            String id = UUID.randomUUID().toString();
            banner.setId(id);
            banner.setCreateDate(new Date());
            int i = bannerDao.insert(banner);
            if(i==0){
                throw new RuntimeException("添加失败");
            }
            return id;
        }
        if ("edit".equals(oper)) {
            //修改
            if("".equals(banner.getCover())){
                banner.setCover(null);
            }
            int i = bannerDao.updateByPrimaryKeySelective(banner);
            if(i==0){
                throw new RuntimeException("修改失败");
            }
            return banner.getId();
        }
        if ("del".equals(oper)) {
            Banner bannerDB = bannerDao.selectByPrimaryKey(banner.getId());
            //删除
            int i = bannerDao.deleteByPrimaryKey(banner.getId());
            if(i==0){
                throw new RuntimeException("删除失败");
            }else{
                System.out.println("前台传的id"+banner);

                System.out.println(bannerDB);
                String realPath = request.getServletContext().getRealPath("/banner/"+bannerDB.getFolder());
                //删除服务器中当前id所属的图片
                File file = new File(realPath,bannerDB.getCover());
                file.delete();
            }
            return banner.getId();
        }
        return null;
    }


    @Override
    public Map<String, Object> uploade(Banner banner, MultipartFile cover, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        //获取服务器的真实路径
        String realPath = request.getServletContext().getRealPath("/banner");
        //动态创建日期文件夹
        String format = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        //判断日期文件夹是否存在
        File file = new File(realPath + "\\" + format);
        if(!file.exists()){
            file.mkdirs();
        }
        //获取文件的后缀
        String s = cover.getOriginalFilename();
        String substring = s.substring(s.indexOf("."), s.length());
        //重新生成文件名
        String newFileName = UUID.randomUUID().toString().replace("-", "")+substring;
        try {
            cover.transferTo(new File(file,newFileName));
            banner.setCover(newFileName);
            banner.setFolder(format);
            bannerDao.updateByPrimaryKeySelective(banner);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return map;
    }
}
