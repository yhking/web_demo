package com.cet.devportal.domain;


import javax.persistence.Id;

/**
 * 资源文件
 */
public class Archive {
    @Id
    private String name;//存档文件名称（下载资源的链接中会使用该名称）
    private String path;//存档文件的实际存储路径（包含其实际的文件名称）

    //扩展字段，用于存档文件的分类管理
    private String version;//存档文件版本号
    private String type;//存档文件类型：exe, app, doc
    private String project;//存档文件的所属项目: 例如cet-dev, pmc-easycom


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
