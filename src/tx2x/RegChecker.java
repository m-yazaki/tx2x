package tx2x;

/**
 * 以下のフォーマットのテキストファイルを読み込みエラーメッセージを表示する仕組みを提供する
 * 誤りを表す正規表現	JustRightでの表現				理由	コメント	正しい表現
-----
コンピュータ(?!ー)
サーフェイス
データー
テクスチャーマッピング
パラメータ(?!ー)
フォルダ(?!ー)
プロパティー
ユーザ座標系（UCS）
ユーザ(?!ー)
レーザスキャニング
HASP(?!（ハスプ）)
(?<!（)ハスプ(?!）)
参照
ご覧
プラオリティ
3Dview
(?<!を|右|ダブル|押しながら)クリック
］－［
[\[\]]
-----
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegChecker {
	static String line_separator = System.getProperty("line.separator");
	private static RegChecker instance = new RegChecker();
	private ArrayList<CheckWordInfo> m_sCheckWordFilelistNG;
	private ArrayList<CheckWordInfo> m_sCheckWordFilelistOK;

	private class CheckWordInfo {
		private ArrayList<String> m_sText;
		public static final int REGEX = 0;
		public static final int JUSTRIGHT = 1;
		public static final int BECAUSE = 5;
		public static final int COMMENT = 6;
		public static final int CORRECTION = 7;

		CheckWordInfo(String line) {
			m_sText = new ArrayList<String>();
			String[] ws = line.split("\t");
			for (int i = 0; i < ws.length; i++) {
				m_sText.add(ws[i]);
			}
		}

		public String getRegex() {
			if (m_sText.size() > REGEX)
				return m_sText.get(REGEX);
			return "";
		}

		public String getComment() {
			if (m_sText.size() > COMMENT)
				return m_sText.get(COMMENT);
			return "";
		}

		public String getMessage() {
			String ret = "";
			if (m_sText.size() > BECAUSE && m_sText.get(BECAUSE).length() > 0)
				ret = ret + "指摘理由：" + m_sText.get(BECAUSE) + line_separator;
			if (m_sText.size() > JUSTRIGHT && m_sText.get(JUSTRIGHT).length() > 0)
				ret = ret + "シンプル表現（JustRight表現）：" + m_sText.get(JUSTRIGHT) + line_separator;
			if (m_sText.size() > REGEX && m_sText.get(REGEX).length() > 0)
				ret = ret + "正規表現：" + m_sText.get(REGEX) + line_separator;
			if (m_sText.size() > COMMENT && m_sText.get(COMMENT).length() > 0)
				ret = ret + "補足　　：" + m_sText.get(COMMENT) + line_separator;
			for (int i = 0; i < 5; i++) {
				if (m_sText.size() > CORRECTION + i && m_sText.get(CORRECTION + i).length() > 0)
					ret = ret + "訂正候補：" + m_sText.get(CORRECTION + i) + line_separator;
			}
			return ret;
		}
	}

	private RegChecker() {
		m_sCheckWordFilelistNG = new ArrayList<CheckWordInfo>();
		m_sCheckWordFilelistOK = new ArrayList<CheckWordInfo>();
	}

	public static RegChecker getInstance() {
		return instance;
	}

	public void setCheckWordFile(File cTx2xCheckWord) {
		m_sCheckWordFilelistNG.clear();
		m_sCheckWordFilelistOK.clear();
		try {
			// 入力ファイル
			BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(cTx2xCheckWord), "UTF-8"));

			String line;
			boolean bOK = false;
			boolean bNG = true;
			while ((line = bf.readLine()) != null) {
				if (line.matches("!! OK words\t+")) {
					bOK = true;
					bNG = false;
				} else if (line.matches("!! NG words\t+")) {
					bOK = false;
					bNG = true;
				}
				if (line.indexOf("!!") == 0)
					continue;

				CheckWordInfo e = new CheckWordInfo(line);
				if (e.getRegex().equals(""))
					continue;

				if (bOK) {
					m_sCheckWordFilelistOK.add(e);
				}
				if (bNG) {
					m_sCheckWordFilelistNG.add(e);
				}
			}

			bf.close();
		} catch (FileNotFoundException e1) {
			Tx2x.appendWarn("ファイルが見つかりません（問題ありません）@IgnoreFile：" + cTx2xCheckWord.getAbsolutePath());
			Tx2x.appendWarn(e1.getLocalizedMessage());
			return;
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	public void check(File cTargetFile) throws IOException {
		int errorCount = 0;
		String sErrorMessage = "■変換対象ファイルの用語チェック...";

		/*
		 * Tx2x形式のテキストファイルをバッファ（ArrayList<String> allText）に読み込む
		 */
		// 入力ファイル
		BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(cTargetFile), "UTF-8"));

		/*
		 * BOM対応
		 */
		ArrayList<String> allText = new ArrayList<String>();
		{
			String line = bf.readLine();
			if (line != null && line.length() > 0 && line.codePointAt(0) == 65279) { // BOM
				line = line.substring(1);
				sErrorMessage = sErrorMessage + line_separator + "BOM付きのUTF-8データになっています。BOM無しにすることを推奨します。";
			}

			// バッファに取り込む
			allText.add(line);
			while ((line = bf.readLine()) != null) {
				allText.add(line);
			}

			bf.close();
		}

		Iterator<String> itLine = allText.iterator();
		while (itLine.hasNext()) {
			String original_line = itLine.next();
			errorCount += validate(original_line);
		}
		if (errorCount > 0) {
			System.out.println(sErrorMessage);
			System.out.println("done.");
		}
	}

	public int validate(String original_line) {
		int errorCount = 0;
		String temp_line = original_line;
		temp_line = temp_line.replaceAll("<ui( recommend=\".+?\")?>.+?</ui>", ""); // UIはチェック対象外とする
		temp_line = temp_line.replaceAll("<img src=\"[^\"]+\" (alt=\"[^\"]+\" )?/>", ""); // imgタグはチェック対象外とする
		// OKループ
		Iterator<CheckWordInfo> itOK = m_sCheckWordFilelistOK.iterator();
		while (itOK.hasNext()) {
			String regex = itOK.next().getRegex();
			temp_line = temp_line.replaceAll(regex, "");
		}
		// NGループ
		Iterator<CheckWordInfo> itNG = m_sCheckWordFilelistNG.iterator();
		while (itNG.hasNext()) {
			CheckWordInfo cInfo = itNG.next();
			String regex = cInfo.getRegex();
			Pattern p = Pattern.compile(".*" + regex + ".*");
			Matcher m = p.matcher(temp_line);
			if (m.matches()) {
				errorCount++;
				System.out.println("----- line -----");
				System.out.println(original_line);
				System.out.println("----------------");
				System.out.println(cInfo.getMessage());
			}
		}
		return errorCount;
	}
}
