package tx2x;

import java.io.File;
import java.io.IOException;

/*
 * テキスト変換機能
 */
public abstract class Converter {
	/**
	 * デバッグモードのON/OFF
	 */
	protected boolean m_bDebugMode;

	protected Converter(boolean bDebugMode) {
		m_bDebugMode = bDebugMode;
	}

	/**
	 * cTargetFileを正しく変換する処理を実装してください（オーバーライドポイント）
	 *
	 * @param cTargetFile
	 *            変換元ファイル
	 */
	public void convert(File cTargetFile) {
		return;
	}

	/**
	 * 全ファイルをconvertする前に実行する処理を実装してください（オーバーライドポイント）
	 *
	 * @param cFile
	 *            変換対象ファイル／ディレクトリ（ユーザーが指定したファイル／フォルダ）
	 */
	public void setup(File cFile) {
		return;
	}

	/**
	 * 全ファイルをconvertした後に実行する処理を実装してください（オーバーライドポイント）
	 *
	 * @param cFile
	 *            変換対象ファイル／ディレクトリ（ユーザーが指定したファイル／フォルダ）
	 */
	public void tearDown(File cFile) {
		return;
	}

	/*
	 * ファイルまたはディレクトリを受け取り、ファイルかディレクトリかを判別して、適切な処理を行う
	 */
	public void parse_filesystem(File cTargetFile) throws IOException {
		// tx2x.ignoreの処理
		IgnoreFile cIgnoreFile = IgnoreFile.getInstance();
		if (cIgnoreFile.isIgnore(cTargetFile))
			return;
		if (cTargetFile.isFile()) {
			// ファイルでした。
			convert(cTargetFile);
		} else if (cTargetFile.isDirectory()) {
			// ディレクトリでした。
			File[] cFiles = cTargetFile.listFiles();
			for (int i = 0; i < cFiles.length; i++) {
				parse_filesystem(cFiles[i]);
			}
		}
	}
}
