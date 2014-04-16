
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;

import java.net.MalformedURLException;
import java.util.Properties;
import java.io.BufferedReader;

import java.io.FileReader;


public class SolrJSearcher {
  public static void main(String[] args) throws MalformedURLException, SolrServerException {
	 
	  String shost="", queries_file="";
	  Properties properties;
	  int node_num=0, shard_num=0, zk_standalone=0, zk_num=0, jettyport=0, num_query_threads = 0;
	  try (FileReader reader = new FileReader("config.properties")) {
			properties = new Properties();
			properties.load(reader);
			shost = properties.getProperty("hosts");
			node_num = Integer.parseInt(properties.getProperty("node_num"));
			shard_num = Integer.parseInt(properties.getProperty("shard_num"));
			zk_standalone = Integer.parseInt(properties.getProperty("zk_standalone"));
			zk_num = Integer.parseInt(properties.getProperty("zk_num"));
			jettyport = Integer.parseInt(properties.getProperty("jettyport"));
			queries_file = properties.getProperty("samplequery_file");
			num_query_threads = Integer.parseInt(properties.getProperty("num_query_threads"));
			System.out.println(shost);
		} catch (Exception e) {
			e.printStackTrace();
		}
	  shost = shost.replaceAll("\\(", "");
	  shost = shost.replaceAll("\"", "");
	  
	  String[] hosts = shost.split(" ");
	  System.out.println(String.format("node_num: %d, shard_num: %d, zk_standalone: %d, zk_num: %d, jettyport: %d", node_num, shard_num, zk_standalone, zk_num, jettyport) );
	  

	 
	StringBuffer sbuf = new StringBuffer();
	try{
		BufferedReader bufferedReader = new BufferedReader(new FileReader(queries_file));
		String line = null;
		while((line =bufferedReader.readLine())!=null){
			sbuf.append(line).append("\n");
		}
//		System.out.println(stringBuffer);
	}catch(Exception e){
		e.printStackTrace();
	}
	
	String s = sbuf.toString();
	String[] qs = s.split("\n");
	int len = qs.length;
	System.out.println("=========================================");
	System.out.println(len);
	System.out.println("First: "+qs[0]);
	System.out.println("Last: "+qs[len-1]);
	
	Runnable[] r = new QueryLoop[num_query_threads];
	Thread[]   t = new Thread[num_query_threads];
	for(int i=0; i < num_query_threads; i++){
		r[i] = new QueryLoop(hosts, i, qs, node_num, jettyport);
		t[i] = new Thread(r[i]);
		t[i].start();
	}

  }
  

}
