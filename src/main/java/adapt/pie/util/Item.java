package adapt.pie.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;

import au.com.bytecode.opencsv.CSVReader;

public class Item {

	private static Configuration config;
	private static Map<Integer, Integer> items = null;
	
	static {
		try {
			config = new HierarchicalINIConfiguration("pie-config.ini");
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}
	private static void load() {
		items = new HashMap<Integer, Integer>();
		String path = config.getString("data.ITEM");
		try {
			CSVReader reader = new CSVReader(new InputStreamReader(
					new FileInputStream(path)));
			String[] strs = reader.readNext();
			while ((strs = reader.readNext()) != null) {
				items.put(Integer.valueOf(strs[0]), Integer.valueOf(strs[2]));
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static boolean containsItem(int itemId) {
		if( items == null )
			load();
		return items.containsKey(itemId);
	}
	
	public static void main(String[] args) {
		load();
		System.out.println(items.size());
	}
}
