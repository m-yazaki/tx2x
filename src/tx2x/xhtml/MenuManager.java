package tx2x.xhtml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

/*
 * menu.htmlの読み込みを管理する
 */
public class MenuManager {

	protected static MenuManager m_cMenuManager = new MenuManager();
	protected LinkedHashMap<String, String> m_cList_DUP = new LinkedHashMap<String, String>();
	protected ArrayList<MenuNode> m_cMenuNodeList = new ArrayList<MenuNode>();

	public static MenuManager getInstance() {
		return m_cMenuManager;
	}

	public MenuNode getMenuNode(String sSearchTitle, String sSearchFilename) {
		Iterator<MenuNode> it = m_cMenuNodeList.iterator();
		sSearchTitle = sSearchTitle.replaceFirst("【サポート対象外】", "");
		while (it.hasNext()) {
			MenuNode cMenuNode = it.next();
			String sCheckTitle = cMenuNode.getTitle();
			String sCheckFilename = cMenuNode.getFilename();
			if (sCheckTitle != null)
				sCheckTitle = sCheckTitle.replaceFirst("\\[\\*\\]", "");
			if (sCheckTitle == null || sCheckFilename == null)
				continue;
			if (sCheckTitle.equals(sSearchTitle) && sCheckFilename.equals(sSearchFilename)) {
				ArrayList<MenuNode> cParents = new ArrayList<MenuNode>();
				// 親を辿ってタイトルリストを作成する
				String sParentTitle = sCheckTitle;
				String[] nums = sParentTitle.split("[\\. ]");
				Iterator<MenuNode> it2 = m_cMenuNodeList.iterator();
				while (it2.hasNext()) {
					MenuNode cMenuNodeTemp = it2.next();
					if (cMenuNodeTemp == cMenuNode)
						break;
					if (cMenuNodeTemp.getTitle() == null)
						continue;
					if (cMenuNodeTemp.getTitle().equals(cMenuNode.getPartNode().getTitle())) {
						cParents.add(cMenuNodeTemp);
					} else if (cParents.size() == 1 && cMenuNodeTemp.getTitle().startsWith(nums[0] + ".")) {
						cParents.add(cMenuNodeTemp);
					} else if (cParents.size() == 2
							&& cMenuNodeTemp.getTitle().startsWith(nums[0] + "." + nums[1] + " ")) {
						cParents.add(cMenuNodeTemp);
					}
				}
				cMenuNode.setParents(cParents);
				return cMenuNode;
			}
		}

		// 親切なメッセージを出す
		System.out.println("テキストファイルのタイトル「" + sSearchTitle + "」とmenu.htmlのタイトルが食い違っているようです。下記を参考に合わせてください。");
		it = m_cMenuNodeList.iterator();
		while (it.hasNext()) {
			MenuNode cMenuNode = it.next();
			String sTitle = cMenuNode.getTitle();
			String sFilename = cMenuNode.getFilename();

			if (sFilename == null) {
				// ファイル名が無かった場合はマニュアル名称
				// // "設定・構築";
				// String sManualName = cMenuNode.getManualName();
			} else {
				String sTitleNo = sTitle.split(" ")[0];
				String sTitleSearchTitle = sSearchTitle.split(" ")[0];
				if (sTitleNo.equals(sTitleSearchTitle)) {
					System.out.println("■" + cMenuNode.getManualName());
					System.out.println(sTitle);
				}
			}
		}

		return null;
	}
}
