package com.instrument.essearch.model;

import java.io.Serializable;

public class JsonHeader {
    //public JsonHeader(){};

    // 摘要:
    //     是否成功
    private Boolean Flag ;
    // 摘要:
    //  信息
    private String Messages ;
    private int PageCount ;
    private int RowCount ;

    public Boolean getFlag() {
        return Flag;
    }

    public void setFlag(Boolean flag) {
        Flag = flag;
    }

    public String getMessages() {
        return Messages;
    }

    public void setMessages(String messages) {
        Messages = messages;
    }

    public int getPageCount() {
        return PageCount;
    }

    public void setPageCount(int pageCount) {
        PageCount = pageCount;
    }

    public int getRowCount() {
        return RowCount;
    }

    public void setRowCount(int rowCount) {
        RowCount = rowCount;
    }
}
