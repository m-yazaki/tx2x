/**
 * InDesign�^�O�t���e�L�X�g�������o���Ƃ��Ɏg��FileWriter
 * �����Ȓ����������󂯂Ă���
 */
package tx2x;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class IDTTG_FileWriter {
	FileOutputStream m_fwInDesign;
	boolean m_bCRLFBuffer; // write()�ɓn���ꂽbCRLF��ێ�����B����write()�Ăт������ɉ��s�R�[�h�iCRLF�Ȃǁj����������

	public IDTTG_FileWriter(File inDesign) throws IOException {
		// m_fwInDesign = new DataOutputStream(new FileOutputStream(inDesign));
		m_fwInDesign = new FileOutputStream(inDesign);
		m_bCRLFBuffer = false;
	}

	public void close(boolean bMac) throws IOException {
		if (m_bCRLFBuffer)
			m_fwInDesign.write(Tx2x.getCRLF(bMac).getBytes());

		m_fwInDesign.close();
	}

	/**
	 * FileWriter.write()�ɁA�u���@�\��ǉ�����
	 *
	 * @param string
	 *            �������ޕ�����
	 * @param bCRLF
	 *            ���s�������K�v���ǂ����B���s�����́A����write()�܂���close()���Ăяo�����Ƃ��ɏo�͂���܂��B
	 * @param bMac
	 *            Mac�p�̃e�L�X�g�ɂ���ꍇ��true
	 * @throws IOException
	 */
	public void write(String string, boolean bCRLF, boolean bMac)
			throws IOException {

		/* CellEnd�̎��̉��s�͏ȗ� */
		if (string.indexOf("<CellEnd:>") == 0) {
			m_bCRLFBuffer = false;
		}

		/* ���s��ǉ� */
		if (m_bCRLFBuffer)
			m_fwInDesign.write(Tx2x.getCRLF(bMac).getBytes());

		/* �Z���̃R���g���[���R�[�h���폜 */
		string = string.replaceAll("�y��[0-9]+%�z", "");

		string = string.replaceAll("<ParaStyle:table-body[0-9]+>�y[�㉺���E]�ƌ����z",
				"");
		string = string.replaceAll("<ParaStyle:memo[0-9]+>�y[�㉺���E]�ƌ����z", "");
		string = string.replaceAll("<ParaStyle:table-body[0-9]+>�y[���E]�ォ��ΐ��z",
				"");

		string = string.replaceAll("�y�㉺�Z���^�[�z", "");

		/* �R���g���[���R�[�h��u�� */
		string = string.replaceAll("�y�����܂ŃC���f���g�z", "");

		if (bMac) {
			/* DTP�p�i��Mac�p�j�̃e�L�X�g�ɂ���ꍇ�̓��ʏ��� */
			if (!Tx2xOptions.getInstance().getBoolean("InDesignCS5")) {
				while (true) {
					int n = string.indexOf("\\");
					if (n == -1) {
						m_fwInDesign.write(string.getBytes());
						break;
					}
					// System.out.println("-----");
					// System.out.println("string: " + string);
					// System.out.println("n: " + n);
					// \�܂ŏ������ށi�ʏ핶����j
					m_fwInDesign.write(string.substring(0, n).getBytes());
					string = string.substring(n);

					// \�̏���
					if (string.substring(1).indexOf("\\") == 0) {
						// ����\�Ȃ�
						m_fwInDesign.write('\\');
						string = string.substring(2);
					} else {
						m_fwInDesign.write(0x80);
						string = string.substring(1);
					}
				}
			} else {
				m_fwInDesign.write(string.getBytes());
			}
		} else {
			/* Windows�p�̃e�L�X�g�ɂ���ꍇ�̓��ʏ��� */
			m_fwInDesign.write(string.getBytes());
		}

		m_bCRLFBuffer = bCRLF;
	}
}
