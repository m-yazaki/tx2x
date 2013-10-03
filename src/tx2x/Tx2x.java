/**
 * IDTaggedTextGeneratorのメインUI
 */
package tx2x;

public class Tx2x {
	private static final String CR = "\r";
	private static final String CRLF = "\r\n";
	private static String m_sWarn = "";

	/**
	 * テキストファイル中の改行コード
	 *
	 * @param bMac
	 * @return
	 */
	public static String getCRLF(boolean bMac) {
		if (bMac) {
			return CR;
		} else {
			return CRLF;
		}
	}

	/**
	 * メッセージの場合の改行コード
	 */
	public static String getCRLF() {
		return CRLF;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*
		 * コマンドライン引数の処理
		 */
		for (int i = 0; i < args.length; i++) {
			if ("-debug".equals(args[i])) {
				Tx2xOptions.getInstance().setOption("debug", true);
			} else {
				Tx2xOptions.getInstance().setOption("tx2x_folder_file_name",
						args[i]);
			}
		}

		/*
		 *
		 */
		Tx2xTextReader cTx2xTextReader = new Tx2xTextReader();
		cTx2xTextReader.convertToInDesign(Tx2xOptions.getInstance().getString(
				"tx2x_folder_file_name"));

		// メッセージ出力
		String message = "-整形終了-" + Tx2x.getCRLF();
		String warn = Tx2x.getWarn();
		if (warn.length() > 0) {
			message += warn;
		}
		System.out.println(message);
		Tx2x.initialize();
	}

	/**
	 * ユーザーに見せるメッセージを管理する
	 *
	 * @param string
	 */
	public static void appendWarn(String string) {
		if (m_sWarn.length() < 2048)
			m_sWarn += string + CRLF;
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
