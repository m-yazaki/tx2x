/**
 * InDesignタグ付きテキストを書き出すときに使うFileWriter
 * 微妙な調整を引き受けている
 */
package tx2x.indesign;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import tx2x.Tx2x;
import tx2x.Tx2xOptions;

public class InDesignTT_FileWriter {
	OutputStreamWriter m_fwInDesign;
	boolean m_bCRLFBuffer; // write()に渡されたbCRLFを保持する。次のwrite()呼びだし時に改行コード（CRLFなど）を書き込む

	public InDesignTT_FileWriter(File inDesign) throws IOException {
		// m_fwInDesign = new DataOutputStream(new FileOutputStream(inDesign));
		m_fwInDesign = new OutputStreamWriter(new FileOutputStream(inDesign),
				"UnicodeLittleUnmarked");
		m_bCRLFBuffer = false;
	}

	public void close(boolean bMac) throws IOException {
		if (m_bCRLFBuffer)
			m_fwInDesign.write(Tx2x.getTaggedTextCRLF(bMac).toCharArray());

		m_fwInDesign.close();
	}

	/**
	 * FileWriter.write()に、置換機能を追加する
	 * 
	 * @param string
	 *            書き込む文字列
	 * @param bCRLF
	 *            改行文字が必要かどうか。改行文字は、次のwrite()またはclose()を呼び出したときに出力されます。
	 * @param bMac
	 *            Mac用のテキストにする場合はtrue
	 * @throws IOException
	 */
	public void write(String string, boolean bCRLF, boolean bMac)
			throws IOException {

		/* CellEndの次の改行は省略 */
		if (string.indexOf("<CellEnd:>") == 0) {
			m_bCRLFBuffer = false;
		}

		/* 改行を追加 */
		if (m_bCRLFBuffer)
			m_fwInDesign.write(Tx2x.getTaggedTextCRLF(bMac).toCharArray());

		/* セルのコントロールコードを削除 */
		string = string.replaceAll("【黒[0-9]+%】", "");

		string = string.replaceAll("<ParaStyle:table-body[0-9]+>【[上下左右]と結合】",
				"");
		string = string.replaceAll("<ParaStyle:memo[0-9]+>【[上下左右]と結合】", "");
		string = string.replaceAll("<ParaStyle:table-body[0-9]+>【[左右]上から斜線】",
				"");

		string = string.replaceAll("【上下センター】", "");

		/* コントロールコードを置換 */
		string = string.replaceAll("【ここまでインデント】", "");

		if (bMac) {
			/*
			 * DTP用（旧Mac用）のタグ付きテキストでの特別処理 ・\マークを\\の形式に（エスケープ）する必要がある
			 */
			if (!Tx2xOptions.getInstance().getBoolean("InDesignCS5")) {
				while (true) {
					int n = string.indexOf("\\");
					if (n == -1) {
						m_fwInDesign.write(string.toCharArray());
						break;
					}
					// 通常文字列として「\」まで書き込む
					m_fwInDesign.write(string.substring(0, n).toCharArray());
					string = string.substring(n);

					// \の処理
					if (string.substring(1).indexOf("\\") == 0) {
						// 次も\なら
						m_fwInDesign.write('\\');
						string = string.substring(2);
					} else {
						m_fwInDesign.write(0x80);
						string = string.substring(1);
					}
				}
			} else {
				m_fwInDesign.write(string.toCharArray());
			}
		} else {
			/* Windows用のテキストにする場合の特別処理 */
			m_fwInDesign.write(string.toCharArray());
		}

		m_bCRLFBuffer = bCRLF;
	}
}
