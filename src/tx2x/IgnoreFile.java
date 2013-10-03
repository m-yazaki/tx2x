package tx2x;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class IgnoreFile {
	private static IgnoreFile instance = new IgnoreFile();
	ArrayList<String> sIgnoreFilelist;

	private IgnoreFile() {
		sIgnoreFilelist = new ArrayList<String>();
	}

	public static IgnoreFile getInstance() {
		return instance;
	}

	public void setIgnoreFiles(File cTx2xIgnore) {
		sIgnoreFilelist.add(cTx2xIgnore.getName());
		try {
			// 入力ファイル
			BufferedReader bf = new BufferedReader(new InputStreamReader(
					new FileInputStream(cTx2xIgnore), "UTF-8"));

			String line;
			while ((line = bf.readLine()) != null) {
				// ループ
				sIgnoreFilelist.add(line);
			}

			bf.close();
		} catch (FileNotFoundException e1) {
			Tx2x.appendWarn("ファイルが見つかりません@IgnoreFile："
					+ cTx2xIgnore.getAbsolutePath());
			return;
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

	}

	public boolean isIgnore(File file) {
		// file.getAbsolutePath()：D:\test-text
		String sAbsolutePath = file.getAbsolutePath();
		String[] sPart = sAbsolutePath.split("[/\\\\]");
		for (int i = 0; i < sIgnoreFilelist.size(); i++) {
			for (int j = 0; j < sPart.length; j++) {
				if (sPart[j].matches(sIgnoreFilelist.get(i))) {
					return true;
				}
			}
		}
		return false;
	}
}
