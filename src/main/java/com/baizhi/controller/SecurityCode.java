package com.baizhi.controller;

import com.baizhi.utils.SecurityCodeUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Controller
@RequestMapping("security")
public class SecurityCode {

    /**
     * 生成验证码
     * @param request
     * @param response
     * @throws IOException
     */
    @RequestMapping("code")
    public void createSecurityCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String code = SecurityCodeUtil.generateVerifyCode(4);
        System.out.println(code);
        request.getSession().setAttribute("securityCode",code);
        BufferedImage image = SecurityCodeUtil.getImage(100, 40, code);
        ServletOutputStream outputStream = response.getOutputStream();
        ImageIO.write(image,"png",outputStream);
        outputStream.close();
    }

}
