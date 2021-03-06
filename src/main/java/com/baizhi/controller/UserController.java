package com.baizhi.controller;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.baizhi.entity.User;
import com.baizhi.service.UserService;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 根据starId查询一个明星下有哪些用户
     * @param page
     * @param rows
     * @param starId
     * @return
     */
    @RequestMapping("findAllByStarIDAndPage")
    @ResponseBody
    public Map<String, Object> findAllByStarIDAndPage(Integer page, Integer rows, String starId){
        Map<String, Object> map = userService.findAllByStarIDAndPage(page, rows, starId);
        return map;
    }


    /**
     * 分页查询所有用户信息
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("findAllByPage")
    @ResponseBody
    public Map<String ,Object> findAllByPage(Integer page, Integer rows){
        return userService.findAllByPage(page,rows);
    }


    /**
     * 导出所有的用户信息表
     * @param response
     */
    @RequestMapping("exportUsers")
    public void exportUsers(HttpServletResponse response,String oper){
        //1.获取所有的用户
        List<User> users = userService.findAll();
        //生成Excel文件
        Workbook workbook = ExcelExportUtil.exportExcel(new ExportParams("用户信息表","用户信息表"), com.baizhi.entity.User.class, users);
        //生成Excel文件名字
        String fileName = "用户表("+ new SimpleDateFormat("yyyy-MM-dd").format(new Date())+").xls";
        //处理文件名中文乱码
        try {
            fileName = new String(fileName.getBytes("gbk"),"iso-8859-1");
            //设置response
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("content-disposition",""+oper+";filename="+fileName);
            workbook.write(response.getOutputStream());
            workbook.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 查询某年各个月份注册男女注册量
     * @return
     */
    @RequestMapping("userRegistData")
    @ResponseBody
    public Map<String ,Object> userRegistData(){
        Map<String, Object> map = new HashMap<>();
        Long[] nan = userService.findRegistCountBySexAndYear("男", "2019");
        System.out.println(nan);
       Long[] nv = userService.findRegistCountBySexAndYear("女", "2019");
        System.out.println(nv);
        map.put("nan",nan);
        map.put("nv",nv);
        return map;
    }


    /**
     * 给用户发送短信验证码
     * @param phone
     * @throws Exception
     */
    @RequestMapping("sendPhoneMessageCode")
    public void sendPhoneMessageCode(String phone) throws Exception {
        //生成6位随机验证码
        Integer iCode = (int) (((Math.random() * 9) + 1) * 100000);
        String code = iCode.toString();
        //设置超时时间-可自行调整
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
//初始化ascClient需要的几个参数
        final String product = "Dysmsapi";//短信API产品名称（短信产品名固定，无需修改）
        final String domain = "dysmsapi.aliyuncs.com";//短信API产品域名（接口地址固定，无需修改）
//替换成你的AK                       LTAI4FopMthj3Vpu71rJg1ic
        final String accessKeyId = "LTAI4FopMthj3Vpu71rJg1ic";//你的accessKeyId,参考本文档步骤2
        final String accessKeySecret = "nrPlKcG9pNVhi0Yp8DbSALhtHoqa71";//你的accessKeySecret，参考本文档步骤2
//初始化ascClient,暂时不支持多region（请勿修改）
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId,
                accessKeySecret);
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        IAcsClient acsClient = new DefaultAcsClient(profile);
        //组装请求对象
        SendSmsRequest request = new SendSmsRequest();
        //使用post提交
        request.setMethod(MethodType.POST);
        //必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式；发送国际/港澳台消息时，接收号码格式为国际区号+号码，如“85200000000”
        /*String str = "";
        StringBuilder sb = new StringBuilder(str);
        for (int i=0;i<phones.length;i++){
            sb.append(phones[i]);
            sb.append(",");
        }
        String s = sb.toString();
        String substring = s.substring(0, s.length() - 1);
        System.out.println("手机号码集合："+substring);*/
        request.setPhoneNumbers(phone);
        //必填:短信签名-可在短信控制台中找到
        request.setSignName("天缘迪方");
        //必填:短信模板-可在短信控制台中找到，发送国际/港澳台消息时，请使用国际/港澳台短信模版
        request.setTemplateCode("SMS_176941094");
        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        //友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
        request.setTemplateParam("{\"code\":"+code+"}");
        //可选-上行短信扩展码(扩展码字段控制在7位或以下，无特殊需求用户请忽略此字段)
        //request.setSmsUpExtendCode("90997");
        //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
        request.setOutId("yourOutId");
//请求失败这里会抛ClientException异常
        SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);
        System.out.println(sendSmsResponse.getCode());
        if(sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
//请求成功
            System.out.println("短信发送成功");
            stringRedisTemplate.opsForValue().set("zzzzz", "xxxx");


        }
   }


}
