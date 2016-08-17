/**
 * IDTaggedTextGeneratorのメインUI
 */
package tx2x;

public class Tx2x {
	private static String m_sWarn = "";

	/**
	 * テキストファイル中の改行コード
	 *
	 * @param bMac
	 * @return
	 */
	public static String getTaggedTextCRLF(boolean bMac) {
		if (bMac) {
			return "\r";
		} else {
			return "\r\n";
		}
	}

	/**
	 * メッセージの場合の改行コード
	 */
	public static String getMessageCRLF() {
		return System.getProperty("line.separator");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Tx2xGUI frame = new Tx2xGUI();
		frame.setVisible(true);
	}

	/**
	 * ユーザーに見せるメッセージを管理する
	 *
	 * @param string
	 */
	public static void appendWarn(String string) {
		if (m_sWarn.length() < 2048)
			m_sWarn += string + getMessageCRLF();
	}

	public static String getWarn() {
		if (m_sWarn.length() < 2048)
			return m_sWarn;
		else
			return m_sWarn + "（警告が多いため省略しました）";
	}

	public static void initialize() {
		m_sWarn = "";
	}
}
