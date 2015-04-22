package adapt.pie.dong;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;

import adapt.pie.eval.EvalUtil;
import adapt.pie.util.Item;
import adapt.pie.util.Record;
import adapt.pie.util.RecordFilter;

public class RuleBased {

	public static void main(String[] args) throws IOException {
		System.out.println("2014-12-16");
		run("2014-12-16", "2014-12-17");
		System.out.println("2014-12-07");
		run("2014-12-07", "2014-12-08");
		System.out.println("2014-12-01");
		run("2014-12-01", "2014-12-02");
		System.out.println("2014-11-25");
		run("2014-11-25", "2014-11-26");
	}

	public static void run(String d1, String d2) throws IOException {
		List<Record> recommend = candidate(d1);
		Collections.reverse(recommend);
		Set<String> rec = new HashSet<String>();
		for (int i = 0; i < 450 && i < recommend.size(); ++i) {
			Record r = recommend.get(i);
			rec.add(r.getUserId() + "," + r.getItemId());
		}
		write(rec);
		System.out.println(rec.size());
		HashMap<Integer, HashMap<Integer, Integer>> testdatamap = EvalUtil
				.SelectDataMap(d2 + " 00", d2 + " 24");
		EvalUtil.evaluate(testdatamap);
		
		recommend = rule1(d1);
		Collections.reverse(recommend);
		rec = new HashSet<String>();
		for (int i = 0; i < 450 && i < recommend.size(); ++i) {
			Record r = recommend.get(i);
			rec.add(r.getUserId() + "," + r.getItemId());
		}
		write(rec);
		System.out.println(rec.size());
		testdatamap = EvalUtil.SelectDataMap(d2 + " 00", d2 + " 24");
		EvalUtil.evaluate(testdatamap);
		
		recommend = rule2(d1);
		Collections.reverse(recommend);
		rec = new HashSet<String>();
		for (int i = 0; i < 450 && i < recommend.size(); ++i) {
			Record r = recommend.get(i);
			rec.add(r.getUserId() + "," + r.getItemId());
		}
		write(rec);
		System.out.println(rec.size());
		testdatamap = EvalUtil.SelectDataMap(d2 + " 00", d2 + " 24");
		EvalUtil.evaluate(testdatamap);
	}

	public static List<Record> rule1(String d1) {
		List<Record> records = Record.loadAll();
		List<Record> candidates = candidate(d1);
		Map<Integer, Integer> map = totalBuy(records);
		Collections.sort(candidates, new Comp1(map));
		return candidates;
	}

	public static List<Record> rule2(String d1) {
		List<Record> records = Record.loadAll();
		List<Record> candidates = candidate(d1);
		Map<Integer, Double> map = rate(records);
		Collections.sort(candidates, new Comp2(map));
		return candidates;
	}

	public static List<Record> candidate(String d1) {
		List<Record> records = Record.getRecords(new RecordFilter(d1 + " 00",
				d1 + " 24"));
		Set<String> buy = new HashSet<String>();
		for (Record r : records) {
			if (Item.containsItem(r.getItemId()) && r.getBehaviorType() == 4)
				buy.add(r.getUserId() + "," + r.getItemId());
		}

		List<Record> candidates = new ArrayList<Record>();
		for (Record r : records) {
			if (r.behaviorType == 3 && Item.containsItem(r.getItemId())
					&& !buy.contains(r.getUserId() + "," + r.getItemId())) {
				candidates.add(r);
			}
		}
		return candidates;
	}

	public static Map<Integer, Map<Integer, Integer>> behavior(
			List<Record> records, int type) {
		Map<Integer, Map<Integer, Integer>> map = new HashMap<Integer, Map<Integer, Integer>>();
		for (Record r : records) {
			if (r.getBehaviorType() == type) {
				int userId = r.getUserId(), itemId = r.getItemId();
				if (!map.containsKey(userId))
					map.put(userId, new HashMap<Integer, Integer>());
				if (!map.get(userId).containsKey(itemId))
					map.get(userId).put(itemId, 1);
				else
					map.get(userId)
							.put(itemId, map.get(userId).get(itemId) + 1);
			}
		}
		return map;
	}

	public static Map<Integer, Integer> totalBuy(List<Record> records) {
		Map<Integer, Map<Integer, Integer>> map = behavior(records, 4);
		Map<Integer, Integer> ret = new HashMap<Integer, Integer>();
		for (int userId : map.keySet()) {
			int total = 0;
			for (int itemId : map.get(userId).keySet())
				total += map.get(userId).get(itemId);
			ret.put(userId, total);
		}
		return ret;
	}

	public static Map<Integer, Double> rate(List<Record> records) {
		Map<Integer, Double> ret = new HashMap<Integer, Double>();
		Map<Integer, Map<Integer, Integer>> map1 = behavior(records, 4);
		Map<Integer, Map<Integer, Integer>> map2 = behavior(records, 3);
		for (int userId : map2.keySet()) {
			int total = 0, buy = 0;
			if (map1.containsKey(userId)) {
				for (int itemId : map2.get(userId).keySet()) {
					total += map2.get(userId).get(itemId);
					if (map1.get(userId).containsKey(itemId))
						buy += map1.get(userId).get(itemId);
				}
				ret.put(userId, 1.0 * buy / total);
			}

		}
		return ret;
	}
	
	public static void write(Set<String> rec) {
		Configuration config;
		try {
			config = new HierarchicalINIConfiguration(
					"pie-config.ini");
			String path = config.getString("result.RULEBASEDTEST");
			BufferedWriter bw = new BufferedWriter(new FileWriter(path));
			bw.write("user_id,iter_id");
			bw.newLine();
			for (String s : rec) {
				bw.write(s);
				bw.newLine();
			}
			bw.flush();
			bw.close();
		} catch (ConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

class Comp1 implements Comparator<Record> {

	private Map<Integer, Integer> map;

	public Comp1(Map<Integer, Integer> map) {
		this.map = map;
	}

	public int compare(Record o1, Record o2) {
		int v1 = map.getOrDefault(o1.getUserId(), 0);
		int v2 = map.getOrDefault(o2.getUserId(), 0);
		if (v1 < v2)
			return -1;
		else if (v1 > v2)
			return 1;
		else
			return o1.getDate().compareTo(o2.getDate());
	}
}

class Comp2 implements Comparator<Record> {

	private Map<Integer, Double> map;

	public Comp2(Map<Integer, Double> map) {
		this.map = map;
	}

	public int compare(Record o1, Record o2) {
		double v1 = map.getOrDefault(o1.getUserId(), -1.0);
		double v2 = map.getOrDefault(o2.getUserId(), -1.0);
		if (v1 < v2)
			return -1;
		else if (v1 > v2)
			return 1;
		else
			return o1.getDate().compareTo(o2.getDate());
	}
}