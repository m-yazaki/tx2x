package tx2x;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class TocManager {
	private static TocManager instance = new TocManager();

	public static TocManager getInstance() {
		return instance;
	}

	ArrayList<String> m_sTocList = null;

	public void open(String sTextFilename) {
		FileReader fr = null;
		BufferedReader bf = null;
		try {
			// 入力ファイル
			File inputFile = new File(sTextFilename);
			fr = new FileReader(inputFile);
			bf = new BufferedReader(fr);
			String line;
			while ((line = bf.readLine()) != null) {
				if (line.matches("【(インデックス|章|節|項|項下)】.*")) {
					m_sTocList.add(line);
				}
			}
		} catch (FileNotFoundException e1) {
			Tx2x.appendWarn("ファイルが見つかりません@TocManager：" + sTextFilename);
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} finally {
			try {
				if (bf != null)
					bf.close();
				if (fr != null)
					fr.close();
			} catch (IOException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
	}

	private TocManager() {
		m_sTocList = new ArrayList<String>();
	}

	public String isIncludeToc(String line) throws IOException {
		line = line.replace("*", "\\*");
		line = line.replace("(", "\\(");
		line = line.replace(")", "\\)");
		Iterator<String> it = m_sTocList.iterator();
		ArrayList<String> tocHopefuls = new ArrayList<String>();
		while (it.hasNext()) {
			String tocItem = (String) it.next();
			if (tocItem.matches("【[^】]+】" + line)) {
				tocHopefuls.add(tocItem);
				break;
			}
		}
		if (tocHopefuls.size() == 0) {
			return null;
		} else if (tocHopefuls.size() == 1) {
			return tocHopefuls.get(0);
		} else {
			System.out.println("候補が2つもありますね！！：" + line);
			return tocHopefuls.get(0);
		}
	}
}
