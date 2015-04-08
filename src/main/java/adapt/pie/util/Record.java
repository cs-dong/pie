package adapt.pie.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;

import au.com.bytecode.opencsv.CSVReader;

public class Record implements Comparable<Record> {

	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH");
	public int userId;
	public int itemId;
	public int behaviorType;
	public String userGeohash;
	public int itemCategory;
	public Date date;

	public Record(String[] strs) {
		userId = Integer.valueOf(strs[0]);
		itemId = Integer.valueOf(strs[1]);
		behaviorType = Integer.valueOf(strs[2]);
		userGeohash = strs[3];
		itemCategory = Integer.valueOf(strs[4]);
		try {
			date = sdf.parse(strs[5]);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public int getBehaviorType() {
		return behaviorType;
	}

	public void setBehaviorType(int behaviorType) {
		this.behaviorType = behaviorType;
	}

	public String getUserGeohash() {
		return userGeohash;
	}

	public void setUserGeohash(String userGeohash) {
		this.userGeohash = userGeohash;
	}

	public int getItemCategory() {
		return itemCategory;
	}

	public void setItemCategory(int itemCategory) {
		this.itemCategory = itemCategory;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String toString() {
		return userId + "\t" + itemId + "\t" + behaviorType + "\t"
				+ userGeohash + "\t" + itemCategory + "\t" + sdf.format(date);
	}

	private static List<Record> records = null;

	public static List<Record> loadAll() {
		if (records == null) {
			records = new ArrayList<Record>();
			Configuration config = null;
			try {
				config = new HierarchicalINIConfiguration("pie-config.ini");
			} catch (ConfigurationException e) {
				e.printStackTrace();
			}

			String path = config.getString("data.USER");
			try {
				CSVReader reader = new CSVReader(new InputStreamReader(
						new FileInputStream(path)));
				String[] strs = reader.readNext();
				while ((strs = reader.readNext()) != null) {
					records.add(new Record(strs));
				}
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Collections.sort(records);
		}
		return records;
	}

	public static List<Record> getRecords(RecordFilter filter) {
		List<Record> ret = new ArrayList<Record>();
		List<Record> records = loadAll();
		for (Record r : records) {
			if (filter.accept(r))
				ret.add(r);
		}
		return ret;
	}

	public static Map<Integer, List<Record>> getUserRecords(List<Record> records) {
		Map<Integer, List<Record>> userRecords = new HashMap<Integer, List<Record>>();
		for (Record r : records) {
			Integer key = r.getUserId();
			if (!userRecords.containsKey(key))
				userRecords.put(key, new ArrayList<Record>());
			userRecords.get(key).add(r);
		}
		return userRecords;
	}

	public static Map<Integer, List<Record>> getItemRecords(List<Record> record) {
		Map<Integer, List<Record>> itemRecords = new HashMap<Integer, List<Record>>();
		List<Record> records = loadAll();
		for (Record r : records) {
			Integer key = r.getItemId();
			if (!itemRecords.containsKey(key))
				itemRecords.put(key, new ArrayList<Record>());
			itemRecords.get(key).add(r);
		}
		return itemRecords;
	}

	public int compareTo(Record o) {
		if( date.before(o.getDate()))
			return -1;
		else if( date.after(o.getDate()))
			return 1;
		return 0;
	}
	
	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		List<Record> records = loadAll();
		System.out.println(records.size());
		System.out.println(System.currentTimeMillis() - start);
	}
}
