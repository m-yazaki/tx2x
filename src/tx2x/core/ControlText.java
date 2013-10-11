package tx2x.core;

import java.util.ArrayList;

public class ControlText extends IntermediateText {

	ArrayList<IntermediateText> m_cChildList = null;

	// line == null の場合は、ControlTextと言ってもいいかも？
	public ControlText(Style ruleBlock, String line) {
		super(ruleBlock, line);
		m_cChildList = new ArrayList<IntermediateText>();
	}

	public boolean hasChild() {
		return true;
	}

	public ArrayList<IntermediateText> getChildList() {
		return m_cChildList;
	}

	public String getText() {
		String sText = "";
		for (int i = 0; i < m_cChildList.size(); i++) {
			sText = sText + m_cChildList.get(i).getText();
		}
		return sText;
	}
}
