package com.instrument.essearch.model;

/**
 * 产品实体
 * @author zhoudong
 * @version 0.1
 * @date 2018/12/13 15:22
 */
public class ProductDocumentBuilder {
    private static ProductDocument productDocument;

    // create start
    public static ProductDocumentBuilder create(){
        productDocument = new ProductDocument();
        return new ProductDocumentBuilder();
    }
    public ProductDocumentBuilder addSqlid(Integer sqlid) {
        productDocument.setSqlid(sqlid);
        return this;
    }

    public ProductDocument builder() {
        return productDocument;
    }
}
