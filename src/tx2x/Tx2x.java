/**
 * IDTaggedTextGeneratorのメインUI
 */
package tx2x;

import java.io.File;

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
		/*
		 * コマンドライン引数の処理
		 */
		for (int i = 0; i < args.length; i++) {
			String sLowerCaseOption = args[i].toLowerCase();
			if ("-debug".equals(sLowerCaseOption)) {
				Tx2xOptions.getInstance().setOption("debug", true);
			} else if ("-word".equals(sLowerCaseOption)) {
				Tx2xOptions.getInstance().setOption("mode", "Word");
			} else {
				File temp = new File(args[i]);
				if (temp.exists()) {
					Tx2xOptions.getInstance().setOption(
							"tx2x_folder_file_name", args[i]);
				}
			}
		}

		if (Tx2xOptions.getInstance().getString("mode").equals("Word")) {
			/*
			 * InDesignタグ付きテキスト
			 */
			Tx2xTextReader cTx2xTextReader = new Tx2xTextReader();
			cTx2xTextReader.convertToWord(Tx2xOptions.getInstance().getString(
					"tx2x_folder_file_name"));
		} else {
			/*
			 * InDesignタグ付きテキスト
			 */
			Tx2xTextReader cTx2xTextReader = new Tx2xTextReader();
			cTx2xTextReader.convertToInDesign(Tx2xOptions.getInstance()
					.getString("tx2x_folder_file_name"));
		}

		// メッセージ出力
		String message = "-整形終了-" + Tx2x.getMessageCRLF();
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
