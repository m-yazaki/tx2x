package tx2x.xhtml;

import java.util.ArrayList;
import java.util.Iterator;

import tx2x.IntermediateTextTreeWalker;
import tx2x.core.CellInfo;
import tx2x.core.ControlText;
import tx2x.core.IntermediateText;
import tx2x.core.TableManager;

public class TableWriter {
	private TableManager m_tManager;

	/* 注目しているセル座標 */
	private int m_nX;
	private int m_nY;

	public TableWriter(ControlText cText, IntermediateTextTreeWalker cTreeWalker, boolean bDebugMode) {
		m_tManager = new TableManager(cText, cTreeWalker, bDebugMode);
	}

	public String getTableHeader(LongStyleManagerXHTML lsManager, int nLsIndex) {
		m_nX = 0;
		m_nY = 0;
		String currentLongStyle = lsManager.getLongStyleFromArrayList(nLsIndex + 1);
		String ret = "";
		if (currentLongStyle.equals("【表】【行】【セル】【本文】【本文】") || currentLongStyle
				.equals("【表】【行】【セル：ヘッダー】【本文】【本文】")/*
													 * 2013.12.19
													 */) {
			ret = "\n\t\t<table class=\"Configuration\">";
		} else if (currentLongStyle.equals("【手順】【手順】【表】【行】【セル】【本文】【本文】")) {
			ret = "\n\t\t<table class=\"Configuration Indent2\">";
		} else {
			System.out.println("【警告】表のclassが正しいことを確認してください。");
			System.out.println(currentLongStyle);
			ret = "\n\t\t<table class=\"Configuration\"><!-- " + currentLongStyle + " -->";
		}
		if (m_tManager.hasCaption()) {
			ret = ret + "\n\t\t\t<caption>" + m_tManager.getCaption() + "</caption>";
		}
		return ret;
	}

	public String getRowHeader(LongStyleManagerXHTML lsManager) {
		m_nY++;
		m_nX = 0;
		return "\t\t\t<tr>\n";
	}

	public String getHeaderCellHeader(LongStyleManagerXHTML lsManager, ControlText cText) {
		m_nX++;

		/*
		 * 結合処理
		 */
		// そもそも【[上下左右]と結合】だった場合は、セルを出力しない
		{
			IntermediateText iText = cText.getChildList().get(0);
			if (iText instanceof ControlText) {
				if (((ControlText) iText).getChildList().get(0).getChildText().matches("【[上下左右]と結合】"))
					return "";
			} else {
				if (iText.getText().matches("【[上下左右]と結合】"))
					return "";
			}
		}

		CellInfo[][] cCellSize = m_tManager.getCellSize();
		String width_attribute = "";
		String height_attribute = "";
		if (cCellSize[m_nY - 1][m_nX - 1].getWidth() > 1)
			width_attribute = " colspan=\"" + cCellSize[m_nY - 1][m_nX - 1].getWidth() + "\"";
		if (cCellSize[m_nY - 1][m_nX - 1].getHeight() > 1)
			height_attribute = " rowspan=\"" + cCellSize[m_nY - 1][m_nX - 1].getHeight() + "\"";

		// Wide100スタイルの場合は、1列目だけ幅が決められている
		if (m_tManager.getStyle().equals("Wide100") && m_nX == 1) {
			return "\t\t\t\t<th" + width_attribute + height_attribute + " class=\"" + m_tManager.getStyle() + "\">";
		}
		// Col1-NoWrap/Wide100スタイルの場合は、1列目だけ幅が決められていて、改行禁止
		else if (m_tManager.getStyle().equals("Col1-NoWrap/Wide100") && m_nX == 1) {
			return "\t\t\t\t<th class=\"Wide100\">";
		}
		// 標準
		return "\t\t\t\t<th" + width_attribute + height_attribute + ">";
	}

