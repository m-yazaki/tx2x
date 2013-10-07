package tx2x;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/*
 * テキスト変換機能
 */
public abstract class Converter {

	/*
	 * cTargetFileを正しく変換する処理を実装してください。
	 */
	abstract public void convert(File cTargetFile);

	/*
	 * ファイルまたはディレクトリを受け取り、ファイルかディレクトリかを判別して、適切な処理を行う
	 */
	void parse_filesystem(File cTargetFile) throws IOException {
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
			Tx2x.appendWarn("copyFile@IntermediateTextTreeBuilder:["
					+ srcFile.getAbsolutePath() + "]が見つかりません。");
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
