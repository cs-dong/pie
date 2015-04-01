package adapt.pie.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;

import au.com.bytecode.opencsv.CSVReader;

public class Record {

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH");
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

	public static List<Record> loadAll() {
		List<Record> records = new ArrayList<Record>();
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
			while( (strs = reader.readNext()) != null ) {
				records.add(new Record(strs));
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return records;
	}
}
