package com.apple.transportClientTest;

import com.apple.pojo.Content;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Test;

import java.net.InetAddress;
import java.util.Iterator;

/**
 * Package: com.apple.transportClientTest
 * ClassName:ClientTest
 * date: 2019/7/17 22:41
 *
 * @author:吴沛恒
 * @version:1.0
 */
public class ClientTest {

    @Test
    public void createIndexTest() throws Exception{
        //创建Settings对象
        Settings settings = Settings.builder().put("cluster.name", "elasticsearch").build();
        TransportClient client = new PreBuiltTransportClient(Settings.EMPTY)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300));

        //创建document
        XContentBuilder contentBuilder = XContentFactory.jsonBuilder()
                .startObject()
                .field("id", 1)
                .field("title","elasticsearch是一个基于lucene的搜索服务")
                .field("content","ElasticSearch是一个基于Lucene的搜索服务器。" +
                        "它提供了一个分布式多用户能力的全文搜索引擎，基于RESTful web接口。" +
                        "Elasticsearch是用Java开发的，并作为Apache许可条款下的开放源码发布，" +
                        "是当前流行的企业级搜索引擎。设计用于云计算中，能够达到实时搜索，稳定，" +
                        "可靠，快速，安装使用方便。")
                .endObject();

        client.prepareIndex("blog1", "user", "1").setSource(contentBuilder).get();
        //关闭资源
        client.close();

    }

    @Test
    public void createMapping() throws Exception{
        TransportClient client = new PreBuiltTransportClient(Settings.EMPTY)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"),9300));

        //设置查询条件
        SearchResponse searchResponse =
                client.prepareSearch("blog1")
                .setTypes("user")
                .setQuery(QueryBuilders.matchAllQuery()).get();

        //获得查询结果
        SearchHits hits = searchResponse.getHits();
        long totalHits = hits.getTotalHits();
        System.out.println("命中数：" + totalHits);

        Iterator<SearchHit> it = hits.iterator();
        while (it.hasNext()){
            SearchHit hitFields = it.next();
            String sourceAsString = hitFields.getSourceAsString();
            System.out.println(sourceAsString);
        }
        //释放资源
        client.close();

    }

    @Test
    public void searchByString() throws Exception{
        QueryBuilder builder1 = QueryBuilders.queryStringQuery("elasticsearch");
        QueryBuilder builder2 = QueryBuilders.idsQuery().addIds("1");
        QueryBuilder builder = QueryBuilders.boolQuery().must(builder1).must(builder2);
        searchQuery(builder);
}


    private void searchQuery(QueryBuilder query) throws Exception{
        //创建客户端
        TransportClient client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300));

        SearchResponse searchResponse =
                client.prepareSearch("blog1")
                .setTypes("user")
                .setQuery(query)
                .get();
        SearchHits hits = searchResponse.getHits();
        Iterator<SearchHit> it = hits.iterator();
        while (it.hasNext()){
            SearchHit hitFields = it.next();
            String sourceAsString = hitFields.getSourceAsString();

            System.out.println(sourceAsString);

            System.out.println(hitFields.getSourceAsMap().get("id"));
            System.out.println(hitFields.getSourceAsMap().get("title"));
            System.out.println(hitFields.getSourceAsMap().get("content"));
        }
    }


    /**
     * prepareUpdate
     */
    @Test
    public void updateIndex() throws Exception{
        TransportClient client =
                new PreBuiltTransportClient(Settings.EMPTY)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300));

        Content content = new Content();
        content.setId(1L);
        content.setTitle("搜索工作其实很快乐xxx");
        content.setContent("33333我们希望我们的搜索解决方案要快，我们希望有一个零配置和一个完全免费的搜索模式，我们希"+
                "望能够简单地使用JSON通过HTTP的索引数据，我们希望我们的搜索服务器始终可用，我们希望能够一台开 始并扩展到数"+
                "百，我们要实时搜索，我们要简单的多租户，我们希望建立一个云的解决方案。Elasticsearch旨在解决所有这些问题"+
                "和更多的问题。");

        ObjectMapper mapper = new ObjectMapper();
        String jsonStr = mapper.writeValueAsString(content);
        UpdateResponse response = client.prepareUpdate("blog1", "user", content.getId()+"")
                .setDoc(jsonStr, XContentType.JSON).get();
        System.out.println(response.status());

        client.close();
    }

    /**
     * deleteByQuery()搜索删除
     */
    @Test
    public void deleteByQuery() throws Exception{
        TransportClient client =
                new PreBuiltTransportClient(Settings.EMPTY)
                        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300));

        BulkByScrollResponse response = DeleteByQueryAction
                .INSTANCE
                .newRequestBuilder(client)
                .filter(QueryBuilders.matchQuery("query.term.title", "这")).source("blog1").get();

        long deleted = response.getDeleted();
        System.out.println("删除的条数：" + deleted);
        client.close();
    }

    /**
     * 插入多条文档数据
     */
    @Test
    public void batchIndex() throws Exception{
        TransportClient client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300));

        //批量插入数据
        ObjectMapper objectMapper = new ObjectMapper();
        for (long i = 0; i < 100; i++) {
            Content content = new Content();
            content.setId(i);
            content.setTitle(i+"搜索工作其实很快乐");
            content.setContent(i+"我们希望我们的搜索解决方案要快，我们希望有一个零配置和一个完全免费的搜索模式，我"+
                    "们希望能够简单地使用JSON通过HTTP的索引数据，我们希望我们的搜索服务器始终可用，我们希望能够一台开 始并扩展"+
                    "到数百，我们要实时搜索，我们要简单的多租户，我们希望建立一个云的解决方案。Elasticsearch旨在解决所有这些"+
                    "问题和更多的问题。");
            client.prepareIndex("blog", "user1")
                    .setSource(objectMapper.writeValueAsString(content),XContentType.JSON)
                    .get();


        }

        client.close();
    }

    /**
     * 搜索和排序
     */
    @Test
    public void queryAndSort() throws Exception{
        TransportClient client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300));

        //分页、排序查询
        SearchRequestBuilder searchRequestBuilder =
                client.prepareSearch("blog")
                .setTypes("user1")
                .setQuery(QueryBuilders.matchAllQuery());
        int pageNo = 1;
        int pageSize = 10;

        searchRequestBuilder
                .setFrom((pageNo-1)*pageSize)//设置查询起始位置
                .setSize(pageSize)//每页条数
                .addSort("id", SortOrder.ASC);

        SearchResponse searchResponse = searchRequestBuilder.get();

        //获取命中数
        SearchHits hits = searchResponse.getHits();
        System.out.println("共查询到的条数："+ hits);
        for (SearchHit hit : hits) {
            System.out.println("查询结果:"+hit.getSourceAsString());
            System.out.println("id:"+ hit.getSourceAsMap().get("id"));
            System.out.println("title:"+hit.getSourceAsMap().get("title"));
        }

        client.close();
    }
}
