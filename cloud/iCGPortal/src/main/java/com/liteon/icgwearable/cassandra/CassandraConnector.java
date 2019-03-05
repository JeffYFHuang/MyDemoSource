package com.liteon.icgwearable.cassandra;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import org.json.simple.JSONObject;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.QueryBuilder;

public class CassandraConnector {
	
	private static Cluster cluster;
	private static Session session;
	
	public static Cluster findCluster(String node) {
		return Cluster.builder().addContactPoint(node).withCredentials("cassan", "Cassandra@007").build();
	} 
	
	public static Session connect(String node, String keyspace) {
		cluster = findCluster(node);
		session = cluster.connect(keyspace);
		return session;
	}
	
	public static void main(String []args) {
		session = connect("127.0.0.1", "liteon");
		String query= "select json ts as date, activeindex as value from context where uuid ='f81d4fae-7dec-11d0-a765-00a0c91e6bf6' ";
		Statement st = QueryBuilder.select().json().column("activeindex").column("ts").from("liteon", "context").where(QueryBuilder.eq("uuid","f81d4fae-7dec-11d0-a765-00a0c91e6bf6")).and(QueryBuilder.eq("ts", 1498588200));
		//Statement st = QueryBuilder.select().json().column("activeindex").as("value").column("ts").from("liteon", "context").where(QueryBuilder.eq("uuid","f81d4fae-7dec-11d0-a765-00a0c91e6bf6"));
		
		ResultSet rs = session.execute(st);
		List<Row> rowsList = rs.all();
		
		StringJoiner jsonString = new StringJoiner(",", "[", "]");
		/*for(Row row: rs.all()) {
		   String json = row.getString(0);
		   System.out.println("json"+"\n"+json);
		   jsonString.add(json);
		}*/
		System.out.println("jsonString"+"\n"+jsonString);
		
		List<String> list=new ArrayList<>();
		for(Row r:rowsList) {
			String rJson = r.getString(0);
			jsonString.add(rJson);
		}
		
		
		//System.out.println("rowsList"+"\n"+rowsList);
		/*Map<Object, Object> map = new LinkedHashMap<>();
		map.put("fitness", list);
		JSONArray jsonA = new JSONArray();
		jsonA.add(list);
		String jsonStr = JSONObject.toJSONString(map);
		List list1=new ArrayList<>();
		list1.add(jsonStr);*/
		JSONObject json = new JSONObject();
		json.put("fitness", jsonString);
		System.out.println("**jsonStr1**"+"\n"+jsonString.toString());
		System.out.println("**jsonStr2**"+"\n"+json);
		System.out.println("**list**"+"\t"+list);
		session.close();
	}
}