	public String getCellHeader(LongStyleManagerXHTML lsManager, ControlText cText) {
		m_nX++;
		/*
		 * 結合処理
		 */
		// そもそも【[上下左右]と結合】だった場合は、セルを出力しない
		{
			IntermediateText iText = cText.getChildList().get(0);
			if (iText instanceof ControlText) {
				if (((ControlText) iText).getChildList().get(0).getChildText().matches("【[上下左右]と結合】"))
					return "";
			} else {
				if (iText.getText().matches("【[上下左右]と結合】"))
					return "";
			}
		}

		CellInfo[][] cCellSize = m_tManager.getCellSize();
		String colspan_attribute = "";
		String rowspan_attribute = "";
		ArrayList<String> cClassAttribute = new ArrayList<String>();
		if (cCellSize[m_nY - 1][m_nX - 1].getWidth() > 1)
			colspan_attribute = " colspan=\"" + cCellSize[m_nY - 1][m_nX - 1].getWidth() + "\"";
		if (cCellSize[m_nY - 1][m_nX - 1].getHeight() > 1)
			rowspan_attribute = " rowspan=\"" + cCellSize[m_nY - 1][m_nX - 1].getHeight() + "\"";

		// Wide100スタイルの場合は、1列目だけ幅が決められている
		if (m_tManager.getStyle().equals("Wide100") && m_nX == 1) {
			return "\t\t\t\t<td" + colspan_attribute + rowspan_attribute + " class=\"" + m_tManager.getStyle() + "\">";
		}
		// Col1-NoWrapスタイルの場合は、1列目だけ幅が決められている
		else if (m_tManager.getStyle().equals("Col1-NoWrap") && m_nX == 1) {
			cClassAttribute.add("NoWrap");
		}
		// Col1-NoWrap/Wide100スタイルの場合は、1列目だけ幅が決められていて、改行禁止
		else if (m_tManager.getStyle().equals("Col1-NoWrap/Wide100") && m_nX == 1) {
			cClassAttribute.add("NoWrap");
		}

		// 【中央揃え】処理
		{
			IntermediateText iText = cText.getChildList().get(0);
			String sString;
			if (iText instanceof ControlText) {
				sString = ((ControlText) iText).getChildList().get(0).getChildText();
			} else {
				sString = iText.getText();
			}
			if (sString.indexOf("【中央揃え】") != -1) {
				if (sString.indexOf("【中央揃え】○") != -1) {
					cClassAttribute.add("TextCenter"); /* 2013.12.19 */
				} else if (sString.indexOf("【中央揃え】△") != -1) {
					cClassAttribute.add("TextCenter"); /* 2013.12.19 */
				} else {
					cClassAttribute.add("TextCenter"); /* 2013.12.19 */
				}
			}
		}

		String ret = "\t\t\t\t<td" + colspan_attribute + rowspan_attribute;
		if (cClassAttribute.size() > 0) {
			ret = ret + " class=\"";
			Iterator<String> itClassAttribute = cClassAttribute.iterator();
			while (itClassAttribute.hasNext()) {
				String classAttribute = itClassAttribute.next();
				ret = ret + classAttribute;
				if (itClassAttribute.hasNext())
					ret = ret + " ";
			}
			ret = ret + "\"";
		}
		ret = ret + ">";
		// 標準
		return ret;/* 2013.12.19 */
	}

	public String getCellFooter(LongStyleManagerXHTML lsManager, ControlText cText) {
		// そもそも【[上下左右]と結合】だった場合は、セルを出力しない
		IntermediateText iText = cText.getChildList().get(0);
		if (iText instanceof ControlText) {
			if (((ControlText) iText).getChildList().get(0).getChildText().matches("【[上下左右]と結合】"))
				return "";
		} else {
			if (iText.getText().matches("【[上下左右]と結合】"))
				return "";
		}
		return "</td>\n";
	}

	public String getHeaderCellFooter(LongStyleManagerXHTML lsManager, ControlText cText) {
		IntermediateText iText = cText.getChildList().get(0);
		if (iText instanceof ControlText) {
			if (((ControlText) iText).getChildList().get(0).getChildText().matches("【[上下左右]と結合】"))
				return "";
		} else {
			if (iText.getText().matches("【[上下左右]と結合】"))
				return "";
		}
		return "</th>\n";
	}

	public String getRowFooter(LongStyleManagerXHTML lsManager) {
		return "\t\t\t</tr>\n";
	}

	public String getTableFooter(LongStyleManagerXHTML lsManager, int nLsIndex) {
		return "\t\t</table>\n";
	}

}
