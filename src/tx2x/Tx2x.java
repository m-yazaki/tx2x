/**
 * IDTaggedTextGenerator�̃��C��UI
 */
package tx2x;

public class Tx2x {
	private static final String CR = "\r";
	private static final String CRLF = "\r\n";
	private static String m_sWarn = "";

	/**
	 * �e�L�X�g�t�@�C�����̉��s�R�[�h
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
	 * ���b�Z�[�W�̏ꍇ�̉��s�R�[�h
	 */
	public static String getCRLF() {
		return CRLF;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*
		 * �R�}���h���C�������̏���
		 */
		for (int i = 0; i < args.length; i++) {
			if ("-debug".equals(args[i])) {
				Tx2xOptions.getInstance().setOption("debug", true);
			} else {
				Tx2xOptions.getInstance().setOption("tx2x_filename", args[i]);
			}
		}

		/*
		 *
		 */
		Tx2xTextReader cTx2xTextReader = new Tx2xTextReader();
		cTx2xTextReader.convertToInDesign(Tx2xOptions.getInstance().getString(
				"tx2x_filename"));

		// ���b�Z�[�W�o��
		String message = "-���`�I��-" + Tx2x.getCRLF();
		String warn = Tx2x.getWarn();
		if (warn.length() > 0) {
			message += warn;
		}
		System.out.println(message);
		Tx2x.initialize();
	}

	/**
	 * ���[�U�[�Ɍ����郁�b�Z�[�W���Ǘ�����
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
			return m_sWarn + "�i�x�����������ߏȗ����܂����j";
	}

	public static void initialize() {
		m_sWarn = "";
	}
}
