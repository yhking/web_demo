package com.cet.devportal.domain;

import javax.persistence.Id;

/**
 * 临时存档文件（动态生成的资源标识码）
 */
public class TempArchive {
    @Id
    private String id;//动态资源标识码
    private String name;//资源的名称
    private String path;//实际物理资源路径
    private String createTimestamp;//动态标识创建的时间戳

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCreateTimestamp() {
        return createTimestamp;
    }

    public void setCreateTimestamp(String createTimestamp) {
        this.createTimestamp = createTimestamp;
    }
}
