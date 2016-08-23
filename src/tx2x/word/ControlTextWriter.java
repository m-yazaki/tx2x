package tx2x.word;

import static tx2x.Constants.MM;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jacob.com.ComFailException;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

import tx2x.Tx2xOptions;
import tx2x.core.ControlText;
import tx2x.core.IntermediateText;

/*
 * ControlTextをWordデータに書き出す
 */
public class ControlTextWriter {
	public void writeBigBlockOpenInfo(Dispatch oSelection, LongStyleManagerWord lsManager, ControlText cText) {
		String longStyle = lsManager.getLongStyle();
		if (longStyle.equals("【ヒント】")) {
			writeHintParts(oSelection, 0);
			return;
		} else if (longStyle.equals("【1.】【1.】【ヒント】")) {
			writeHintParts(oSelection, 15 * MM);
			return;
		} else if (longStyle.equals("【注意】")) {
			writeNoteParts(oSelection, 0, 136.9 * MM);
			return;
		} else if (longStyle.equals("【HACK】")) {
			// 表を作る
			Variant oRange = Dispatch.call(oSelection, "Range");
			Dispatch oTable = Tx2xDispatch.call(oSelection, "Document.Tables.Add", oRange, 1, 2).toDispatch();

			// HACKグレーアイコン
			String sHackFilename = Tx2xOptions.getInstance().getString("tx2x_folder_name") + "\\hack.png";
			try {
				Dispatch oInlineShape = Tx2xDispatch
						.call(oSelection, "InlineShapes.AddPicture", sHackFilename, "False", "True").toDispatch();

				Dispatch oShape = Dispatch.call(oInlineShape, "ConvertToShape").toDispatch();
				Dispatch.call(oShape, "ZOrder", 5 /* msoSendBehindText */);
				Dispatch.call(oShape, "IncrementTop", -2 * MM); // 2mm上へ移動
				Dispatch.call(oShape, "IncrementLeft", -2 * MM); // 2mm左へ移動
			} catch (ComFailException e) {
				System.out.println("---------- error ----------\n" + e.getLocalizedMessage() + "ファイル名：" + sHackFilename
						+ "\n---------------------------");
			}

			Dispatch oCell = Dispatch.call(oTable, "Cell", 1, 1).getDispatch();
			setCellWidth(oCell, 15 * MM);

			Dispatch.call(oSelection, "MoveStart", 12 /* wdCell */, 0); // 左へ移動
			Dispatch.put(oSelection, "Text", "HACK");
			Dispatch.put(oSelection, "Style", "HACK");
			Dispatch.call(oSelection, "MoveRight"); // 右へ移動
			Dispatch.call(oSelection, "TypeParagraph"); // 改行
			Pattern p = Pattern.compile("【HACK (.*)】(.*)");
			IntermediateText iText = ((ControlText) (cText.getChildList().get(0))).getChildList().get(0);
			Matcher matcher = p.matcher(iText.getText());
			matcher.find();
			Dispatch.put(oSelection, "Text", matcher.group(1));
			Dispatch.put(oSelection, "Style", "HACK-No");

			// HACKリード文のエリア
			oCell = Dispatch.call(oTable, "Cell", 1, 2).getDispatch();
			setCellWidth(oCell, 136.9 * MM);

			Dispatch.call(oCell, "Select");
			Dispatch.call(oSelection, "MoveLeft"); // 左へ移動（カーソルを立てる）
			iText.setText(matcher.group(2));
			return;
		} else if (longStyle.equals("【コード】")) {
			writeCodeParts(oSelection, 15 * MM, 134.8 * MM, RGB(236, 240, 241));
			return;
		} else if (longStyle.equals("【ヒント】【コード】")) {
			writeCodeParts(oSelection, 10 * MM, 120 * MM, RGB(236, 240, 241));
			return;
		} else if (longStyle.equals("【1.】【1.】【コード】") || longStyle.equals("【箇条書き・】【箇条書き・】【コード】")) {
			writeCodeParts(oSelection, 20 * MM, 124.8 * MM, RGB(236, 240, 241));
			return;
		} else if (longStyle.equals("【注意】【コード】")) {
			writeCodeParts(oSelection, 10 * MM, 120 * MM, RGB(253, 243, 227));
			return;
		}
		return;
	}

	private int RGB(int r, int g, int b) {
		return r + g * 256 + b * 256 * 256;
	}

	private void writeCodeParts(Dispatch oSelection, double dLeftIndent, double dWidth, int iBackgroundPatternColor) {
		// 表を作る
		Variant oRange = Dispatch.call(oSelection, "Range");
		Dispatch oTable = Tx2xDispatch.call(oSelection, "Document.Tables.Add", oRange, 1, 1).toDispatch();

		// 幅を調節
		Dispatch oCell = Dispatch.call(oTable, "Cell", 1, 1).getDispatch();
		setCellWidth(oCell, dWidth);

		// 左端からのインデント
		Tx2xDispatch.put(oTable, "Rows.LeftIndent", dLeftIndent);

		// 背景
		Tx2xDispatch.put(oTable, "Shading.BackgroundPatternColor", iBackgroundPatternColor);

		// 罫線
		for (int i = -4; i <= -1; i++) {
			/* i=-1:wdBorderTop */
			/* i=-2:wdBorderLeft */
			/* i=-3:wdBorderBottom */
			/* i=-4:wdBorderRight */
			Dispatch oBorder = Dispatch.call(oTable, "Borders", i).toDispatch();
			Dispatch.put(oBorder, "LineStyle", 4 /* wdLineStyleDashLargeGap */);
		}
	}

