package tx2x.xhtml;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * menu.htmlの読み込みを管理する
 */
public class MenuManager {

	protected static MenuManager m_cMenuManager = new MenuManager();
	protected LinkedHashMap<String, String> m_cList = new LinkedHashMap<String, String>();

	public static MenuManager getInstance() {
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
			Pattern pA = Pattern.compile("<a href=\"(.*)\">(.*)</a>");
			while ((line = bf.readLine()) != null) {
				Matcher m = pManualName.matcher(line);
				if (m.find()) {
					m_cList.put(m.group(1), null);
					continue;
				}
				m = pPartName.matcher(line);
				if (m.find()) {
					m_cList.put(m.group(1), null);
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

	public MenuNode getMenuNode(String sSearchTitle) {
		MenuNode ret = new MenuNode();
		Iterator<String> it = m_cList.keySet().iterator();
		String sPrevFilename = null;
		sSearchTitle = sSearchTitle.replaceFirst("【サポート対象外】", "");
		while (it.hasNext()) {
			String sTitle = it.next();
			String sFilename = m_cList.get(sTitle);
			sTitle = sTitle.replaceFirst("\\[\\*\\]", "");

			if (sFilename == null) {
				// ファイル名が無かった場合はマニュアル名称または編名称
				if (ret.getManualName_DUP() == null) {
					ret.setManualName(sTitle);
				} else {
					ret.setPartName(sTitle);
				}
			} else {
				if (sTitle.equals(sSearchTitle)) {
					if (sPrevFilename == null)
						sPrevFilename = "index.html";
					LinkedHashMap<String, String> cParents = new LinkedHashMap<String, String>();
					// 親のタイトルを決定する
					String sParentTitle = sTitle;
					String[] nums = sParentTitle.split("[\\. ]");
					Iterator<String> it2 = m_cList.keySet().iterator();
					int nHit = 0;
					while (it2.hasNext()) {
						String sTitleTemp = it2.next();
						if (nHit == 0 && sTitleTemp.equals(ret.getPartName())) {
							// マニュアル名と完全一致
							nHit++;
							// cParents.put(sTitleTemp,
							// m_cList.get(sTitleTemp));
						} else if (nHit == 1 && sTitleTemp.startsWith(nums[0] + ".")) {
							if (getTitleLevel(nums) > 2) {
								nHit++;
								cParents.put(sTitleTemp, m_cList.get(sTitleTemp));
							}
						} else if (nHit == 2 && sTitleTemp.startsWith(nums[0] + "." + nums[1] + " ")) {
							if (getTitleLevel(nums) > 3) {
								nHit++;
								cParents.put(sTitleTemp, m_cList.get(sTitleTemp));
							}
						}
					}
					ret.setParents(cParents);

					// // "C-3.1.html";
					// String sPrevFilename = cMenuNode.getPrevFilename();
					ret.setPrevFilename(sPrevFilename);

					// // "C-3.1.2.html";
					// String sNextFilename = cMenuNode.getNextFilename();
					String sNextFilename;
					if (it.hasNext()) {
						String sNextTitle = it.next();
						sNextFilename = m_cList.get(sNextTitle);
						while (sNextFilename == null && it.hasNext()) {
							sTitle = it.next();
							sNextFilename = m_cList.get(sTitle);
						}
					} else {
						sNextFilename = "index.html";
					}
					ret.setNextFilename(sNextFilename);
					return ret;
				}
				sPrevFilename = sFilename;
			}
		}

		// 親切なメッセージを出す
		System.out.println("テキストファイルのタイトル「" + sSearchTitle + "」とmenu.htmlのタイトルが食い違っているようです。下記を参考に合わせてください。");
		it = m_cList.keySet().iterator();
		while (it.hasNext()) {
			String sTitle = it.next();
			String sFilename = m_cList.get(sTitle);

			if (sFilename == null) {
				// ファイル名が無かった場合はマニュアル名称
				// // "設定・構築";
				// String sManualName = cMenuNode.getManualName();
			} else {
				String sTitleNo = sTitle.split(" ")[0];
				String sTitleSearchTitle = sSearchTitle.split(" ")[0];
				if (sTitleNo.equals(sTitleSearchTitle)) {
					System.out.println("■" + ret.getManualName_DUP());
					System.out.println(sTitle);
				}
			}
		}

		return null;
	}

	private int getTitleLevel(String[] nums) {
		int i = 0;
		try {
			for (; i < nums.length; i++) {
				Integer.parseInt(nums[i]);
			}
		} catch (NumberFormatException e) {
			// 問題ない
		}
		return i + 1;
	}
}
