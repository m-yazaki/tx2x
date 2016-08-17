package tx2x.core;

import java.util.ArrayList;

public class ControlText extends IntermediateText {

	ArrayList<IntermediateText> m_cChildList = null;

	public ControlText(Style ruleBlock) {
		super(ruleBlock, ""); // ControlTextはline無し
		m_cChildList = new ArrayList<IntermediateText>();
	}

	public ArrayList<IntermediateText> getChildList() {
		return m_cChildList;
	}

	public String getText() {
		System.out.println("WARNING: getChildText();を呼び出すべきではありませんか。。");
		System.out.println(getChildText());
		String sText = "（ControlText）";
		// for (int i = 0; i < m_cChildList.size(); i++) {
		// sText = sText + m_cChildList.get(i).getText();
		// }
		return sText;
	}

	public String getChildText() {
		String sText = "";
		for (int i = 0; i < m_cChildList.size(); i++) {
			IntermediateText iText = m_cChildList.get(i);
			if (iText instanceof ControlText) {
				sText = sText + ((ControlText) m_cChildList.get(i)).getChildText();
			} else {
				sText = sText + m_cChildList.get(i).getText();
			}
		}
		return sText;
	}

	public int getIndex(IntermediateText iText) {
		for (int i = 0; i < m_cChildList.size(); i++) {
			if (iText == m_cChildList.get(i))
				return i;
		}
		return -1;
	}

	public String getDebugText(int level) {
		String sText = "(cText)" + m_cAStyle.getStyleName() + ":" + m_sLine + "\n";
		level++;
		for (int i = 0; i < m_cChildList.size(); i++) {
			String string = "";
			for (int j = 0; j < level; j++) {
				string = string.concat("\t");
			}
			sText = sText + string;
			if (m_cChildList.get(i) instanceof ControlText) {
				sText = sText + ((ControlText) m_cChildList.get(i)).getDebugText(level);
			} else {
				sText = sText + m_cChildList.get(i).getDebugText();
			}
		}
		return sText;
	}

	public String getDebugText() {
		return "-----\n" + getDebugText(0) + "-----";
	}
}
