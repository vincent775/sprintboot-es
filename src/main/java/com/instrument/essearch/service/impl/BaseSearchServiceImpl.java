package com.instrument.essearch.service.impl;

import com.instrument.essearch.model.JsonHeader;
import com.instrument.essearch.model.KeyVlaue;
import com.instrument.essearch.model.ResultJsonContent;
import com.instrument.essearch.model.SearchResult;
import com.instrument.essearch.service.BaseSearchService;
import com.instrument.essearch.page.Page;
import com.sun.javafx.collections.MappingChange;
import org.apache.lucene.search.Query;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.lucene.search.function.CombineFunction;
import org.elasticsearch.common.lucene.search.function.FunctionScoreQuery;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.*;
import org.elasticsearch.index.query.functionscore.*;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.ScriptSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.Field;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


import javax.annotation.Resource;
import javax.swing.plaf.synth.Region;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import java.io.IOException;
import java.security.KeyException;
import java.security.PublicKey;
import java.util.*;

/**
 * elasticsearch 搜索引擎
 * @author zhoudong
 * @version 0.1
 * @date 2018/12/13 15:33
 */
@Service
public class BaseSearchServiceImpl<T> implements BaseSearchService<T> {
    private Logger log = LoggerFactory.getLogger(getClass());
    @Resource
    private ElasticsearchTemplate elasticsearchTemplate;


    @Override
    public List<T> query(String keyword, Class<T> clazz) {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(new QueryStringQueryBuilder(keyword))
                .withSort(SortBuilders.scoreSort().order(SortOrder.DESC))
                // .withSort(new FieldSortBuilder("createTime").order(SortOrder.DESC))
                .build();

        return elasticsearchTemplate.queryForList(searchQuery,clazz);
    }

    /**
     * 高亮显示
     * @auther: zhoudong
     * @date: 2018/12/13 21:22
     */
    @Override
    public  List<Map<String,Object>> queryHit(String keyword,String indexName,String ... fieldNames) {
        // 构造查询条件,使用标准分词器.
        QueryBuilder matchQuery = createQueryBuilder(indexName,keyword,fieldNames);

        // 设置高亮,使用默认的highlighter高亮器
        HighlightBuilder highlightBuilder = createHighlightBuilder(fieldNames);

        // 设置查询字段
        SearchResponse response = elasticsearchTemplate.getClient().prepareSearch(indexName)
                .setQuery(matchQuery)
                .highlighter(highlightBuilder)
                .setSize(10000) // 设置一次返回的文档数量，最大值：10000
                .get();

        // 返回搜索结果
        SearchHits hits = response.getHits();

        return getHitList(hits);
    }
    /**
     * 高亮显示，返回分页
     * @auther: zhoudong
     * @date: 2018/12/18 10:29
     */
    @Override
    public Page<Map<String, Object>> queryHitByPage(int pageNo,int pageSize, String keyword, String indexName, String... fieldNames) {
        // 构造查询条件,使用标准分词器.
        QueryBuilder matchQuery = createQueryBuilder(indexName,keyword,fieldNames);




        // 设置高亮,使用默认的highlighter高亮器
        HighlightBuilder highlightBuilder = createHighlightBuilder(fieldNames);

        // 设置查询字段
        SearchResponse response = elasticsearchTemplate.getClient().prepareSearch(indexName)
                .setQuery(matchQuery)
                .highlighter(highlightBuilder)
                .setFrom((pageNo-1) * pageSize)
                .setSize(pageNo * pageSize) // 设置一次返回的文档数量，最大值：10000
                .get();


        // 返回搜索结果
        SearchHits hits = response.getHits();

        Long totalCount = hits.getTotalHits();
        Page<Map<String, Object>> page = new Page<>(pageNo,pageSize,totalCount.intValue());
        page.setList(getHitList(hits,fieldNames));
        return page;
    }

