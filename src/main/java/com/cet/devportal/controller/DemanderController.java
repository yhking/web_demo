package com.cet.devportal.controller;

import com.cet.devportal.domain.Archive;
import com.cet.devportal.domain.Demander;
import com.cet.devportal.domain.TempArchive;
import com.cet.devportal.service.ArchiveService;
import com.cet.devportal.service.DemanderService;
import com.cet.devportal.service.TempArchiveService;
import javafx.scene.shape.Arc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tk.mybatis.mapper.entity.Example;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Controller
@Validated
@RequestMapping(value = "/resources")
public class DemanderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DemanderController.class);

    @Autowired
    private DemanderService demanderService;

    @Autowired
    private ArchiveService archiveService;

    @Autowired
    private TempArchiveService tempArchiveService;

    /**
     * 跳转到下载请求表单填写页面
     * @return
     */
    @GetMapping("demander")
    public String demander(@RequestParam("archive") String archive, Model model){
        model.addAttribute("archive", archive);
        return "resources/demander";
    }

    /**
     * 提交下载请求表单的处理
     * @param name
     * @param company
     * @param mailbox
     * @param phone
     * @param model
     * @return
     */
    @PostMapping("demander")
    public String demanderHandler (
            @Size(min = 1, max = 20, message = "请输入有效长度的'姓名'(1-20字符)")
            @RequestParam("name") String name,
            @Size(min = 1, max = 50, message = "请输入有效长度的'企业'(1-50字符)")
            @RequestParam("company") String company,
            @Email(message = "请输入有效的'邮箱'")
            @RequestParam("mailbox") String mailbox,
            @Pattern(regexp = "^1[3456789]\\d{9}$", message = "请输入有效的'手机号'")
            @RequestParam("phone") String phone,
            @RequestParam("archive") String archiveName, Model model) {
        Demander demander = new Demander();
        demander.setId(UUID.randomUUID().toString());
        demander.setName(name);
        demander.setMailbox(mailbox);
        demander.setPhone(phone);
        demander.setCompany(company);

        int ret = demanderService.save(demander);
        if (ret <= 0){
            LOGGER.error("{}对象存入数据库失败！", Demander.class);
            return "comm/error-404";
        }

        return createArchiveIdForDownloading(archiveName, model);

    }

    /**
     * 创建动态资源码(archiveId)，作为下载请求启动时的临时资源标识
     * @param downloadArchiveName 要下载的文件名
     * @return
     */
    private String createArchiveIdForDownloading(String downloadArchiveName, Model model){
        Archive archive = archiveService.selectByKey(downloadArchiveName);

        String archiveId = UUID.randomUUID().toString();
        TempArchive tempArchive = new TempArchive();
        tempArchive.setId(archiveId);
        tempArchive.setName(downloadArchiveName);
        tempArchive.setCreateTimestamp(Long.toString(new Date().getTime()));
        tempArchive.setPath(archive.getPath());

        tempArchiveService.save(tempArchive);

        model.addAttribute("archiveId", archiveId);

        return "resources/downloading";
    }
}
