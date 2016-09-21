/**
 * ePubを書き出すときに使うFileWriter
 * 微妙な調整を担当する
 */
package tx2x.xhtml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class XHTML_FileWriter {
	OutputStreamWriter m_fwOutput;
	boolean m_bCRLFBuffer; // write()に渡されたbCRLFを保持する。次のwrite()呼びだし時に改行コード（CRLFなど）を書き込む

	public XHTML_FileWriter(File cXHTML) throws IOException {
		m_fwOutput = new OutputStreamWriter(new FileOutputStream(cXHTML), "UTF-8");
		m_bCRLFBuffer = false;
	}

	public void close(boolean bMac) throws IOException {
		if (m_bCRLFBuffer)
			m_fwOutput.write(Tx2xXHTML.getXHTML_CRLF());

		m_fwOutput.close();
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
	public void write(String string, boolean bCRLF, boolean bMac) throws IOException {
		if (string == null)
			return;

		/* 改行を追加 */
		if (m_bCRLFBuffer)
			m_fwOutput.write(Tx2xXHTML.getXHTML_CRLF());

		/* コントロールコードを削除（ここから） */
		string = string.replaceAll("【黒[0-9]+%】", "");
		string = string.replaceAll("【上下センター】", "");
		/* コントロールコードを削除（ここまで） */

		if (bMac) {
			/* Mac用のテキストにする場合の特別処理 */
			while (true) {
				int n = string.indexOf("\\");
				if (n == -1) {
					m_fwOutput.write(string);
					break;
				}
				// \まで書き込む（通常文字列）
				m_fwOutput.write(string.substring(0, n));
				string = string.substring(n);

				// \の処理
				if (string.substring(1).indexOf("\\") == 0) {
					// 次も\なら
					m_fwOutput.write('\\');
					string = string.substring(2);
				} else {
					m_fwOutput.write(0x80);
					string = string.substring(1);
				}
			}
		} else {
			/* Windows用のテキストにする場合の特別処理 */
			m_fwOutput.write(string);
		}

		m_bCRLFBuffer = bCRLF;
	}

	public void clearCRLFBuffer() {
		m_bCRLFBuffer = false;
	}
}