    /***
     *
     * @param pageNo        当前页
     * @param pageSize      每页显示的总条数
     * @param keyword       关键字
     * @param indexName     索引库
     * @param fieldNames    搜索的字段
     * @return    返回和现有的接口框架一样的结果
     */
    @Override
    public ResultJsonContent<List<SearchResult>> queryHitByPage1(int pageNo, int pageSize, String keyword, String indexName, String... fieldNames) {
        // 构造查询条件,使用标准分词器.
        QueryBuilder matchQuery = createQueryBuilder(indexName,keyword, fieldNames);

        //自定义排序规则算法
        ScriptSortBuilder scriptSort = createQueryScript(indexName);

        // 设置高亮,使用默认的highlighter高亮器
        HighlightBuilder highlightBuilder = createHighlightBuilder(fieldNames);


        // 设置查询字段
        String[] indexName_Str = GetActIndex(indexName);  //获取各个栏目对应的要查询的栏目信息
        SearchRequestBuilder searchgetlistbuild = elasticsearchTemplate.getClient().prepareSearch(indexName_Str)
                .setQuery(matchQuery)
                //.addSort(scriptSort)
                .addSort(new ScoreSortBuilder())
                .addSort("intime",SortOrder.DESC)
                .highlighter(highlightBuilder)
                .setFrom((pageNo-1) * pageSize)
                .setSize(pageSize); // 设置一次返回的文档数量，最大值：10000
        log.info(searchgetlistbuild.toString());
        SearchResponse response = searchgetlistbuild.get();

       // response.

        // 返回搜索结果
        SearchHits hits = response.getHits();

        Long totalCount = hits.getTotalHits();
//        Page<Map<String, Object>> page = new Page<>(pageNo,pageSize,totalCount.intValue());
//        page.setList(getHitList(hits,fieldNames));
        ResultJsonContent<List<SearchResult>> result = new ResultJsonContent<>();
        JsonHeader header = new JsonHeader();
        header.setFlag(true);
        header.setRowCount(totalCount.intValue());
        header.setPageCount(0);
        result.setHeader(header);
        result.setTbody(getBuildList(hits,fieldNames));
        return result;
    }

    /**
     * 构造查询条件
     * @auther: zhoudong
     * @date: 2018/12/18 10:42
     */
    private QueryBuilder createQueryBuilder(String indexName,String keyword, String... fieldNames){
        // 构造查询条件,使用标准分词器.
        /**
         * 使用QueryBuilder
         * termQuery("key", obj) 完全匹配
         * termsQuery("key", obj1, obj2..)   一次匹配多个值
         * matchQuery("key", Obj) 单个匹配, field不支持通配符, 前缀具高级特性
         * multiMatchQuery("text", "field1", "field2"..);  匹配多个字段, field有通配符忒行
         * matchAllQuery();         匹配所有文件
         */
        QueryBuilder  queryBuilder=QueryBuilders
                .multiMatchQuery(keyword,fieldNames)   // matchQuery(),单字段搜索
                .analyzer("my_hanlp")
                .tieBreaker(0.3f)
                .minimumShouldMatch("30%")
               // .field("title",10)
               // .field("content",1)
                .operator(Operator.OR);
        for (Map.Entry<String,Integer> item: GetScriptBoot(indexName).entrySet())
        {
            ((MultiMatchQueryBuilder) queryBuilder).field(item.getKey(),item.getValue());
        }


        ScriptScoreFunctionBuilder scoreFunctionBuilder =  createfunctionScoreScipt(indexName);


                //构建function_score
        QueryBuilder  queryBuilder1=QueryBuilders
              .functionScoreQuery(queryBuilder,scoreFunctionBuilder)
                .scoreMode(FunctionScoreQuery.ScoreMode.SUM)
                .boostMode(CombineFunction.SUM);



        return queryBuilder1;
    }


    /**
     * 构建function_score中的自定义排序算法定义 function中的内容
     * @param indexName
     * @return
     */
    private ScriptScoreFunctionBuilder createfunctionScoreScipt(String indexName)
    {
        //测试script 脚本自定义排序规则。
        Map<String, Object> params = new HashMap<>();//存放参数的map
        //  params.put("resourceMapList", resourceMapList);//其他一些匹配信息List<Map<isnot；ip>>,isnot及ip的含义见下面脚本代码
        //params.put("now", "1567393142550");//ip对应的字段名称
        String painlessCode =GetScriptPainless(indexName);
        params = GetScriptParams(indexName);

        if(StringUtils.isEmpty(painlessCode))
        {
            painlessCode = "return 0;";

        }
        Script script = new Script(ScriptType.INLINE, "painless", painlessCode, params);//脚本文件名称，脚本类型
        ScriptScoreFunctionBuilder scoreFunctionBuilder = ScoreFunctionBuilders.scriptFunction(script);
        return scoreFunctionBuilder;

    }

