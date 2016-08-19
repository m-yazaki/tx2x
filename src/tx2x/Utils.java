package tx2x;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {
	/**
	 * ファイルコピー src:コピー元 dest:コピー先
	 *
	 * @throws IOException
	 */
	public static void copyFile(File srcFile, File destFile) throws IOException {
		if (srcFile.getAbsolutePath().equals(destFile.getAbsolutePath()))
			return; // コピー元とコピー先が同じなら何もしない

		// コピー元を取得
		FileInputStream srcStream;
		srcStream = new FileInputStream(srcFile);
		FileChannel srcChannel = srcStream.getChannel();

		// コピー先を作成
		if (!destFile.getAbsoluteFile().getParentFile().exists())
			destFile.getAbsoluteFile().getParentFile().mkdirs();
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

	public static void copyFile(String src, String dest) throws IOException {
		File srcFile = new File(src);
		File destFile = new File(dest);
		copyFile(srcFile, destFile);
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

	public static String getFileExtension(String filename) {
		int lastDotPos = filename.lastIndexOf('.');

		if (lastDotPos == -1) {
			return "";
		} else if (lastDotPos == 0) {
			return "";
		} else {
			return filename.substring(lastDotPos + 1);
		}
	}

	/**
	 * MD5計算メソッド
	 */
	public static String caluculateMD5(String string) {
		// ダイジェストの計算
		DigestInputStream inStream;
		try {
			inStream = new DigestInputStream(new ByteArrayInputStream(string.getBytes()),
					MessageDigest.getInstance("MD5"));
			byte[] buf = new byte[1024];
			for (;;) {
				if (inStream.read(buf) <= 0)
					break;
			}
			inStream.close();

			MessageDigest md5 = inStream.getMessageDigest();
			return MessageDigestToString(md5.digest());
		} catch (NoSuchAlgorithmException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

		return "";
	}

	/**
	 * byte[] digest を String に変換するメソッド
	 */
	private static String MessageDigestToString(byte[] digest) {
		String result = "";
		for (int loop = 0; loop < digest.length; loop++) {
			int n = digest[loop] & 0xFF;
			if (n <= 0xF) {
				result += "0";
			}
			result += Integer.toHexString(n);
		}
		return result;
	}

	/**
	 * 全角数字を半角数字に変換するメソッド
	 *
	 * @param s
	 *            全角数字
	 * @return 半角数字
	 */
	public static String zenkakuNumberToHankakuNumber(String s) {
		StringBuffer sb = new StringBuffer(s);
		for (int i = 0; i < sb.length(); i++) {
			char c = sb.charAt(i);
			if (c >= '０' && c <= '９') {
				sb.setCharAt(i, (char) (c - '０' + '0'));
			}
		}
		return sb.toString();
	}
}
