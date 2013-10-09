/**
 * IDTaggedTextGeneratorのメインUI
 */
package tx2x;

import java.io.File;
import java.io.IOException;

import tx2x.indesign.ConvertToInDesign;
import tx2x.word.ConvertToWord;

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
			} else if ("-indesign-mac".equals(sLowerCaseOption)) {
				Tx2xOptions.getInstance().setOption("mode",
						"InDesign-Macintosh");
			} else {
				File temp = new File(args[i]);
				if (temp.exists()) {
					Tx2xOptions.getInstance().setOption(
							"tx2x_folder_file_name", args[i]);
				}
			}
		}

		initialize();

		Converter cConverter;
		if (Tx2xOptions.getInstance().getString("mode").equals("Word")) {
			cConverter = new ConvertToWord();
		} else {
			cConverter = new ConvertToInDesign();
		}
		File cFile = new File(Tx2xOptions.getInstance().getString(
				"tx2x_folder_file_name"));
		if (cFile.exists()) {
			IgnoreFile cIgnoreFile = IgnoreFile.getInstance();
			if (cFile.isDirectory()) {
				Tx2xOptions.getInstance().setOption("tx2x_folder_name",
						cFile.getAbsolutePath());
				cIgnoreFile.setIgnoreFiles(new File(cFile.getAbsolutePath()
						+ File.separator + "tx2x.ignore"));
			}
			try {
				cConverter.parse_filesystem(cFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// メッセージ出力
		String message = "-整形終了-" + Tx2x.getMessageCRLF();
		String warn = Tx2x.getWarn();
		if (warn.length() > 0) {
			message += warn;
		}
		System.out.println(message);
		initialize();
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