    /**
     * 构造script  自定义分析器
     * @param indexName     *
     * @return
     */
    private ScriptSortBuilder createQueryScript(String indexName){

        //测试script 脚本自定义排序规则。
        Map<String, Object> params = new HashMap<>();//存放参数的map
        //  params.put("resourceMapList", resourceMapList);//其他一些匹配信息List<Map<isnot；ip>>,isnot及ip的含义见下面脚本代码
        //params.put("now", "1567393142550");//ip对应的字段名称
        String painlessCode =GetScriptPainless(indexName);
        params = GetScriptParams(indexName);

        if(StringUtils.isEmpty(painlessCode))
        {
            painlessCode = "return 0;";

        }
        Script script = new Script(ScriptType.INLINE, "painless", painlessCode, params);//脚本文件名称，脚本类型
        //ScriptQueryBuilder filterBuilder = QueryBuilders.scriptQuery(script);//创建scriptquery
        //QueryBuilder query=QueryBuilders.boolQuery().filter(filterBuilder); //将scriptQueryc存入过滤条件
        ScriptSortBuilder sortBuilder = SortBuilders.scriptSort(script, ScriptSortBuilder.ScriptSortType.NUMBER).order(SortOrder.DESC);
        return sortBuilder;

    }
    /**
     * 构造高亮器
     * @auther: zhoudong
     * @date: 2018/12/18 10:44
     */
    private HighlightBuilder createHighlightBuilder(String... fieldNames){
        // 设置高亮,使用默认的highlighter高亮器
        HighlightBuilder highlightBuilder = new HighlightBuilder()
                // .field("productName")
                .preTags("<span style='color:red'>")
                .postTags("</span>");

        // 设置高亮字段
        //fragment_size: 你一个Field的值，比如有长度是1万，但是你不可能在页面上显示这么长。设置要显示出来的fragment文本判断的长度，默认是100
        //number_of_fragments：你可能你的高亮的fragment文本片段有多个片段，你可以指定就显示几个片段
        for (String fieldName: fieldNames) highlightBuilder.field(fieldName,80,2);

        return highlightBuilder;
    }

