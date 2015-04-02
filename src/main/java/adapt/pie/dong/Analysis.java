package adapt.pie.dong;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import adapt.pie.util.Record;

public class Analysis {
	
	public static void main(String[] args) {
		List<Record> records = Record.loadAll();
		Set<String> pairs = new HashSet<String>();
		Set<String> buy = new HashSet<String>();
		for( Record r: records) {
			String s = r.getUserId() + "\t" + r.getItemId();
			pairs.add(s);
			if( r.getBehaviorType() == 4)
				buy.add(s);
		}
		System.out.println(records.size());
		System.out.println(pairs.size());
		System.out.println(buy.size());
			
	}
}
