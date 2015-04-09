package adapt.pie.eval;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;

import adapt.pie.util.Item;
import adapt.pie.util.Record;
import adapt.pie.util.RecordFilter;

public class EvalUtil {
	static List<Record> data = null;
	static Configuration config = null;
	static String resultPath = null;
	
	static {
		data = Record.loadAll();
		try {
			config = new HierarchicalINIConfiguration("pie-config.ini");
			resultPath = config.getString("data.RESULT");
			System.out.println(resultPath);
			
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	public static HashMap<Integer,HashMap<Integer,Integer>> SelectDataMap(String startDate, String endDate){
		
		HashMap<Integer,HashMap<Integer,Integer>> datamap = 
				new HashMap<Integer,HashMap<Integer,Integer>>();
		
		RecordFilter filter = new RecordFilter(startDate,endDate);
		
		for (Record record : data){
			if(record.getBehaviorType() == 4 
					&& filter.accept(record)
					&& Item.containsItem(record.getItemId())){ //商品子集P
				
				Integer userid = record.getUserId();
				Integer itemid = record.getItemId();
				
				if (!datamap.containsKey(userid)){
					datamap.put(userid, new HashMap<Integer,Integer>());
				}
				if (!datamap.get(userid).containsKey(itemid)){
					datamap.get(userid).put(itemid, 0);
				}
				
				datamap.get(userid).put(itemid, datamap.get(userid).get(itemid)+1);
				
			}
		}
		
		return datamap;
	}
	
	public static void evaluate(HashMap<Integer,HashMap<Integer,Integer>> testdatamap)
			throws IOException{
		
		double correct = 0;
		double numberOfResults = 0;
		
		BufferedReader reader = new BufferedReader(new InputStreamReader
				(new FileInputStream(resultPath)));
		
		String line = null;
		while ((line = reader.readLine()) != null){
			String[] partLine = line.split(",");
			Integer userid;
			Integer itemid;
			
			try {
				userid = Integer.parseInt(partLine[0]);
				itemid = Integer.parseInt(partLine[1]);
				numberOfResults += 1;
				
			} catch (Exception ex){
				continue;
			}
			
			if (testdatamap.containsKey(userid)
					&& testdatamap.get(userid).containsKey(itemid)){
				correct += 1;
			}
		}
		reader.close();

		double precision = correct / numberOfResults;
		double recall = correct / countDataMapSize(testdatamap);
		double f1 = 2 * precision * recall / (precision + recall);
		
		System.out.println(correct);
		System.out.println(numberOfResults);
		System.out.println("precision: "+precision+", recall: "+recall);
		System.out.println("F1: " + f1);
	}
	
	public static int countDataMapSize(HashMap<Integer,HashMap<Integer,Integer>> datamap){
		int size = 0;
		
		for (Integer userid : datamap.keySet()){
			size += datamap.get(userid).keySet().size();
		}
		
		return size;
	}
	
	public static void test1() throws IOException{
		HashMap<Integer,HashMap<Integer,Integer>> testdatamap = 
				SelectDataMap("2014-12-17 24","2014-12-18 24");
		
		evaluate(testdatamap);
	}
	
	public static void main(String[] args) throws IOException{
		test1();
	}
}
