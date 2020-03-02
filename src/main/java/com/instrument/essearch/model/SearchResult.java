package com.instrument.essearch.model;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;

import java.io.Serializable;
import java.util.List;

@Document(indexName = "showb", type = "shwob")
@Mapping(mappingPath = "productIndex.json") // 解决IK分词不能使用问题
public class SearchResult implements Serializable {
    private Integer id;
    private String tableid;
    public  String Total ;
    public  String Words ;
    public  Integer Weight ;
    private String title;
    private String content;
    private String img;
    private Bool isExist;
    /// <summary>版区号 </summary>
    private String classid1;
    /// <summary>版面号</summary>
    private Integer classid2;
    /// <summary>版区名 </summary>
    private String class1;
    /// <summary>版面名 </summary>
    private String class2;
    /// <summary>回复数 </summary>
    private Integer replycount;
    /// <summary>点击数 </summary>
    private Integer clickcount;
    /// <summary>pc链接地址 </summary>
    private String url;
    /// <summary>wap链接地址 </summary>
    private String murl;
    /// <summary>发帖时间 </summary>
    private String date;
    /// <summary>厂商个数 </summary>
    private String IMSHowIDCount;
    /// <summary>仪器个数 </summary>
    private String IMSHowBCount;
    private String nameID;
    private String keywords;
    private String style;
    private String username;
    private List<KeyVlaue> Other;
    private List<AppResult> AppResult;
    private String docid;
    /// <summary>
    /// 是否来自模板数据
    /// </summary>
    private Bool fromtemplate;
    // 摘要:备用字段1
    private String other1;



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTableid() {
        return tableid;
    }

    public void setTableid(String tableid) {
        this.tableid = tableid;
    }

    public String getTotal() {
        return Total;
    }

    public void setTotal(String total) {
        Total = total;
    }

    public String getWords() {
        return Words;
    }

    public void setWords(String words) {
        Words = words;
    }

    public Integer getWeight() {
        return Weight;
    }

    public void setWeight(Integer weight) {
        Weight = weight;
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

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Bool getIsExist() {
        return isExist;
    }

    public void setIsExist(Bool isExist) {
        this.isExist = isExist;
    }

    public String getClassid1() {
        return classid1;
    }

    public void setClassid1(String classid1) {
        this.classid1 = classid1;
    }

    public Integer getClassid2() {
        return classid2;
    }

    public void setClassid2(Integer classid2) {
        this.classid2 = classid2;
    }

    public String getClass1() {
        return class1;
    }

    public void setClass1(String class1) {
        this.class1 = class1;
    }

    public String getClass2() {
        return class2;
    }

    public void setClass2(String class2) {
        this.class2 = class2;
    }

    public Integer getReplycount() {
        return replycount;
    }

    public void setReplycount(Integer replycount) {
        this.replycount = replycount;
    }

    public Integer getClickcount() {
        return clickcount;
    }

    public void setClickcount(Integer clickcount) {
        this.clickcount = clickcount;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMurl() {
        return murl;
    }

    public void setMurl(String murl) {
        this.murl = murl;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIMSHowIDCount() {
        return IMSHowIDCount;
    }

    public void setIMSHowIDCount(String IMSHowIDCount) {
        this.IMSHowIDCount = IMSHowIDCount;
    }

    public String getIMSHowBCount() {
        return IMSHowBCount;
    }

    public void setIMSHowBCount(String IMSHowBCount) {
        this.IMSHowBCount = IMSHowBCount;
    }

    public String getNameID() {
        return nameID;
    }

    public void setNameID(String nameID) {
        this.nameID = nameID;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<KeyVlaue> getOther() {
        return Other;
    }

    public void setOther(List<KeyVlaue> other) {
        Other = other;
    }

    public List<com.instrument.essearch.model.AppResult> getAppResult() {
        return AppResult;
    }

    public void setAppResult(List<com.instrument.essearch.model.AppResult> appResult) {
        AppResult = appResult;
    }

    public String getDocid() {
        return docid;
    }

    public void setDocid(String docid) {
        this.docid = docid;
    }

    public Bool getFromtemplate() {
        return fromtemplate;
    }

    public void setFromtemplate(Bool fromtemplate) {
        this.fromtemplate = fromtemplate;
    }

    public String getOther1() {
        return other1;
    }

    public void setOther1(String other1) {
        this.other1 = other1;
    }
}



class AppResult implements  Serializable
{
    public String index ;
    public String tableid ;
    public String id ;
    public String title ;
    public String pid ;
    /*******1 方案*********/
    public String url ;
    public String imshownameid ;
    public String imshowid ;
    /*******2仪器***************/
    /*******3厂商*********************/
    public String count ;
    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getTableid() {
        return tableid;
    }

    public void setTableid(String tableid) {
        this.tableid = tableid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImshownameid() {
        return imshownameid;
    }

    public void setImshownameid(String imshownameid) {
        this.imshownameid = imshownameid;
    }

    public String getImshowid() {
        return imshowid;
    }

    public void setImshowid(String imshowid) {
        this.imshowid = imshowid;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}

