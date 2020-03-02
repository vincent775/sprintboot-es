package com.instrument.essearch.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;

import java.io.Serializable;

/**
 * 产品实体
 * @author zhoudong
 * @version 0.1
 * @date 2018/12/13 15:22
 */
@Document(indexName = "news", type = "news")
@Mapping(mappingPath = "productIndex.json") // 解决IK分词不能使用问题
public class ProductDocument implements Serializable {

    @Id
    private Integer sqlid;
    //@Field(analyzer = "ik_max_word",searchAnalyzer = "ik_max_word")
    private String title;
    //@Field(analyzer = "ik_max_word",searchAnalyzer = "ik_max_word")
    private String content;


    public Integer getSqlid() {
        return sqlid;
    }

    public void setSqlid(Integer sqlid) {
        this.sqlid = sqlid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


}
