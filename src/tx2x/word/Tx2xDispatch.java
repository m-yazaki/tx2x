package tx2x.word;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

public class Tx2xDispatch {
	private static final boolean bProfiling = false;
	public static final long SLEEP_INTERVAL = 400;

	static final Pattern urlPattern = Pattern.compile("(.*)\\((.*)\\)");
	static HashMap<String, Integer> cNameTimesSet;
	static HashMap<String, Long> cNameMinutesSet;

	public static void initialize() {
		if (bProfiling) {
			cNameTimesSet = new HashMap<String, Integer>();
			cNameMinutesSet = new HashMap<String, Long>();
		}
	}

	public static void SystemOutPrintlnHashMap() {
		if (bProfiling) {
			System.out.println("----------");
			List<Map.Entry<String, Integer>> timesEntries = new ArrayList<Map.Entry<String, Integer>>(
					cNameTimesSet.entrySet());
			Collections.sort(timesEntries, new Comparator<Map.Entry<String, Integer>>() {

				public int compare(Entry<String, Integer> entry1, Entry<String, Integer> entry2) {
					return ((Integer) entry2.getValue()).compareTo((Integer) entry1.getValue());
				}
			});

			for (Entry<String, Integer> s : timesEntries) {
				System.out.println(s.getKey() + "：\t" + s.getValue() + "回");
			}

			System.out.println("----------");
			List<Map.Entry<String, Long>> minutesEntries = new ArrayList<Map.Entry<String, Long>>(
					cNameMinutesSet.entrySet());
			Collections.sort(minutesEntries, new Comparator<Map.Entry<String, Long>>() {

				public int compare(Entry<String, Long> entry1, Entry<String, Long> entry2) {
					return ((Long) entry2.getValue()).compareTo((Long) entry1.getValue());
				}
			});

			for (Entry<String, Long> s : minutesEntries) {
				Date processTime = new Date(s.getValue());
				SimpleDateFormat sdf = new SimpleDateFormat("mm:ss SSS");
				System.out.println(s.getKey() + "：\t" + sdf.format(processTime) + "ms\t（"
						+ sdf.format(processTime.getTime() / cNameTimesSet.get(s.getKey())) + "）");
			}
		}
	}

	public static Variant call(Dispatch oSelection, String name, Object... attributes) {
		long lStartTime, lEndTime;
		if (bProfiling) {
			lStartTime = System.currentTimeMillis();
			if (cNameTimesSet.containsKey(name))
				cNameTimesSet.put(name, cNameTimesSet.get(name) + 1);
			else
				cNameTimesSet.put(name, 1);
			System.out.println(
					"----- Tx2xDispatch.call(oSelection, \"" + name + "\"): " + cNameTimesSet.get(name) + "-----");
		}
		// Item(3)の処理を書くこと
		String[] names = name.split("\\.");
		Dispatch oTemp = oSelection;
		int i;
		for (i = 0; i < names.length - 1; i++) {
			if (names[i].indexOf("(") != -1) {
				oTemp = parseItemN(oTemp, names[i]).getDispatch();
			} else {
				oTemp = Dispatch.get(oTemp, names[i]).getDispatch();
			}
		}

		Variant retVariant;
		if (names[i].indexOf("(") != -1) {
			retVariant = parseItemN(oTemp, names[i]);
		} else {
			retVariant = Dispatch.call(oTemp, names[i], attributes);
		}
		if (bProfiling) {
			lEndTime = System.currentTimeMillis();
			if (cNameMinutesSet.containsKey(name))
				cNameMinutesSet.put(name, cNameMinutesSet.get(name) + lEndTime - lStartTime);
			else
				cNameMinutesSet.put(name, lEndTime - lStartTime);
		}
		return retVariant;
	}

	private static Variant parseItemN(Dispatch oTemp, String s) {
		Matcher matcher = urlPattern.matcher(s);
		if (matcher.find()) {
			String sName = matcher.group(1);
			int nInt = Integer.parseInt(matcher.group(2));
			return Dispatch.call(oTemp, sName, nInt);
		}
		return Variant.VT_MISSING;
	}

	public static void put(Dispatch oSelection, String name, Object val) {
		long lStartTime, lEndTime;
		if (bProfiling) {
			lStartTime = System.currentTimeMillis();
			if (cNameTimesSet.containsKey(name))
				cNameTimesSet.put(name, cNameTimesSet.get(name) + 1);
			else
				cNameTimesSet.put(name, 1);
			if (name.equals("Style")) {
				System.out.println("----- Tx2xDispatch.put(oSelection, \"" + name + "\", \"" + val + "\") : "
						+ cNameTimesSet.get(name) + "-----");
			} else {
				System.out.println(
						"----- Tx2xDispatch.put(oSelection, \"" + name + "\") : " + cNameTimesSet.get(name) + "-----");
			}
		}
		String[] names = name.split("\\.");
		Dispatch oTemp = oSelection;
		for (int i = 0; i < names.length - 1; i++) {
			if (names[i].indexOf("(") != -1) {
				oTemp = parseItemN(oTemp, names[i]).getDispatch();
			} else {
				oTemp = Dispatch.get(oTemp, names[i]).getDispatch();
			}
		}
		Dispatch.put(oTemp, names[names.length - 1], val);
		if (bProfiling) {
			lEndTime = System.currentTimeMillis();
			if (cNameMinutesSet.containsKey(name))
				cNameMinutesSet.put(name, cNameMinutesSet.get(name) + lEndTime - lStartTime);
			else
				cNameMinutesSet.put(name, lEndTime - lStartTime);
		}
		return;
	}
}
