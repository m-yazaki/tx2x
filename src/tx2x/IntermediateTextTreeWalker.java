package tx2x;

import java.util.ArrayList;
import java.util.Iterator;

import tx2x.core.ControlText;
import tx2x.core.IntermediateText;

public class IntermediateTextTreeWalker {
	private static final boolean LOCAL_DEBUG_MODE = false;

	public static final int SHOW_CONTROL_TEXT = 0;
	public static final int SHOW_INTERMEDIATE_TEXT = 1;

	IntermediateText m_cRoot;
	IntermediateText m_cCurrentNode;
	ArrayList<IntermediateText> m_cParents; // m_cCurrentNodeからm_cRootまでのルートを確保する

	public IntermediateTextTreeWalker(ControlText cRootText) {
		m_cRoot = cRootText;
		m_cCurrentNode = m_cRoot;
		m_cParents = new ArrayList<IntermediateText>();
	}

	// この属性は、TreeWalker を使用して表されるノード型を判定します。
	int getWhatToShow() {
		if (m_cCurrentNode instanceof ControlText) {
			return SHOW_CONTROL_TEXT;
		} else {
			return SHOW_INTERMEDIATE_TEXT;
		}
	}

	// TreeWalker が現在あるノード。
	public IntermediateText getCurrentNode() {
		return m_cCurrentNode;
	}

	// 生成時に指定された TreeWalker の root ノード。
	public IntermediateText getRoot() {
		m_cParents.clear();
		m_cCurrentNode = m_cRoot;
		return m_cCurrentNode;
	}

	// 現在のノードのもっとも近い可視の上位ノードに移動し、そのノードを返します。
	public ControlText parentNode() {
		if (LOCAL_DEBUG_MODE) {
			System.out.println("parentNode:親ノードに移動する");
			Iterator<IntermediateText> it = m_cParents.iterator();
			while (it.hasNext()) {
				IntermediateText iText = it.next();
				System.out.println(" > " + iText.getStyle().getStyleName() + " : " + iText.getText());
			}
		}
		if (m_cParents.size() > 0) {
			m_cCurrentNode = m_cParents.remove(m_cParents.size() - 1);
			return (ControlText) m_cCurrentNode;
		}
		// 親がない（m_cRootと同じオブジェクトを指しているはず）
		return null;
	}

	// 現在のノードのもっとも近い可視の上位ノードに移動しないで、そのノードを返します。
	public ControlText peekParent() {
		if (LOCAL_DEBUG_MODE)
			System.out.println("peekParentNode:親ノードを見る");
		if (m_cParents.size() > 0) {
			return (ControlText) m_cParents.get(m_cParents.size() - 1);
		}
		// 親がない（m_cRootと同じオブジェクトを指しているはず）
		return null;
	}

	public ControlText peekParent(int level) {
		if (LOCAL_DEBUG_MODE)
			System.out.println("peekParentNode:親ノードを見る(level)");
		if (m_cParents.size() > level - 1) {
			return (ControlText) m_cParents.get(m_cParents.size() - level);
		}
		// 親がない（m_cRootと同じオブジェクトを指しているはず）
		return null;
	}

	// TreeWalker を現在のノードの最初の可視の子に移動し、新規ノードを返します。
	public IntermediateText firstChild() {
		if (LOCAL_DEBUG_MODE) {
			System.out.println("firstChild:最初の子に移動する");
			System.out.println("m_sCurrentNode:" + m_cCurrentNode.getText());
		}
		if (m_cCurrentNode instanceof ControlText) {
			m_cParents.add(m_cCurrentNode);
			IntermediateText cFirstChild = ((ControlText) m_cCurrentNode).getChildList().get(0);
			m_cCurrentNode = cFirstChild;
			if (LOCAL_DEBUG_MODE)
				System.out.println("return " + m_cCurrentNode.getText());
			return cFirstChild;
		}
		return null;
	}

	// TreeWalker を現在のノードの最後の可視の子まで移動させ、新規ノードを返します。
	public IntermediateText lastChild() {
		if (LOCAL_DEBUG_MODE)
			System.out.println("lastChild:最後の子に移動する");
		if (m_cCurrentNode instanceof ControlText) {
			m_cParents.add(m_cCurrentNode);
			ArrayList<IntermediateText> childList = ((ControlText) m_cCurrentNode).getChildList();
			m_cCurrentNode = childList.get(childList.size() - 1);
			return m_cCurrentNode;
		}
		return null;
	}

	// TreeWalker を現在のノードの前の兄弟まで移動させ、新規ノードを返します。
	public IntermediateText previousSibling() {
		if (LOCAL_DEBUG_MODE)
			System.out.println("previousSibling:前の兄弟に移動する");
		if (m_cParents.size() > 0) {
			IntermediateText cCurrentNode = m_cCurrentNode; // parentNode()やnextSibling()は、m_cCurrentNodeを動かしてしまうので、戻すためにとっておく。
			ControlText cParents = parentNode();
			int nCurrentNode = cParents.getChildList().lastIndexOf(cCurrentNode);
			if (nCurrentNode - 1 >= 0) {
				// まだ前がある
				m_cParents.add(m_cCurrentNode);
				m_cCurrentNode = cParents.getChildList().get(nCurrentNode - 1);
				return m_cCurrentNode;
			} else {
				// m_cCurrentNodeを元に戻す
				firstChild();
				while (m_cCurrentNode != cCurrentNode) {
					previousSibling();
				}
				return null;
			}
		}
		// 親がない（m_cRootと同じオブジェクトを指しているはず）
		return null;
	}

