package com.instrument.essearch.model;

import java.io.Serializable;

public class ResultJsonContent<T>  implements Serializable {
    //public ResultJsonContent(){};

    // 摘要:
    //     头部，存储是否成功，错误信息等
    private JsonHeader Header ;
    //
    // 摘要:
    //     数据主题，根据不同参数返回类型
    private T Tbody ;


    public JsonHeader getHeader() {
        return Header;
    }

    public void setHeader(JsonHeader header) {
        Header = header;
    }

    public T getTbody() {
        return Tbody;
    }

    public void setTbody(T tbody) {
        Tbody = tbody;
    }
}