	public void writeSmallBlockOpenInfo() {
		return;
	}

	public void writeLineOpenInfo() {
		return;
	}

	public void writeLine() {
		return;
	}

	public void writeLineCloseInfo() {
		return;
	}

	public void writeSmallBlockCloseInfo() {
		return;
	}

	public void writeBigBlockCloseInfo(Dispatch oSelection, LongStyleManagerWord lsManager, ControlText cText) {
		String longStyle = lsManager.getLongStyle();

		/* 表組から安全に出るだけの単純なスタイル */
		if (longStyle.equals("【ヒント】") || longStyle.equals("【1.】【1.】【ヒント】") || longStyle.equals("【注意】")
				|| longStyle.equals("【HACK】") || longStyle.equals("【コード】") || longStyle.equals("【ヒント】【コード】")
				|| longStyle.equals("【1.】【1.】【コード】") || longStyle.equals("【箇条書き・】【箇条書き・】【コード】")) {
			Dispatch.call(oSelection, "TypeBackspace");
			Dispatch.call(oSelection, "MoveRight", 1 /* wdCharacter */, 2); // 右へ2つ移動
			return;
		}
		return;

	}

	private void writeHintParts(Dispatch oSelection, double dLeftIndent) {
		// 表を作る
		Variant oRange = Dispatch.call(oSelection, "Range");
		Dispatch oTable = Tx2xDispatch.call(oSelection, "Document.Tables.Add", oRange, 1, 2).toDispatch();

		// 左端からのインデント
		Tx2xDispatch.put(oTable, "Rows.LeftIndent", dLeftIndent);

		// ヒントアイコン
		Dispatch oCell = Dispatch.call(oTable, "Cell", 1, 1).getDispatch();
		setCellWidth(oCell, 15 * MM);

		Dispatch.call(oCell, "Select");
		Dispatch.put(oSelection, "Style", "本文");
		String sHintFilename = Tx2xOptions.getInstance().getString("tx2x_folder_name") + "\\hint.png";
		try {
			Tx2xDispatch.call(oSelection, "InlineShapes.AddPicture", sHintFilename, "False", "True");
		} catch (ComFailException e) {
			System.out.println("---------- error ----------\n" + e.getLocalizedMessage() + "ファイル名：" + sHintFilename
					+ "\n---------------------------");
		}

		// 本文
		oCell = Dispatch.call(oTable, "Cell", 1, 2).getDispatch();
		setCellWidth(oCell, 136.9 * MM - dLeftIndent);

		Dispatch.call(oCell, "Select");
		Dispatch.call(oSelection, "MoveLeft"); // 左へ移動（カーソルを立てる）
	}

	private void writeNoteParts(Dispatch oSelection, double dLeftIndent, double dBodyWidth) {
		// 表を作る
		Variant oRange = Dispatch.call(oSelection, "Range");
		Dispatch oTable = Tx2xDispatch.call(oSelection, "Document.Tables.Add", oRange, 1, 2).toDispatch();

		// 左端からのインデント
		Tx2xDispatch.put(oTable, "Rows.LeftIndent", dLeftIndent);

		// アイコン
		Dispatch oCell = Dispatch.call(oTable, "Cell", 1, 1).getDispatch();
		setCellWidth(oCell, 15 * MM);

		Dispatch.call(oCell, "Select");
		Dispatch.put(oSelection, "Style", "本文");
		String sNoteFilename = Tx2xOptions.getInstance().getString("tx2x_folder_name") + "\\note.png";
		try {
			Tx2xDispatch.call(oSelection, "InlineShapes.AddPicture", sNoteFilename, "False", "True");
		} catch (ComFailException e) {
			System.out.println("---------- error ----------\n" + e.getLocalizedMessage() + "ファイル名：" + sNoteFilename
					+ "\n---------------------------");
		}

		// 本文
		oCell = Dispatch.call(oTable, "Cell", 1, 2).getDispatch();
		setCellWidth(oCell, 136.9 * MM - dLeftIndent);

		Dispatch.call(oCell, "Select");
		Dispatch.call(oSelection, "MoveLeft"); // 左へ移動（カーソルを立てる）
	}

	private void setCellWidth(Dispatch oCell, double dWidth) {
		Dispatch.put(oCell, "Width", dWidth);
		// if (Tx2xOptions.getInstance().getBoolean("Visible") == true) {
		// // Wordの更新を待つ
		// try {
		// Thread.sleep(2000);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// }
	}
}
