package adapt.pie.dong;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;

import adapt.pie.util.Record;
import adapt.pie.util.RecordFilter;

public class RuleBased {
	public static void main(String[] args) throws ConfigurationException, IOException, ParseException {
		Date d = Record.sdf.parse("2014-12-18 18");
		List<Record> records = Record.getRecords(new RecordFilter("2014-12-18 00", "2014-12-18 24"));
		Set<String> shoppingcart = new HashSet<String>();
		Set<String> buy = new HashSet<String>();
		for( Record r: records) {
			if( r.getBehaviorType() == 3 && r.getDate().after(d))
				shoppingcart.add(r.getUserId() + "," + r.getItemId());
			if( r.getBehaviorType() == 4 )
				buy.add(r.getUserId() + "," + r.getItemId());
		}
		System.out.println(records.size());
		System.out.println(shoppingcart.size());
		System.out.println(buy.size());
		shoppingcart.removeAll(buy);
		System.out.println(shoppingcart.size());
		Configuration config = new HierarchicalINIConfiguration("pie-config.ini");
		
		String path = config.getString("result.RULEBASED");
		BufferedWriter bw = new BufferedWriter(new FileWriter(path));
		bw.write("user_id,item_id");
		bw.newLine();
		for(String s: shoppingcart) {
			bw.write(s);
			bw.newLine();
		}
		bw.flush();
		bw.close();
	}
}
