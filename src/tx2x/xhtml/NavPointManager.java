package tx2x.xhtml;

import java.util.ArrayList;
import java.util.Iterator;

public class NavPointManager {
	private ArrayList<NavPointInfo> m_cNavPointInfoItems;
	private String m_sHref;

	public NavPointManager() {
		m_cNavPointInfoItems = new ArrayList<NavPointInfo>();
		m_sHref = null;
	}

	public void setHref(String sHref) {
		m_sHref = sHref;
	}

	public void add(String sText, int nLevel, String sName) {
		m_cNavPointInfoItems.add(new NavPointInfo(sText, m_sHref + "#" + sName,
				nLevel));
	}

	public Iterator<NavPointInfo> iterator() {
		return m_cNavPointInfoItems.iterator();
	}
}