    /**
     * 处理高亮结果
     * @auther: weiyt
     * @date: 2019年8月15日13:57:10
     */
    private List<Map<String,Object>> getHitList(SearchHits hits,String... fieldNames){
        List<Map<String,Object>> list = new ArrayList<>();
        Map<String,Object> map;
        for(SearchHit searchHit : hits){
            map = new HashMap<>();
            // 处理源数据
            map.put("source",searchHit.getSourceAsMap());
            // 处理高亮数据
            Map<String,Object> hitMap = new HashMap<>();
            searchHit.getHighlightFields().forEach((k,v) -> {
                //log.info(k);
                //log.info(v.getFragments()[0].toString());
                //log.info(v.getFragments());
                String hight = "";
                for(Text text : v.getFragments()) hight += text.string();
                hitMap.put(v.getName(), hight);
            });
            //es自带的高亮没有匹配的字段是不会显示的，所以在此定义没有匹配的字段也需要在 highlight中显示
            for(String item : fieldNames)
            {
                boolean flag=hitMap.containsKey(item);
                if(!flag)
                {
                    hitMap.put(item,searchHit.getSourceAsMap().get(item).toString());
                }
            }
            map.put("highlight",hitMap);
            list.add(map);
        }
        return list;
    }
    /**
     * 结果进行格式化
     * @auther: weiyt
     * @date: 2019年8月15日13:57:10
     */
    private List<SearchResult> getBuildList(SearchHits hits,String... fieldNames){

        List<SearchResult> result = new ArrayList<>();
        result = AnalyzeData(hits.getHits());



//        List<Map<String,Object>> list = new ArrayList<>();
//        Map<String,Object> map;
//        for(SearchHit searchHit : hits){
//            map = new HashMap<>();
//            // 处理源数据
//            map.put("source",searchHit.getSourceAsMap());
//            // 处理高亮数据
//            Map<String,Object> hitMap = new HashMap<>();
//            searchHit.getHighlightFields().forEach((k,v) -> {
//                log.info(k);
//                log.info(v.getFragments()[0].toString());
//                //log.info(v.getFragments());
//                String hight = "";
//                for(Text text : v.getFragments()) hight += text.string();
//                hitMap.put(v.getName(), hight);
//            });
//            //es自带的高亮没有匹配的字段是不会显示的，所以在此定义没有匹配的字段也需要在 highlight中显示
//            for(String item : fieldNames)
//            {
//                boolean flag=hitMap.containsKey(item);
//                if(!flag)
//                {
//                    hitMap.put(item,searchHit.getSourceAsMap().get(item).toString());
//                }
//            }
//            map.put("highlight",hitMap);
//            list.add(map);
//        }
        return result;
    }
    //解析数据
    public List<SearchResult> AnalyzeData(SearchHit[] Hit){
        List<SearchResult> list = new ArrayList<>();
        List<String> commField = Arrays.asList( "id",  "sqlid", "title", "content", "img", "intime", "url", "murl", "indexname" );

        for(SearchHit item : Hit){
            SearchResult model =  new SearchResult();
            List<KeyVlaue> keyVlaueList =  new ArrayList<>();
           //log.info(item.getIndex());
           //log.info(item.getSourceAsString());
           for(String key : item.getSourceAsMap().keySet()) {
               //log.info(key);
               boolean isBool = commField.contains(key);
               KeyVlaue keyVlaueModel = new KeyVlaue();
               if (!isBool) {
                   log.info(key);
                   log.info(GetStr(item.getSourceAsMap().get(key)));
                   keyVlaueModel.setKey(key);
                   keyVlaueModel.setValue(GetStr(item.getSourceAsMap().get(key)));
               }
               keyVlaueList.add(keyVlaueModel);
           }
               model.setId(Integer.parseInt(GetStr(item.getSourceAsMap().get("sqlid"))));
               model.setWeight((int)item.getScore());
               model.setTitle(GetStr(item.getSourceAsMap().get("title")));
               model.setContent(GetStr(item.getSourceAsMap().get("content")));
               model.setTableid(GetStr(item.getSourceAsMap().get("indexname")));
               model.setImg(GetImgUrl(GetStr(item.getSourceAsMap().get("indexname")),GetStr(item.getSourceAsMap().get("img"))));
               model.setDate(GetStr(item.getSourceAsMap().get("intime")));
               model.setUrl(GetStr(item.getSourceAsMap().get("url")));
               model.setUsername(item.getSourceAsMap().containsKey("admin_name")?GetStr(item.getSourceAsMap().get("admin_name")):"");
               model.setDocid(GetStr(item.getId()));
               model.setOther(keyVlaueList);

            item.getHighlightFields().forEach((k,v) -> {
                //log.info(k);
                //log.info(v.getFragments()[0].toString());
                String hight = "";
                for(Text text : v.getFragments()) hight += text.string();
                if(v.getName().equals("title")){  model.setTitle(GetStr(hight)); }
                if(v.getName().equals("content")){  model.setContent(GetStr(hight)); }
            });
            list.add(model);
        }
        return list;
    }

    /***
     * 拼接图片地址
     * @param indexname 栏目
     * @param img  原地址
     * @return
     */
    public String GetImgUrl(String indexname, String img)
    {
        if (!StringUtils.isEmpty(img))
        {
            switch (indexname)
            {
                case "company":
                    img = "https://img1.17img.cn/17img" + img;
                    break;
                case "news":
                    if (!img.contains("https://") && !img.contains("http://"))
                    img = "https://img1.17img.cn/17img" + img;
                    break;
                case "showb":
                    img = "https://img1.17img.cn/17img" + img;
                    break;
                case "subjectnews":
                    img = "https://img1.17img.cn/17img" + img;
                    break;
            }
        }
        return img;
    }

    /**
     * 判断字符串是否为空
     * @param obj
     * @return
     */
    public String GetStr(Object obj)
    {
        String result="";
        if(obj==""||obj==null){
            result="";
        }
        else {
            result = obj.toString();
        }
        //result=result.length()>200?result.substring(0,200):result;
        return result;

    }
    @Override
    public void deleteIndex(String indexName) {
        elasticsearchTemplate.deleteIndex(indexName);
    }

