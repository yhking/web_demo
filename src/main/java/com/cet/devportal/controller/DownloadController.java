package com.cet.devportal.controller;

import com.cet.devportal.domain.Archive;
import com.cet.devportal.domain.TempArchive;
import com.cet.devportal.service.TempArchiveService;
import com.cet.devportal.utils.HttpContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("resources")
public class DownloadController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadController.class);

    //动态资源标识的下载有效期为5min
    private static final Long PERIOD_OF_DOWNLOAD_VALIDITY = 5*60*1000L;

    @Autowired
    private TempArchiveService tempArchiveService;

    @GetMapping("download")
    public void downloadhandler(@RequestParam("archiveId") String archiveId){

        TempArchive tempArchive = getTempArchiveById(archiveId);

        if (tempArchive == null){
            LOGGER.error("temp_archive表中没有找到记录条目: {}", archiveId);
            return;
        }


        if (!isDownloadRequestLinkValid(tempArchive)){
            LOGGER.info("下载链接已失效，请返回重新下载！");
            return;
        }

        startDownload(tempArchive);

    }


    private void startDownload(TempArchive tempArchive){
        if (tempArchive == null){
            LOGGER.error("入参数为空！");
            return;
        }

        String fileName = tempArchive.getName();
        String fullPath = tempArchive.getPath();

        HttpServletResponse response = HttpContextUtils.getHttpServletResponse();
        response.setHeader("content-type", "application/octet-stream");
        response.setContentType("application/octet-stream");// 设置强制下载不打开
        response.addHeader("Content-Disposition", "attachment;fileName=" + encodeFileName(fileName));// 设置文件名

        try {
            read(fullPath, response.getOutputStream());
        } catch (Exception ex) {
            LOGGER.error("下载文件'{}'失败", fullPath);
        }
    }

    private boolean isDownloadRequestLinkValid(TempArchive tempArchive){
        if (tempArchive == null){
            return false;
        }

        Long createTime = Long.parseLong(tempArchive.getCreateTimestamp());
        Long currTime = new Date().getTime();

        return currTime - createTime <= PERIOD_OF_DOWNLOAD_VALIDITY;
    }

    private TempArchive getTempArchiveById(String archiveId){
        return tempArchiveService.selectByKey(archiveId);
    }

    /**
     * @Description: 文件名称编码。主要处理火狐浏览器编码格式
     * @Param:
     * @return:
     * @Author: liyanchao
     * @Date: 2019/1/17
     */
    private String encodeFileName(String fileName) {
        String filenameEncode = fileName;
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        try {
            String agent = request.getHeader("User-Agent");
            if (agent.contains("MSIE")) {
                filenameEncode = URLEncoder.encode(fileName, "utf-8");
                filenameEncode = filenameEncode.replace("+", "%20");
            } else if (agent.contains("Firefox")) {
                filenameEncode = "=?utf-8?B?" + (new String(Base64.getEncoder().encode(fileName.getBytes("UTF-8")))) + "?=";
            } else {
                filenameEncode = URLEncoder.encode(fileName, "utf-8");
                filenameEncode = filenameEncode.replace("+", "%20");
            }
        } catch (Exception ex) {
            LOGGER.error("文件{}名称编码失败", fileName);
        }
        return filenameEncode;
    }

    public void read(String fileName, OutputStream outputStream) throws Exception {
        LOGGER.info("{}已经准备写入HttpServletResponse。", fileName);
        try {
            InputStream is = new BufferedInputStream(new FileInputStream(fileName));
            byte[] buffer = new byte[50 * 1024];
            int readSize = 0;
            while ((readSize = is.read(buffer)) > 0) {
                outputStream.write(buffer, 0, readSize);
            }
            is.close();
            LOGGER.info("{}结束写入HttpServletResponse。", fileName);
        } catch (IOException ex) {
            LOGGER.error("读取文件{}过程中出错，异常：{}", fileName, ex.getMessage());
            throw new Exception("文件读取失败");
        }
    }
}
