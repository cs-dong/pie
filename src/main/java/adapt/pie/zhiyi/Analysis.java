package adapt.pie.zhiyi;

import java.util.List;

import adapt.pie.util.Record;

public class Analysis {
	public static List<Record> data;
	
	static {
		data = Record.loadAll();
	}
	
	public static void test1(){
		int count = 0;
		for (Record record : data){
			if(record.getBehaviorType()==4){
				count += 1;
			}
		}
		
		System.out.println("#behavior=4:"+count);
	}
	
	public static void main(String[] args){
		test1();
	}
	
}
