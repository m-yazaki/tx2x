/**
 * tx2x形式のテキストをInDesignのタグ付きテキストに変換する機能のUI部分
 */
package tx2x;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class Tx2xTextReader {

	public Tx2xTextReader() {
	}

	public void convertToInDesign(String sTx2xFilename) {
		String sMaker = Tx2xOptions.getInstance().getString("maker");
		boolean bDebugMode = Tx2xOptions.getInstance().getBoolean("debug");

		Tx2x.initialize();

		// inddファイルのコピー
		File cFile = new File(sTx2xFilename);
		if (cFile.exists()) {
			try {
				Tx2xTextReader.copyFile("Tx2xTemplate.indesign.indd",
						Tx2xTextReader.removeFileExtension(sTx2xFilename)
								+ ".indd");
			} catch (IOException e2) {
				// TODO 自動生成された catch ブロック
				e2.printStackTrace();
			}
		}

		try {
			File file = new File(sTx2xFilename);
			String sInDesignOS = Tx2xOptions.getInstance().getString(
					"InDesign_OS");
			if (sInDesignOS.equals("Windows")) {
				if (!bDebugMode) {
					System.out
							.println("==========Windows用テキストを出力します==========");
				} else {
					System.out
							.println("==========Windows用テキストを出力します（DEBUG）==========");
				}
				IgnoreFile cIgnoreFile = IgnoreFile.getInstance();
				if (file.isDirectory()) {
					cIgnoreFile.setIgnoreFiles(new File(file.getAbsolutePath()
							+ File.separator + "tx2x.ignore"));
				}
				parse_filesystem(file, sMaker, bDebugMode);
			} else if (sInDesignOS.equals("Macintosh")) {
				System.out.println("==========Macintosh用テキストを出力します==========");
				IntermediateTextTreeBuilder formatterForMac = new IntermediateTextTreeBuilder(
						true, bDebugMode);
				formatterForMac.parse_file(file, sMaker);
			}
		} catch (IOException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}
	}

	/*
	 * ファイルまたはディレクトリを受け取り、ファイルかディレクトリかを判別して、適切な処理を行う
	 */
	private void parse_filesystem(File file, String sMaker, boolean bDebugMode)
			throws IOException {
		// tx2x.ignoreの処理
		IgnoreFile cIgnoreFile = IgnoreFile.getInstance();
		if (cIgnoreFile.isIgnore(file))
			return;
		if (file.isFile()) {
			// ファイルでした。
			IntermediateTextTreeBuilder formatterForWindows = new IntermediateTextTreeBuilder(
					false, bDebugMode);
			formatterForWindows.parse_file(file, sMaker);
		} else if (file.isDirectory()) {
			// ディレクトリでした。
			File[] cFiles = file.listFiles();
			for (int i = 0; i < cFiles.length; i++) {
				parse_filesystem(cFiles[i], sMaker, bDebugMode);
			}
		}
	}

	/**
	 * ファイルコピー src:コピー元 dest:コピー先
	 *
	 * @throws IOException
	 */
	public static void copyFile(String src, String dest) throws IOException {
		if (src.equals(dest))
			return; // コピー元とコピー先が同じなら何もしない

		// コピー元を取得
		File srcFile = new File(src);
		FileInputStream srcStream;
		try {
			srcStream = new FileInputStream(srcFile);
		} catch (FileNotFoundException e) {
			Tx2x.appendWarn("[" + srcFile.getAbsolutePath() + "]が見つかりません。");
			return; // 何もしないで帰る
		}
		FileChannel srcChannel = srcStream.getChannel();

		// コピー先を作成
		File destFile = new File(dest).getAbsoluteFile();
		if (!destFile.getParentFile().exists())
			destFile.getParentFile().mkdirs();
		destFile.createNewFile();
		FileOutputStream destStream = new FileOutputStream(destFile);
		FileChannel destChannel = destStream.getChannel();

		// コピー！
		long limit = srcChannel.size();
		srcChannel.transferTo(0, limit, destChannel);

		// 後処理
		destChannel.close();
		destStream.close();
		srcChannel.close();
		srcStream.close();

		destFile.setLastModified(srcFile.lastModified());
		return;
	}

	// 拡張子をトル
	public static String removeFileExtension(String filename) {
		int lastDotPos = filename.lastIndexOf('.');

		if (lastDotPos == -1) {
			return filename;
		} else if (lastDotPos == 0) {
			return filename;
		} else {
			return filename.substring(0, lastDotPos);
		}
	}
}
