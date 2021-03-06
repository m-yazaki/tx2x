package tx2x;

import java.util.HashMap;

public class Tx2xOptions {
	private static Tx2xOptions instance = new Tx2xOptions();
	HashMap<String, Object> cOptions;

	public static Tx2xOptions getInstance() {
		return instance;
	}

	Tx2xOptions() {
		cOptions = new HashMap<String, Object>();
		// 変換対象テキストファイル名
		cOptions.put("tx2x_folder_file_name", "");
		// 対象メーカー指定
		cOptions.put("maker", "");
		// デバッグモード
		cOptions.put("debug", false);
		// 変換モード
		cOptions.put("mode", "InDesign-Windows");
		// InDesign CS5モード
		cOptions.put("InDesignCS5", true);
		// Word等を表示するかどうか
		cOptions.put("Visible", true);
	}

	public String getString(String string) {
		if (cOptions.containsKey(string) == false) {
			System.out.println("Tx2xOptions未定義ERROR: \"" + string + "\"の初期値（String）を定義してください。");
			return null;
		}
		return (String) cOptions.get(string);
	}

	public boolean getBoolean(String string) {
		if (cOptions.containsKey(string) == false) {
			System.out.println("Tx2xOptions未定義ERROR: \"" + string + "\"の初期値（Boolean）を定義してください。");
			return false;
		}
		return (Boolean) cOptions.get(string);
	}

	public void setOption(String string, boolean bBoolean) {
		cOptions.put(string, bBoolean);
	}

	public void setOption(String string, String sString) {
		cOptions.put(string, sString);
	}
}
