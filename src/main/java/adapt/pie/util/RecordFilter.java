package adapt.pie.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RecordFilter {

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH");

	private Date start;
	private Date end;

	public RecordFilter(Date start, Date end) {
		this.start = start;
		this.end = end;
	}
	
	public RecordFilter(String s1, String s2) {
		try {
			start = sdf.parse(s1);
			end = sdf.parse(s2);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Check whether the record is fallen in [start, end)
	 * @param r
	 * @return
	 */
	public boolean accept(Record r) {
		if ((r.getDate().equals(start) || r.getDate().after(start))
				&& r.getDate().before(end))
			return true;
		return false;
	}
}