    /**
     * 获取每个栏目的自定义排序算法
     * @param indexName
     * @return
     */
    private String GetScriptPainless(String indexName)
    {
        Map<String,String> map = new HashMap<>();
        map.put("news","def Ttime = params['now'];String input = doc['intime'].value.toString();ZonedDateTime zdt =ZonedDateTime.parse(input);long intime = zdt.toInstant().toEpochMilli();def DTime =(365-(Ttime-intime)/86400000);long ytime =0;if(DTime>0){ ytime=(DTime );} return ytime*0.2");
        map.put("bbs","def Ttime = params['now'];String input = doc['intime'].value.toString();ZonedDateTime zdt =ZonedDateTime.parse(input);long intime = zdt.toInstant().toEpochMilli();def DTime =(365-(Ttime-intime)/86400000);long ytime =0;if(DTime>0){ ytime=(DTime );} return ytime*0.2");
        map.put("company","def showstype=doc['showstype'].value;def showyear = doc['showyear'].value;def isfee=doc['isfee'].value;def totle=0; if(showstype==20){showstype=0} totle=showstype*0.5+showyear*0.1+ isfee;return totle");
        map.put("showb","def zcscore = doc['zcscore'].value;def stype =doc['stype'].value;def showyear = doc['showyear'].value;def imshowbcharacter=doc['imshowbcharacter'].value;return (zcscore*0.0001 + stype + showyear*0.5 - imshowbcharacter*10);");
        map.put("webinar","def Nintime = params['now'];String input = doc['intime'].value.toString();ZonedDateTime zdt =ZonedDateTime.parse(input);long intime = zdt.toInstant().toEpochMilli();def Tintime =Nintime-2592000;def type =doc['stype'].value;def totle=0;if(intime<Tintime){totle=-type*2} return totle;");
        return map.get(indexName);
    }

    /**
     * 获取script中的参数
     * @param indexName
     * @return
     */
    private Map<String, Object> GetScriptParams(String indexName)
    {
        long datechuo = System.currentTimeMillis();
        Map<String, Object> result = new HashMap<>();
        Map<String,Map<String,Object>> map = new HashMap<>();
        Map<String, Object> params = new HashMap<>();//存放参数的map
        params.put("now", datechuo);
        map.put("news",params);  //资讯
        map.put("bbs",params);   //论坛
        map.put("webinar",params);   //网络讲堂

        result =  map.get(indexName);
        if(result==null)
        {
           return  Collections.emptyMap();
        }
        else {
            return result;
        }
    }

    private  Map<String,Integer> GetScriptBoot(String indexName)
    {
        //新闻

        Map<String,Integer> boot =  new HashMap<>();
        switch (indexName)
        {
            case "news":
            boot.put("title",10);
            boot.put("content",1);
            boot.put("username",8);
            break;
            case "company":
           //厂商
            boot.clear();
            boot.put("title",10);
            boot.put("content",1);
            boot.put("mainproduct",10);
            break;
            case "showb":
            // 仪器
            boot.clear();
            boot.put("title",10);
            boot.put("imshowtype1",1);
            boot.put("imshowtype2",1);
            boot.put("imcom",8);
            boot.put("agentname",3);
            break;
            case "bbs":
            // 论坛
            boot.clear();
            boot.put("title",10);
            boot.put("content",2);
            break;
            case "subjectnews":
            // 专题
            boot.clear();
            boot.put("title",10);
            boot.put("content",2);
            break;
            case "paper":
            // 资料
            boot.clear();
            boot.put("title",10);
            boot.put("content",2);
            break;
            case "result":
            // 谱图
            boot.clear();
            boot.put("title",10);
            boot.put("content",2);
            break;
            case "quotation":
            // 消耗品-耗材
            boot.clear();
            boot.put("title",10);
            boot.put("content",2);
            break;
            case "solutionpaper":
            // 行业应用
            boot.clear();
            boot.put("title",10);
            break;
            case "job":
            // 招聘
            boot.clear();
            boot.put("title",10);
            boot.put("content",1);
            boot.put("imcom",8);
            break;
            case "webinar":
            // 网络讲堂-
            boot.clear();
            boot.put("title",10);
            boot.put("content",3);
            boot.put("author",4);
            boot.put("immsorttwo",3);
            boot.put("file3",4);
            break;
            case "ykt":
            // 仪课通
            boot.clear();
            boot.put("title",10);
            boot.put("content",3);
            boot.put("author",4);
            break;
            default:
            boot.clear();
            boot.put("title",10);
            boot.put("content",3);
            break;
        }
        return boot;
    }
    /**
     * 获取栏目查询index和type
     * @param indexName
     * @return
     */
    private String[] GetActIndex(String indexName)
    {
        String indexName_str="";
        String[] result={indexName};
        Map<String, String> map = new HashMap<>();
        map.put("webinar","webinar,webinarmeeting");
        map.put("index","news,showb,company,bbs,webinar,webinarmeeting,ykt");

        indexName_str =  map.get(indexName);

        if(indexName_str==null)
        {
            indexName_str=  indexName;
        }
        if(indexName_str.contains(","))
        {
            result = indexName_str.split(",");
        }



        return result;

    }
}