	// TreeWalker を現在のノードの次の兄弟まで移動させ、新規ノードを返します。
	public IntermediateText nextSibling() {
		if (LOCAL_DEBUG_MODE) {
			System.out.println("nextSibling:次の兄弟に移動する");
			System.out.println(m_cCurrentNode.getDebugText());
		}
		if (m_cParents.size() > 0) {
			IntermediateText cCurrentNode = m_cCurrentNode; // parentNode()やnextSibling()は、m_cCurrentNodeを動かしてしまうので、戻すためにとっておく。
			ControlText cParents = parentNode(); // parentNodeを取り損ねている
			int nCurrentNode = cParents.getChildList().lastIndexOf(cCurrentNode);
			if (nCurrentNode + 1 < cParents.getChildList().size()) {
				// まだ次がある
				m_cParents.add(m_cCurrentNode);
				m_cCurrentNode = cParents.getChildList().get(nCurrentNode + 1);
				return m_cCurrentNode;
			} else {
				// m_cCurrentNodeを元に戻す
				firstChild();
				while (m_cCurrentNode != cCurrentNode) {
					nextSibling();
				}
				return null;
			}
		}
		// 親がない（m_cRootと同じオブジェクトを指しているはず）
		return null;
	}

	// TreeWalker を現在のノードについてドキュメント順に前の可視ノードまで移動し、新規ノードを返します。
	IntermediateText previousNode() {
		// 未実装
		if (LOCAL_DEBUG_MODE)
			System.out.println("previousNode:前の可視ノードに移動する");
		return null;
	}

	// TreeWalker を現在のノードについてドキュメント順に次の可視ノードまで移動し、新規ノードを返します。
	public IntermediateText nextNode() {
		if (LOCAL_DEBUG_MODE)
			System.out.println("nextNode:次の可視ノードに移動する");
		// 兄弟を探す
		IntermediateText iSibling = nextSibling();
		if (iSibling != null) {
			return m_cCurrentNode;
		}

		// ここにくるときは、nextSibling()した後でも、m_cCurrentNodeは移動していない。

		// 親を探す
		IntermediateText cCurrentNode = m_cCurrentNode; // parentNode()やnextSibling()は、m_cCurrentNodeを動かしてしまうので、戻すためにとっておく。
		ControlText cParent = null;
		do {
			cParent = parentNode();
			IntermediateText cParentSibling = nextSibling();
			if (cParentSibling != null) {
				return m_cCurrentNode;
			}
		} while (cParent != null);
		m_cCurrentNode = cCurrentNode;

		if (m_cCurrentNode instanceof ControlText) {
			IntermediateText iFirstChild = firstChild();
			return iFirstChild;
		}

		return null;
	}

	public String getDebugText() {
		java.util.Iterator<IntermediateText> it = m_cParents.iterator();
		String ret = "";
		while (it.hasNext()) {
			IntermediateText obj = it.next();
			ret = ret + obj.getDebugText();
		}
		return ret;
	}

	public IntermediateText peekNextSibling() {
		if (LOCAL_DEBUG_MODE)
			System.out.println("peekNextSibling:次の兄弟を確認する（移動しない）");
		if (m_cParents.size() > 0) {
			IntermediateText cCurrentNode = m_cCurrentNode; // parentNode()やnextSibling()は、m_cCurrentNodeを動かしてしまうので、戻すためにとっておく。
			ControlText cParents = parentNode();
			IntermediateText iRetText = null;
			int nCurrentNode = cParents.getChildList().lastIndexOf(cCurrentNode);
			if (nCurrentNode + 1 < cParents.getChildList().size()) {
				// 直の兄弟がいた場合
				iRetText = cParents.getChildList().get(nCurrentNode + 1);
			} else {
				// もう1つ親を遡る必要がある場合
				IntermediateText cCurrentParentNode = m_cCurrentNode;
				if (cParents != cCurrentParentNode) {
					System.out.println("cParents != cCurrentParentNode");
				}
				ControlText cGrandParents = parentNode();
				if (cGrandParents != null) {
					nCurrentNode = cGrandParents.getChildList().lastIndexOf(cCurrentParentNode);
					if (nCurrentNode + 1 < cGrandParents.getChildList().size()) {
						// まだ次がある
						IntermediateText iTemp = cGrandParents.getChildList().get(nCurrentNode + 1);
						if (iTemp instanceof ControlText) {
							ControlText iRetParentText = (ControlText) iTemp;
							iRetText = iRetParentText.getChildList().get(0);
							if ((cCurrentNode instanceof ControlText) != (iRetText instanceof ControlText)) {
								iRetText = null;
							}
						} else {
							iRetText = null;
						}
					}
					// m_cCurrentNodeを元に戻す
					firstChild();
					while (m_cCurrentNode != cCurrentParentNode) {
						nextSibling();
					}
				}
			}
			firstChild();
			while (m_cCurrentNode != cCurrentNode) {
				nextSibling();
			}
			return iRetText;
		}
		// 親がない（m_cRootと同じオブジェクトを指しているはず）
		return null;
	}

	// 次の兄弟の段落スタイルを取得する
	public String getNextSiblingStyleName() {
		IntermediateText cNextSibling = peekNextSibling();
		if (cNextSibling != null) {
			return cNextSibling.getStyle().getStyleName();
		} else {
			return null;
		}
	}
}
