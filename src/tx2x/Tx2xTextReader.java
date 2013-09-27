/**
 * tx2x�`���̃e�L�X�g��InDesign�̃^�O�t���e�L�X�g�ɕϊ�����@�\��UI����
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

		// indd�t�@�C���̃R�s�[
		File cFile = new File(sTx2xFilename);
		if (cFile.exists()) {
			try {
				Tx2xTextReader.copyFile(
						"Tx2xTemplate.indesign.indd",
						Tx2xTextReader
								.removeFileExtension(sTx2xFilename) + ".indd");
			} catch (IOException e2) {
				// TODO �����������ꂽ catch �u���b�N
				e2.printStackTrace();
			}
		}

		try {
			if (Tx2xOptions.getInstance().getString("InDesign_OS")
					.equals("Windows")) {
				System.out.println("==========Windows�p�e�L�X�g���o�͂��܂�==========");
				IntermediateTextTreeBuilder formatterForWindows = new IntermediateTextTreeBuilder(
						false, bDebugMode);
				formatterForWindows.parse_file(sTx2xFilename, sMaker);
			} else if (Tx2xOptions.getInstance().getString("InDesign_OS")
					.equals("Macintosh")) {
				System.out.println("==========Macintosh�p�e�L�X�g���o�͂��܂�==========");
				IntermediateTextTreeBuilder formatterForMac = new IntermediateTextTreeBuilder(
						true, bDebugMode);
				formatterForMac.parse_file(sTx2xFilename, sMaker);
			}
		} catch (IOException e1) {
			// TODO �����������ꂽ catch �u���b�N
			e1.printStackTrace();
		}
	}

	/**
	 * �t�@�C���R�s�[ src:�R�s�[�� dest:�R�s�[��
	 *
	 * @throws IOException
	 */
	public static void copyFile(String src, String dest) throws IOException {
		if (src.equals(dest))
			return; // �R�s�[���ƃR�s�[�悪�����Ȃ牽�����Ȃ�

		// �R�s�[�����擾
		File srcFile = new File(src);
		FileInputStream srcStream;
		try {
			srcStream = new FileInputStream(srcFile);
		} catch (FileNotFoundException e) {
			Tx2x.appendWarn("[" + srcFile.getAbsolutePath() + "]��������܂���B");
			return; // �������Ȃ��ŋA��
		}
		FileChannel srcChannel = srcStream.getChannel();

		// �R�s�[����쐬
		File destFile = new File(dest).getAbsoluteFile();
		if (!destFile.getParentFile().exists())
			destFile.getParentFile().mkdirs();
		destFile.createNewFile();
		FileOutputStream destStream = new FileOutputStream(destFile);
		FileChannel destChannel = destStream.getChannel();

		// �R�s�[�I
		long limit = srcChannel.size();
		srcChannel.transferTo(0, limit, destChannel);

		// �㏈��
		destChannel.close();
		destStream.close();
		srcChannel.close();
		srcStream.close();

		destFile.setLastModified(srcFile.lastModified());
		return;
	}

	// �g���q���g��
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
