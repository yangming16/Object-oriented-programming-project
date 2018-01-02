package com.news.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

import com.news.entity.News;

public class Esutil {

	public static TransportClient client = null;

	/**
	 * 获取客户端
	 * @return
	 */
	@SuppressWarnings("resource")
	public static TransportClient getClient() {
		if (client != null) {
			return client;
		}
		/*
		 * jar包冲突 NoSuchMethodError
		 * io.netty.buffer.CompositeByteBuf.addComponents(java.lang.Iterable);
		 * io.netty.buffer.CompositeByteBuf.put("transport.type","netty3").put("http.type", "netty3");
		 */
		Settings settings = Settings.builder().put("cluster.name", "myelasticsearch").put("transport.type", "netty3").put("http.type", "netty3").put("client.transport.sniff", false).build();
		try {
			client = new PreBuiltTransportClient(settings).addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("120.78.135.116"), 9300));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return client;
	}
	
	/**
	 * 向ES中添加索引
	 * @param index
	 * @param type
	 * @param news
	 * @return
	 */
	public static String addIndex(String index, String type, News news) {
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("id", news.getId());
		hashMap.put("title", news.getTitle());
		hashMap.put("source", news.getSource());
		hashMap.put("content", news.getContent());
		hashMap.put("contentHTML", news.getContentHTML());
		hashMap.put("url", news.getUrl());
		hashMap.put("pic", news.getPic());
		hashMap.put("date", news.getDate());
		hashMap.put("comNum", news.getComNum());
		hashMap.put("popularity", news.getPopularity());
		hashMap.put("upNum", news.getUpNum());
		hashMap.put("shareNum", news.getShareNum());
		hashMap.put("genreId", news.getGenreId());
		hashMap.put("genreName", news.getGenreName());

		IndexResponse response = getClient().prepareIndex(index, type).setSource(hashMap).execute().actionGet();
		return response.getId();
	}
	
	/**
	 * 搜索title和content的内容
	 * @param key
	 * @param index
	 * @param type
	 * @param start
	 * @param row
	 * @return
	 */
	public static Map<String, Object> search(String key, String index, String type, int start, int row) {
		SearchRequestBuilder builder = getClient().prepareSearch(index);
		builder.setTypes(type);
		builder.setFrom(start);
		builder.setSize(row);

		HighlightBuilder highlightBuilder = new HighlightBuilder().field("*").requireFieldMatch(false);
		highlightBuilder.preTags("<span style=\"color:red\">");
		highlightBuilder.postTags("</span>");
		builder.highlighter(highlightBuilder);

		builder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
		if (StringUtils.isNotBlank(key)) {
			builder.setQuery(QueryBuilders.multiMatchQuery(key, "title", "content"));
		}
		builder.setExplain(true);
		SearchResponse searchResponse = builder.get();

		SearchHits hits = searchResponse.getHits();
		long total = hits.getTotalHits();
		Map<String, Object> map = new HashMap<String, Object>();
		SearchHit[] hits2 = hits.getHits();
		map.put("count", total);
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (SearchHit searchHit : hits2) {
			Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
			HighlightField highlightField = highlightFields.get("title");
			Map<String, Object> source = searchHit.getSource();
			if (highlightField != null) {
				Text[] fragments = highlightField.fragments();
				String name = "";
				for (Text text : fragments) {
					name += text;
				}
				source.put("title", name);
			}
			HighlightField highlightField2 = highlightFields.get("content");
			if (highlightField2 != null) {
				Text[] fragments = highlightField2.fragments();
				String content = "";
				for (Text text : fragments) {
					content += text;
				}
				source.put("content", content);
			}
			list.add(source);
		}
		map.put("dataList", list);
		return map;
	}
	
	/**
	 * 获取node节点信息
	 * @param client
	 */
	public static void getInfo(TransportClient client) {
        List<DiscoveryNode> nodes = client.connectedNodes();
        for (DiscoveryNode node : nodes) {
            System.out.println(node.getHostAddress());
        }
    }
	
	/**
	 * 删除ES索引
	 * @param client
	 * @param index
	 */
	public static void deleteIndex(TransportClient client, String index) {
		IndicesExistsRequest inExistsRequest = new IndicesExistsRequest(index);
		IndicesExistsResponse inExistsResponse = client.admin().indices().exists(inExistsRequest).actionGet();
		if(inExistsResponse.isExists()) {
			DeleteIndexResponse dResponse = client.admin().indices().prepareDelete(index).execute().actionGet();
			if(dResponse.isAcknowledged()) {
				System.out.println("删除索引" + index + "成功！");
			}
		}
	}
	
	public static void main(String[] args) {
		TransportClient client = getClient();
		getInfo(client);
		deleteIndex(client, Constants.ESIndex);
		System.out.println("Delete over from ES!");
		// GetResponse response = client.prepareGet(Constants.ESIndex, Constants.ESType, "0").setOperationThreaded(false).get();
        // System.out.println(response.getSourceAsString());
	}
	
}