package com.cet.devportal.controller;

import com.cet.devportal.domain.Archive;
import com.cet.devportal.domain.Demander;
import com.cet.devportal.service.ArchiveService;
import com.cet.devportal.service.DemanderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller
@RequestMapping(value = "/resources")
public class ResourcesController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResourcesController.class);


    /**
     * 跳转到资源页面
     * @return
     */
    @GetMapping("")
    public String resources(){
        return "resources/index";
    }

    /**
     * 跳转到cet-dev资源页面
     * @param model
     * @return
     */
    @GetMapping(value = "cet-dev")
    public String cetdevResources(Model model){
        return "resources/resources-cet-dev";
    }


}
