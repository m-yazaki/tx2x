package tx2x.xhtml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * menu.htmlの読み込みを管理する
 */
public class GioPriNasMenuManager extends tx2x.xhtml.MenuManager {

	protected static GioPriNasMenuManager m_cMenuManager = new GioPriNasMenuManager();

	public static GioPriNasMenuManager getInstance() {
		return m_cMenuManager;
	}

	public void readMenuHtml(File inputFile) {
		FileReader fr = null;
		BufferedReader bf = null;
		try {
			// 入力ファイル
			fr = new FileReader(inputFile);
			bf = new BufferedReader(fr);
			String line;
			// 様々なパターン
			Pattern pManualName = Pattern.compile("<title>(.*)</title>");
			Pattern pPartName = Pattern.compile("<div class=\"CollapsiblePanelTab\" tabindex=\"0\">(.*)</div>");
			Pattern pA = Pattern.compile("<a target=\"_top\" href=\"(.*)\">(.*)</a>");
			while ((line = bf.readLine()) != null) {
				Matcher m = pManualName.matcher(line);
				if (m.find()) {
					m_cList.put(m.group(1), null);
					continue;
				}
				m = pPartName.matcher(line);
				if (m.find()) {
					Matcher mA = pA.matcher(m.group(1));
					if (mA.find()) {
						m_cList.put(mA.group(2), mA.group(1));
					} else {
						m_cList.put(m.group(1), null);
					}
					continue;
				}
				m = pA.matcher(line);
				if (m.find()) {
					m_cList.put(m.group(2), m.group(1));
				}
				continue;
			}
		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
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
}
