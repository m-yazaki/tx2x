/**
 * LongStyle�i�y���m�点�z�y�ӏ������z�y�ӏ������z�y�{���z�y�{���z�j�����ɁAInDesign�^�O�t���e�L�X�g�𐶐�����
 * ���łɓ��ꕶ���̒u�����s���Ă��܂��B
 */
package tx2x;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tx2x_core.IntermediateText;
import tx2x_core.Style;

public class LongStyleManager {
	private static final String KOKOMADE_INDENT_CHAR = String.valueOf((char) 7); // �����܂ŃC���f���g����
	LinkedList<Style> m_StyleLinkedList; // �X�^�C������push/pop����
	String m_sPrevLongStyle; // ���O�̒����X�^�C����
	private boolean m_bMac;

	// �ʎ��ԍ�����������Ƃ��Ƀy�[�W��؂�ւ��邽�߂̕ϐ�
	private int m_nPrevPageNum;
	private Hashtable<String, Integer> m_cPrefixTable;

	// ����`�̃X�^�C���idummy000�j���Ǘ����邽�߂̕ϐ�
	private int m_nDummyCounter;
	private Hashtable<String, String> m_cDummyStyleHashTable;

	/**
	 * �菇�\�g�݂𐧌䂷�邽�߂̕ϐ�
	 */
	private int m_nStepTableWidth = 0;

	// type = 0x1: ��ʂ���
	// type = 0x2: �菇��������ɕ\����
	int m_nStepTableType = 0;

	// type = 0x1�̂Ƃ��́A��ʃL���v�V������ێ�
	// type = 0x2�̂Ƃ��́A�X�e�b�v�ԍ���ێ�
	String m_sStepCaption = "";

	// �X�e�b�v�ԍ���ێ�
	String m_sStepNumber = "";

	int m_nPrevStepTableWidth = 0;

	// type = 0x1: ��ʂ���
	// type = 0x2: �菇��������ɕ\����
	int m_nPrevStepTableType = 0;

	String m_sPrevStepCaption = "";

	private ArrayList<String> m_cLongStyleArrayList;
	private String m_sMaker;

	LongStyleManager(String sMaker, boolean bMac) {
		m_StyleLinkedList = new LinkedList<Style>();
		m_sPrevLongStyle = "";
		m_nPrevPageNum = -1;
		m_cPrefixTable = new Hashtable<String, Integer>();
		m_nDummyCounter = 0;
		m_cDummyStyleHashTable = new Hashtable<String, String>();
		m_bMac = bMac;
		m_cLongStyleArrayList = new ArrayList<String>();
		m_sMaker = sMaker;
	}

	public String getInDesignStyle(IntermediateText iText, int nLsIndex)
			throws IOException {
		String longStyle = getLongStyle();
		if (iText.getStyle() == null) {
			longStyle += "�y�{���z";
		}

		// iText�̖{���������B�y��������z�ȊO�̑S�i������
		{
			String text = iText.getEscapeText();
			text = text.replaceAll("\\\\<CharStyle:([^>]*)\\\\>",
					"<CharStyle:$1>");
			// text = text.replaceAll("\\\\<CharStyle:\\\\>", "<CharStyle:>");
			if (m_bMac) {
				String sKeyFontName = "";
				if (m_sMaker.equals("����")) {
					sKeyFontName = "KeyFont";

					// ���ŃX�}�[�g�t�H��
					text = text.replace("�y�z�[���^�b�`�L�[�z", "<CharStyle:"
							+ sKeyFontName + ">A<CharStyle:>");
					text = text.replace("�y�d���{�^���z", "<CharStyle:" + sKeyFontName
							+ ">C<CharStyle:>");
					text = text.replace("�y�d���L�[�z", "<CharStyle:" + sKeyFontName
							+ ">C<CharStyle:>");
					text = text.replace("�y�J�����{�^���z", "<CharStyle:"
							+ sKeyFontName + ">D<CharStyle:>");
					text = text.replace("�y�J�����L�[�z", "<CharStyle:" + sKeyFontName
							+ ">D<CharStyle:>");
					text = text.replace("�y���ʃ{�^����z", "<CharStyle:"
							+ sKeyFontName + ">E<CharStyle:>");
					text = text.replace("�y���ʃ{�^�����z", "<CharStyle:"
							+ sKeyFontName + ">F<CharStyle:>");
					text = text.replace("�y�T�C�h��L�[�z", "<CharStyle:"
							+ sKeyFontName + ">E<CharStyle:>");
					text = text.replace("�y�T�C�h���L�[�z", "<CharStyle:"
							+ sKeyFontName + ">F<CharStyle:>");
					text = text.replace("�y���M�L�[�z", "<CharStyle:" + sKeyFontName
							+ ">G<CharStyle:>");
					text = text.replace("�y�d�b�L�[�z", "<CharStyle:" + sKeyFontName
							+ ">G<CharStyle:>");
					text = text.replace("�y�X�^�[�g�L�[�z", "<CharStyle:"
							+ sKeyFontName + ">H<CharStyle:>");
					text = text.replace("�y�z�[���L�[�z", "<CharStyle:" + sKeyFontName
							+ ">A<CharStyle:>");
					text = text.replace("�y���j���[�L�[�z", "<CharStyle:"
							+ sKeyFontName + ">M<CharStyle:>");
					text = text.replace("�y�o�b�N�L�[�z", "<CharStyle:" + sKeyFontName
							+ ">R<CharStyle:>");
					// if (text.matches(".*�y�z�[���L�[�z.*")) {
					// IDTaggedTextGenerator4KDDI
					// .appendWarn("�y�z�[���L�[�z�͞B���ł��B�y�z�[���^�b�`�L�[�z�܂��́y�z�[���^���[���L�[�z���g�p���Ă��������B�F"
					// + text);
					// }
					// E31T
					text = text.replace("�y���b�N�L�[�z", "<CharStyle:" + sKeyFontName
							+ ">I<CharStyle:>");
					text = text.replace("�y�N���A�^�����L�[�z", "<CharStyle:"
							+ sKeyFontName + ">B<CharStyle:>");
					text = text.replace("�y���[���L�[�z", "<CharStyle:" + sKeyFontName
							+ ">L<CharStyle:>");
					text = text.replace("�y�����L�[�z", "<CharStyle:" + sKeyFontName
							+ ">R<CharStyle:>");
					text = text.replace("�y�㉺���E�L�[�z", "<CharStyle:"
							+ sKeyFontName + ">a<CharStyle:>");
					text = text.replace("�y�㉺�L�[�z", "<CharStyle:" + sKeyFontName
							+ ">j<CharStyle:>");
					text = text.replace("�y���E�L�[�z", "<CharStyle:" + sKeyFontName
							+ ">s<CharStyle:>");
					text = text.replace("�y��L�[�z", "<CharStyle:" + sKeyFontName
							+ ">u<CharStyle:>");
					text = text.replace("�y���L�[�z", "<CharStyle:" + sKeyFontName
							+ ">d<CharStyle:>");
					text = text.replace("�y���L�[�z", "<CharStyle:" + sKeyFontName
							+ ">l<CharStyle:>");
					text = text.replace("�y�E�L�[�z", "<CharStyle:" + sKeyFontName
							+ ">r<CharStyle:>");
					text = text.replace("�y�Z���^�[�L�[�z", "<CharStyle:"
							+ sKeyFontName + ">c<CharStyle:>");
					text = text.replace("�y�A����L�[�z", "<CharStyle:" + sKeyFontName
							+ ">&<CharStyle:>");
					text = text.replace("�y�A�h���X���L�[�z", "<CharStyle:"
							+ sKeyFontName + ">&<CharStyle:>");
					{ // �y0�L�[�z�`�y9�L�[�z
						Pattern pattern = Pattern.compile("�y([0-9])�L�[�z");
						Matcher matcher = pattern.matcher(text);
						while (matcher.find()) {
							text = text.replaceFirst("�y[0-9]�L�[�z", "<CharStyle:"
									+ sKeyFontName + ">" + matcher.group(1)
									+ "<CharStyle:>");
							matcher = pattern.matcher(text);
						}
					}
					text = text.replace("�y���L�[�z", "<CharStyle:" + sKeyFontName
							+ ">#<CharStyle:>");
					text = text.replace("�y���L�[�z", "<CharStyle:" + sKeyFontName
							+ ">*<CharStyle:>");

				} else if (m_sMaker.equals("���Z��")) {
					sKeyFontName = "KeyFont\\_BaseKey001";

					text = text.replace("�y���L�[�z", "<CharStyle:" + sKeyFontName
							+ ">#<CharStyle:>");
					text = text.replace("�y�A�v���L�[�z", "<CharStyle:" + sKeyFontName
							+ ">%<CharStyle:>");
					text = text.replace("�y�A�h���X���L�[�z", "<CharStyle:"
							+ sKeyFontName + ">&<CharStyle:>");
					text = text.replace("�y�}�i�[�L�[�z", "<CharStyle:" + sKeyFontName
							+ ">(<CharStyle:>");
					text = text.replace("�y�J�����L�[�z", "<CharStyle:" + sKeyFontName
							+ ">)<CharStyle:>");
					text = text.replace("�y���L�[�z", "<CharStyle:" + sKeyFontName
							+ ">*<CharStyle:>");
					{ // �y0�L�[�z�`�y9�L�[�z
						Pattern pattern = Pattern.compile("�y([0-9])�L�[�z");
						Matcher matcher = pattern.matcher(text);
						while (matcher.find()) {
							text = text.replaceFirst("�y[0-9]�L�[�z", "<CharStyle:"
									+ sKeyFontName + ">" + matcher.group(1)
									+ "<CharStyle:>");
							matcher = pattern.matcher(text);
						}
					}
					text = text.replace("�y�N���A�^�����L�[�z", "<CharStyle:"
							+ sKeyFontName + ">C<CharStyle:>");
					text = text.replace("�y�I�b�L�[�z", "<CharStyle:" + sKeyFontName
							+ ">F<CharStyle:>");
					text = text.replace("�y���[���L�[�z", "<CharStyle:" + sKeyFontName
							+ ">L<CharStyle:>");
					text = text.replace("�y���M�L�[�z", "<CharStyle:" + sKeyFontName
							+ ">N<CharStyle:>");
					text = text.replace("�yEZ�L�[�z", "<CharStyle:" + sKeyFontName
							+ ">R<CharStyle:>");
					text = text.replace("�y�㉺���E�L�[�z", "<CharStyle:"
							+ sKeyFontName + ">a<CharStyle:>");
					text = text.replace("�y�T�C�h����L�[�z", "<CharStyle:"
							+ sKeyFontName + ">b<CharStyle:>");
					text = text.replace("�y�Z���^�[�L�[�z", "<CharStyle:"
							+ sKeyFontName + ">c<CharStyle:>");
					text = text.replace("�y���L�[�z", "<CharStyle:" + sKeyFontName
							+ ">d<CharStyle:>");
					text = text.replace("�yTV�L�[�z", "<CharStyle:" + sKeyFontName
							+ ">e<CharStyle:>");
					text = text.replace("�y�T�C�h�E�L�[�z", "<CharStyle:"
							+ sKeyFontName + ">f<CharStyle:>");
					text = text.replace("�y�T�C�h���L�[�z", "<CharStyle:"
							+ sKeyFontName + ">g<CharStyle:>");
					text = text.replace("�y�T�C�h���E�L�[�z", "<CharStyle:"
							+ sKeyFontName + ">gf<CharStyle:>");
					text = text.replace("�y�㉺�L�[�z", "<CharStyle:" + sKeyFontName
							+ ">j<CharStyle:>");
					text = text.replace("�y���L�[�z", "<CharStyle:" + sKeyFontName
							+ ">l<CharStyle:>");
					text = text.replace("�y�E�L�[�z", "<CharStyle:" + sKeyFontName
							+ ">r<CharStyle:>");
					text = text.replace("�y���E�L�[�z", "<CharStyle:" + sKeyFontName
							+ ">s<CharStyle:>");
					text = text.replace("�y��L�[�z", "<CharStyle:" + sKeyFontName
							+ ">u<CharStyle:>");
					// �y�}���`�L�[�z�Ɓy�N�C�b�N�L�[�z�͓����L�[�t�H���g�iw�j
					text = text.replace("�y�}���`�L�[�z", "<CharStyle:" + sKeyFontName
							+ ">w<CharStyle:>");
					text = text.replace("�y�N�C�b�N�L�[�z", "<CharStyle:"
							+ sKeyFontName + ">w<CharStyle:>");
					// KD47�ȍ~
					text = text.replace("�yBOOK�L�[�z", "<CharStyle:"
							+ sKeyFontName + ">x<CharStyle:>");
					// KD48�ȍ~
					text = text.replace("�y�c���D��ؑփL�[�z", "<CharStyle:"
							+ sKeyFontName + ">m<CharStyle:>");
					text = text.replace("�y�N���A�L�[�z", "<CharStyle:" + sKeyFontName
							+ ">n<CharStyle:>");
					text = text.replace("�y�T�C�h�d���L�[�z", "<CharStyle:"
							+ sKeyFontName + ">o<CharStyle:>");
					text = text.replace("�y���b�N�L�[�z", "<CharStyle:" + sKeyFontName
							+ ">p<CharStyle:>");
					if (text.matches(".*�y�d���L�[�z.*")) {
						Tx2x.appendWarn("�y�d���L�[�z�͞B���ł��B�y�I�b�L�[�z�܂��́y�T�C�h�d���L�[�z���g�p���Ă��������B�F"
								+ text);
					}
				}
				// text = text.replaceAll("-", "<2013>"); // ���v����Ȃ�����
				// text = text.replaceAll("(<[^>]+)<2013>([^>]+>)", "$1-$2"); //
				// �^�O�̒���-�́A<2013>�ł̓_��

				if (text.matches(".*�L�[�z.*")) {
					Tx2x.appendWarn("�s���ȃL�[��������܂����B�F" + text);
				}

				if (!Tx2xOptions.getInstance().getBoolean("InDesignCS5")) {
					// �@�`�S
					text = text.replace("�@", "����1��");
					text = text.replace("�A", "����2��");
					text = text.replace("�B", "����3��");
					text = text.replace("�C", "����4��");
					text = text.replace("�D", "����5��");
					text = text.replace("�E", "����6��");
					text = text.replace("�F", "����7��");
					text = text.replace("�G", "����8��");
					text = text.replace("�H", "����9��");
					text = text.replace("�I", "����10��");
					text = text.replace("�J", "����11��");
					text = text.replace("�K", "����12��");
					text = text.replace("�L", "����13��");
					text = text.replace("�M", "����14��");
					text = text.replace("�N", "����15��");
					text = text.replace("�O", "����16��");
					text = text.replace("�P", "����17��");
					text = text.replace("�Q", "����18��");
					text = text.replace("�R", "����19��");
					text = text.replace("�S", "����20��");
				}
			}

			{ // ��0�`��999�i�������������j
				Pattern pattern = Pattern.compile("��(��[0-9]*)��");
				Matcher matcher = pattern.matcher(text);
				while (matcher.find()) {
					text = text.replaceFirst("����[0-9]*��",
							"<CharStyle:small-up>" + matcher.group(1)
									+ "<CharStyle:>");
					matcher = pattern.matcher(text);
				}
			}

			// ���iR�j��
			text = text.replace("���iR�j��",
					"<CharStyle:small-up><00AE><CharStyle:>");
			// ���iC�j��
			text = text.replace("���iC�j��", "<cOTFContAlt:0><00A9><cOTFContAlt:>");
			// ��TM��
			text = text.replace("��TM��", "<cOTFContAlt:0><2122><cOTFContAlt:>");

			// �`
			// text = text.replace("�`", "<FF5E>");
			// text = text.replace("�|", "<2212>");

			text = text.replace("��P.", "<25B6>P.");
			text = text.replace(" �� ", " <25B6> ");

			{ // ����n
				Pattern pattern = Pattern.compile("����([�O-�X]+)");
				Matcher matcher = pattern.matcher(text);
				if (matcher.find()) {
					text = matcher
							.replaceFirst("����<2009><CharStyle:step\\\\_number02><cOTFContAlt:0><cOTFeatureList:nalt\\\\,7>"
									+ zenkakuNumberToHankakuNumber(matcher
											.group(1))
									+ "<cOTFContAlt:><cOTFeatureList:><cOTFContAlt:0><2009><cOTFContAlt:><CharStyle:>");
				}
			}

			text = text.replace("�������܂ŃC���f���g��", KOKOMADE_INDENT_CHAR);

			iText.setText(text);
		}

		// �W���I�ȃ`�F�b�N�i���ꂼ��Ɨ����Ă���̂ŏ��s���j

		if (longStyle.equals("�y�́z�y�́z") || longStyle.equals("�y�t�^�z�y�́z�y�́z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:�匩�o��>";
		}

		if (longStyle.equals("�y�߁z�y�߁z")) {
			iText.setText(iText.getText().substring(3)); // �y�߁z���폜�������
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:�����o��>";
		}

		if (longStyle.equals("�y���z�y���z") || longStyle.equals("�y���S��̂����Ӂz�y���z�y���z")
				|| longStyle.equals("�y�t�^�z�y���z�y���z")) {
			String ret = "";
			iText.setText(iText.getText().substring(3)); // �y�߁z���폜�������
			// if (iText.getText().equals("�t�H�g�T�C�Y��ݒ肷��"))
			// System.out.println("!");
			if (m_sPrevLongStyle.equals("�ymemo�z�ymemo�z��")) {
				ret += "<ParaStyle:head04\\_01>";
			} else {
				ret += "<ParaStyle:head04>";
			}
			m_sPrevLongStyle = longStyle;
			return ret;
		}

		if (longStyle.equals("�y��2�z�y��2�z")) {
			String ret = "";
			iText.setText(iText.getText().substring(4)); // �y��2�z���폜�������
			ret += "<ParaStyle:head04\\_2line>";
			m_sPrevLongStyle = longStyle;
			return ret;
		}

		if (longStyle.equals("�y�����z�y�����z")
				|| longStyle.equals("�y���S��̂����Ӂz�y�����z�y�����z")
				|| longStyle.equals("�y�t�^�z�y�����z�y�����z")
				|| longStyle.equals("�y�t�^�z�y���p�����z�y�����z�y�����z")) {
			String ret = "";
			iText.setText(iText.getText().substring(4)); // �y�����z���폜�������
			if (m_sPrevLongStyle.equals("�y�菇�z�y�菇�z�y�{���z�y�{���z"))
				ret = "<ParaStyle:head05\\_01>��	";
			else
				ret = "<ParaStyle:head05>��	";
			m_sPrevLongStyle = longStyle;
			return ret;
		}

		if (longStyle.equals("�y�{���z�y�{���z")) {
			String text = iText.getText();
			// <b></b>
			text = text.replace("\\<b\\>", "<CharStyle:body-M>");
			text = text.replace("\\</b\\>", "<CharStyle:>");
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:�{��>";
		}

		if (longStyle.equals("�y�{���z�y�{���z�y�{���z�y�{���z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body01>";
		}

		if (longStyle.equals("�y���p�����z�y�{���z")
				|| longStyle.equals("�y�t�^�z�y���p�����z�y�{���z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body03>";
		}

		if (longStyle.equals("�y���p�����z�y�菇����z�y�菇����z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body03-Bold01>";
		}

		if (longStyle.compareTo("�y���S��̂����Ӂz�y�菇����z�y�菇����z") == 0) {
			iText.setText("��\t" + iText.getText().substring(1));
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body02-Bold01>";
		}

		if (longStyle.equals("�y���p�����z�y���p�����_��i1�j�z�y���p�����_��i1�j�z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body03\\_b>";
		}

		if (longStyle.equals("�y���p�����z�y�ȏ�z�y�ȏ�z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body03-center01>";
		}

		if (longStyle.equals("�y�t�^�z�y���p�����z�y�ӏ������E�z�y�ӏ������E�z")) {
			String text = iText.getText();
			text = text.replaceFirst("�E", "<2022>");
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body03\\_a>";
		}

		if (longStyle.equals("�y�t�^�z�y���p�����z�y�ӏ������E�z�y�ӏ������E�z�y�{���z�y�{���z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body03\\_a>\t";
		}

		if (longStyle.equals("�y�t�^�z�y���p�����z�y�ӏ������E�z�y�ӏ������E�z�y���z�y���z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap04>";
		}

		if (longStyle.equals("�y�t�^�z�yEng�z�y�{���z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body\\_Eng00>";
		}

		if (longStyle.equals("�y�t�^�z�yEng�z�y�ӏ������E�z�y�ӏ������E�z")) {
			String text = iText.getText();
			text = text.replaceFirst("�E", "<2022>");
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body\\_Eng01\\_01>";
		}

		if (longStyle.equals("�y�t�^�z�yEng�z�y�ӏ������E�z�y�ӏ������E�z�y�{���z�y�{���z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body\\_Eng01\\_01>\t";
		}

		if (longStyle.equals("�y�t�^�z�yEng�z�y�\�z�y�s�z�y�Z���z�y�{���z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body\\_Eng00>";
		}

		if (longStyle.equals("�y�ڎ��z�y�{���z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:contents-body01>";
		}

		if (longStyle.equals("�y�ڎ��z�y�ڎ��z�y�{���z�y�{���z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:contents-body02>";
		}

		if (longStyle.equals("�y�ڎ��z�y�ڎ��z�y�{���z�y�{���z�y�{���z�y�{���z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:contents-body03>";
		}

		if (longStyle.equals("�y�ڎ��z�y�ڎ��z�y�{���z�y�{���z�y�{���z�y�{���z�y�{���z�y�{���z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:contents-body04>";
		}

		if (longStyle.equals("�y�Җڎ��z�y�{���z") || longStyle.equals("�y�t�^�z�y�Җڎ��z�y�{���z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:HeadContents01>";
		}

		if (longStyle.equals("�y�Җڎ��z�y�Җڎ��z�y�{���z�y�{���z")
				|| longStyle.equals("�y���S��̂����Ӂz�y�Җڎ��z�y�Җڎ��z�y�{���z�y�{���z")
				|| longStyle.equals("�y�t�^�z�y�Җڎ��z�y�Җڎ��z�y�{���z�y�{���z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:HeadContents02>";
		}

		if (longStyle.equals("�y�t�^�z�y�����z�y�{���z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:index-body01>";
		}

		if (longStyle.equals("�y�t�^�z�y�����z�y�����z�y�{���z�y�{���z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:index-body02>";
		}

		if (longStyle.equals("�y��ʁz�y��ʁz")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body00>";
		}

		if (longStyle.equals("�y�ӏ������E�z�y�ӏ������E�z")
				|| longStyle.equals("�y���S��̂����Ӂz�y�ӏ������E�z�y�ӏ������E�z")
				|| longStyle.equals("�y�t�^�z�y�ӏ������E�z�y�ӏ������E�z")) {
			String text = iText.getText();
			text = text.replaceFirst("�E", "<2022>");
			// <b></b>
			text = text.replace("\\<b\\>", "<CharStyle:body-M>");
			text = text.replace("\\</b\\>", "<CharStyle:>");
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body01>";
		}

		if (longStyle.equals("�y�ӏ������E�z�y�ӏ������E�z�y�\�z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body01>";
		}

		if (longStyle.equals("�y�ӏ������E�z�y�ӏ������E�z�y�\�z�y�s�z�y�Z���z�y�{���z")) {
			m_sPrevLongStyle = longStyle;
			String text = iText.getText();
			if (text.matches(".*�y���������z.*")) {
				iText.setText(text.replaceFirst("�y���������z", ""));
				return "<ParaStyle:table-body01-center01>";
			} else {
				return "<ParaStyle:table-body02>";
			}
		}

		if (longStyle.equals("�y�ӏ������E�z�y�ӏ������E�z�y�\�z�y�s�z�y�Z���F�w�b�_�[�z�y�{���z")) {
			m_sPrevLongStyle = longStyle;
			String text = iText.getText();
			if (text.matches(".*�y���������z.*")) {
				iText.setText(text.replaceFirst("�y���������z", ""));
				return "<ParaStyle:table-body01-center01>";
			} else {
				return "<ParaStyle:table-body02>";
			}
		}

		if (longStyle.equals("�y�ӏ������E�z�y�ӏ������E�z�y�{���z�y�{���z")
				|| longStyle.equals("�y�t�^�z�y�ӏ������E�z�y�ӏ������E�z�y�{���z�y�{���z")) {
			String text = iText.getText();
			// <b></b>
			text = text.replace("\\<b\\>", "<CharStyle:body-M>");
			text = text.replace("\\</b\\>", "<CharStyle:>");
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body01>\t";
		}

		if (longStyle.equals("�y�ӏ������E�z�y�ӏ������E�z�y�ӏ������E�z�y�ӏ������E�z")
				|| longStyle.equals("�y�ӏ������E�z�y�ӏ������E�z�y�ӏ������|�z�y�ӏ������|�z")
				|| longStyle.equals("�y�t�^�z�y�ӏ������E�z�y�ӏ������E�z�y�ӏ������|�z�y�ӏ������|�z")) {
			String text = iText.getText();
			text = text.replaceFirst("�|", "-");
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body01\\_a>";
		}

		if (longStyle.equals("�y�ӏ������E�z�y�ӏ������E�z�y���z�y���z")
				|| longStyle.equals("�y�t�^�z�y�ӏ������E�z�y�ӏ������E�z�y���z�y���z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap03>";
		}

		if (longStyle.equals("�y�ӏ������E�z�y�ӏ������E�z�y���z�y���z�y�{���z�y�{���z")
				|| longStyle.equals("�y�t�^�z�y�ӏ������E�z�y�ӏ������E�z�y���z�y���z�y�{���z�y�{���z�y�{���z�y�{���z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap03>\t";
		}

		if (longStyle.equals("�y*�z�y*�z") || longStyle.equals("�y�t�^�z�y*�z�y*�z")
				|| longStyle.equals("�y�t�^�z�yEng�z�y*�z�y*�z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap01\\_a>";
		}

		if (longStyle.equals("�y���z�y���z") || longStyle.equals("�y�t�^�z�y���z�y���z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap01>";
		}

		if (longStyle.equals("�y�t�^�z�y���E�z�y���E�z")) {
			iText.setText(iText.getText().substring(1)); // �����폜�������
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap01>";
		}

		if (longStyle.equals("�y���z�y���z�y�{���z�y�{���z")
				|| longStyle.equals("�y�t�^�z�y���z�y���z�y�{���z�y�{���z")
				|| longStyle.equals("�y�t�^�z�y���E�z�y���E�z�y�{���z�y�{���z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap01>\t";
		}

		if (longStyle.equals("�y���z�y���z�y�ӏ������E�z�y�ӏ������E�z")
				|| longStyle.equals("�y�t�^�z�y���z�y���z�y�ӏ������E�z�y�ӏ������E�z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap04>";
		}

		if (longStyle.equals("�y��0�z�y��0�z")
				|| longStyle.equals("�y���S��̂����Ӂz�y��0�z�y��0�z")
				|| longStyle.equals("�y�t�^�z�y��0�z�y��0�z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap01\\_a>";
		}

		if (longStyle.equals("�y��0�z�y��0�z�y�{���z�y�{���z")
				|| longStyle.equals("�y�t�^�z�y��0�z�y��0�z�y�{���z�y�{���z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap01\\_a>\t";
		}

		if (longStyle.equals("�y�\�z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:�\\\:�\>";
		}

		if (longStyle.equals("�y�\�z�y�s�z�y�Z���F�w�b�_�[�z�y�{���z")) {
			m_sPrevLongStyle = longStyle;
			iText.setText(iText.getText().replace("�y�w�b�_�[�z", ""));
			return "<ParaStyle:�\\\:�\�w�b�_�[>";
		}

		if (longStyle.equals("�y�t�^�z�y�\�z�y�s�z�y�Z���F�w�b�_�[�z�y�{���z")) {
			m_sPrevLongStyle = longStyle;
			String text = iText.getText();
			if (text.matches(".*�y���������z.*")) {
				iText.setText(text.replaceFirst("�y���������z", ""));
				return "<ParaStyle:table-title02>";
			} else {
				return "<ParaStyle:table-body03>";
			}
		}

		if (longStyle.equals("�y�\�z�y�s�z�y�Z���z�y�{���z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:�\\\:�\�{��>";
		}

		if (longStyle.equals("�y���S��̂����Ӂz�y�\�z�y�s�z�y�Z���z�y�{���z")) {
			m_sPrevLongStyle = longStyle;
			String text = iText.getText();
			if (text.matches(".*�y���������z.*")) {
				iText.setText(text.replaceFirst("�y���������z", ""));
				return "<ParaStyle:table-body03-center01>";
			} else {
				return "<ParaStyle:table-body04>";
			}
		}

		if (longStyle.equals("�y���S��̂����Ӂz�y�\�z�y�s�z�y�Z���F�w�b�_�[�z�y�{���z")) {
			m_sPrevLongStyle = longStyle;
			String text = iText.getText();
			if (text.matches(".*�y���������z.*")) {
				iText.setText(text.replaceFirst("�y���������z", ""));
				return "<ParaStyle:table-title02>";
			} else {
				return "<ParaStyle:table-body03>";
			}
		}

		if (longStyle.equals("�y���S��̂����Ӂz�y�\�z�y�s�z�y�Z���z�y�ӏ������E�z�y�ӏ������E�z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body04\\_a>";
		}

		if (longStyle.equals("�y���S��̂����Ӂz�y�\�z�y�s�z�y�Z���z�y1.�z�y1.�z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body04\\_a>";
		}

		if (longStyle.equals("�y���S��̂����Ӂz�y�\�z�y�s�z�y�Z���z�y1.�z�y1.�z�y�ӏ������E�z�y�ӏ������E�z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body04\\_a-tab01>";
		}

		if (longStyle.equals("�y���S��̂����Ӂz�y�\�z�y�s�z�y�Z���z�y�ӏ��������z�y�ӏ��������z")) {
			iText.setText(iText.getText().replaceFirst("��",
					"<CharStyle:body_color01>��<CharStyle:>")
					+ ""); // ���ɐF��t����
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body04\\_a>";
		}

		if (longStyle.equals("�y���S��̂����Ӂz�y�\�z�y�s�z�y�Z���z�y�ӏ��������z�y�ӏ��������z�y�{���z�y�{���z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body04\\_a>\t";
		}

		if (longStyle.equals("�y���S��̂����Ӂz�y�\�z�y�s�z�y�Z���z�y�ӏ��������z�y�ӏ��������z�y�ӏ������E�z�y�ӏ������E�z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body04\\_a-tab01>";
		}

		if (longStyle.equals("�y���S��̂����Ӂz�y�\�z�y�s�z�y�Z���z�y�ӏ��������z�y�ӏ��������z�y�ӏ������|�z�y�ӏ������|�z")) {
			m_sPrevLongStyle = longStyle;
			iText.setText(iText.getText().replaceFirst("�|", "<2212>") + "");
			return "<ParaStyle:table-body04\\_a-tab01>";
		}

		if (longStyle.equals("�y���S��̂����Ӂz�y�\�z�y�s�z�y�Z���z�y�ӏ��������z�y�ӏ��������z�y���z�y���z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap04>";
		}

		if (longStyle
				.equals("�y���S��̂����Ӂz�y�\�z�y�s�z�y�Z���z�y�ӏ��������z�y�ӏ��������z�y�ӏ������E�z�y�ӏ������E�z�y�{���z�y�{���z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body04\\_a-tab01>\t";
		}

		if (longStyle
				.equals("�y���S��̂����Ӂz�y�\�z�y�s�z�y�Z���z�y�ӏ��������z�y�ӏ��������z�y�ӏ������E�z�y�ӏ������E�z�y�ӏ������|�z�y�ӏ������|�z")) {
			m_sPrevLongStyle = longStyle;
			iText.setText(iText.getText().replaceFirst("\t",
					KOKOMADE_INDENT_CHAR));
			return "<ParaStyle:table-body04\\_a-tab01>\t";
		}

		if (longStyle.equals("�y���S��̂����Ӂz�y�\�z�y�s�z�y�Z���z�y�ӏ��������z�y�ӏ��������z�y1.�z�y1.�z")) {
			iText.setText(iText.getText().replaceFirst("\t", ""));
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body04\\_a-tab01>";
		}

		if (longStyle.equals("�y���S��̂����Ӂz�y�\�z�y�s�z�y�Z���z�y���z�y���z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap01>";
		}

		if (longStyle.equals("�y�t�^�z�y�\�z�y�s�z�y�Z���z�y�{���z")) {
			m_sPrevLongStyle = longStyle;
			String text = iText.getText();
			if (text.matches(".*�y���������z.*")) {
				iText.setText(text.replaceFirst("�y���������z", ""));
				return "<ParaStyle:table-body03-center01>";
			} else {
				return "<ParaStyle:table-body03>";
			}
		}

		if (longStyle.compareTo("�y�t�^�z�y�\�z�y�s�z�y�Z���z�y�ӏ������E�z�y�ӏ������E�z") == 0) {
			String text = iText.getText();
			text = text.replaceFirst("�E", "<2022>");
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body03\\_a>";
		}

		if (longStyle.compareTo("�y�t�^�z�y�\�z�y�s�z�y�Z���z�y�ӏ������E�z�y�ӏ������E�z�y�{���z�y�{���z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body03\\_a>\t";
		}

		if (longStyle.compareTo("�y�t�^�z�y�\�z�y�s�z�y�Z���z�y���z�y���z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body04\\_a>\t";
		}

		if (longStyle.compareTo("�y�\�z�y�s�z�y�Z���z�y�Q�Ɓz�y�Q�Ɓz") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body02\\_a>";
		}

		if (longStyle.equals("�y�\�z�y�s�z�y�Z���z�y�ӏ������E�z�y�ӏ������E�z")) {
			m_sPrevLongStyle = longStyle;
			String text = iText.getText();
			text = text.replaceFirst("�E", "<2022>");
			iText.setText(text);
			return "<ParaStyle:�\\\:�\�o���b�g>";
		}

		if (longStyle.equals("�y�\�z�y�s�z�y�Z���z�y�ӏ������E�z�y�ӏ������E�z�y�{���z�y�{���z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body02\\_a>\t";
		}

		if (longStyle.compareTo("�y�\�z�y�s�z�y�Z���z�y���z�y���z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap01>";
		}

		if (longStyle.compareTo("�y�\�z�y�s�z�y�Z���z�y�菇����z�y�菇����z") == 0) {
			iText.setText("��\t" + iText.getText().substring(1));
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body01-Bold01>";
		}

		if (longStyle.compareTo("�y��ʈ͂݁z�y��ʁz�y��ʁz") == 0) {
			m_sPrevLongStyle = longStyle;
			// iText.setText(iText.getText()
			// + IDTaggedTextGenerator4KDDI.getCRLF(m_bMac)
			// + "<ParaStyle:space01>");
			return "<ParaStyle:body00>" + Tx2x.getCRLF(m_bMac)
					+ "<ParaStyle:body00>" + Tx2x.getCRLF(m_bMac)
					+ "<ParaStyle:body00>" + Tx2x.getCRLF(m_bMac)
					+ "<ParaStyle:body00>" + Tx2x.getCRLF(m_bMac)
					+ "<ParaStyle:body00>" + Tx2x.getCRLF(m_bMac)
					+ "<ParaStyle:body00>" + Tx2x.getCRLF(m_bMac)
					+ "<ParaStyle:body00>" + Tx2x.getCRLF(m_bMac)
					+ "<ParaStyle:body00>";
		}

		if (longStyle.compareTo("�y��ʈ͂݁z�y�{���z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-cap01>";
		}
		if (longStyle.equals("�y�����z�y�����z")) {
			if (iText.getText().equals("��")) {
				iText.setText("");
				m_sPrevLongStyle = longStyle + "��";
				return "<CellEnd:><RowEnd:><TableEnd:>";
			} else {
				iText.setText("");
				m_sPrevLongStyle = longStyle + "��";

				// FIXME: ���s���Ȃ��悤�ɓ`������
				return "<ParaStyle:�{��><TableStyle:����><TableStart:1,1:0:0<tCellDefaultCellType:Text>><ColStart:<tColAttrWidth:238.52362204733453>><RowStart:<tRowAttrHeight:167.89844595841532><tRowAttrMinRowSize:167.89844595841532>><CellStyle:����><StylePriority:1><CellStart:1,1>";
			}
		}

		if (longStyle.equals("�y�����z�y�ӏ������E�z�y�ӏ������E�z")) {
			m_sPrevLongStyle = longStyle;
			String text = iText.getText();
			text = text.replaceFirst("�E", "<2022>");
			iText.setText(text);
			return "<ParaStyle:�o���b�g>";
		}

		if (longStyle.equals("�y�����z�y�ӏ������E�z�y�ӏ������E�z�y�菇�z�y�菇�z")) {
			m_sPrevLongStyle = longStyle;
			// �菇�����̒u������
			String text = iText.getText();
			text = text.replaceFirst("^�O", ""); // "�O\t"�͍폜����
			Pattern pattern = Pattern.compile("^([�O-�X]+)");
			Matcher matcher = pattern.matcher(text);
			if (matcher.find()) {
				text = matcher
						.replaceFirst(zenkakuNumberToHankakuNumber(matcher
								.group(1)) + ".");
			}
			iText.setText(text);
			return "<ParaStyle:�o���b�g�⑫�o���b�g>";
		}

		if (longStyle.equals("�y�����z�y�ӏ������E�z�y�ӏ������E�z�y�菇�z�y�菇�z�y�{���z�y�{���z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:�o���b�g�⑫�o���b�g>\t";
		}

		if (longStyle.compareTo("�y�����z�y�ӏ������E�z�y�ӏ������E�z�y�ӏ������E�z�y�ӏ������E�z") == 0) {
			m_sPrevLongStyle = longStyle;
			String text = iText.getText();
			text = text.replaceFirst("�E", "<2022>");
			iText.setText(text);
			return "<ParaStyle:�o���b�g�⑫�o���b�g>";
		}

		if (longStyle.compareTo("�y�����z�y�ӏ������E�z�y�ӏ������E�z�y�ӏ������E�z�y�ӏ������E�z�y�{���z�y�{���z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:�o���b�g�⑫�o���b�g>\t";
		}

		if (longStyle.equals("�y�菇�z�y�菇�z") || longStyle.equals("�y�t�^�z�y�菇�z�y�菇�z")) {
			// �菇�����̒u������
			String text = iText.getText();
			String sStepNumber = text.substring(0, text.indexOf("\t"));
			text = text.replaceFirst("^�O", ""); // "�O\t"�͍폜����
			Pattern pattern = Pattern.compile("^([�O-�X]+)");
			Matcher matcher = pattern.matcher(text);
			if (matcher.find()) {
				text = matcher
						.replaceFirst(zenkakuNumberToHankakuNumber(matcher
								.group(1)) + ".");
			}

			iText.setText(text);

			// �\�g�݂ɂ��邩���f
			// �菇�̊J�n
			String ret = "";

			// �菇�̏ꍇ�͂��̍s��\�g�݂ɂ��邩��������
			// System.out.println("���̍s��\�g�݂ɂ��邩�������܂��B");
			m_nStepTableType = 0;
			if (iText.getText().matches(".*	�y���.*")) {
				// ��ʂ���
				m_nStepTableType |= 0x1;
				m_nStepTableWidth = 3;
				m_sStepCaption = iText.getText().substring(
						1 + iText.getText().indexOf("�z�s"));
				iText.setText(iText.getText().substring(0,
						iText.getText().indexOf("	�y���")));
			}
			if (iText.getText().equals("\t")
					|| iText.getText()
							.matches(
									"<CharStyle:step\\\\_number01><cOTFContAlt:0><cOTFeatureList:nalt\\\\,7>[0-9]+<cOTFContAlt:><cOTFeatureList:><CharStyle:>\\t")) {
				// "[�O-�X]+\\t")) {

				// ����ɕ\�g�݂����鑀�앶
				m_nStepTableType |= 0x2;
				m_nStepTableWidth = 0;
				m_sStepCaption = iText.getText().substring(0,
						iText.getText().indexOf("\t"));
				if (m_sStepCaption.equals("")) {
					m_sStepCaption = " ";
				}
			}
			if (m_nStepTableType == 0) {
				m_nStepTableType = 0;
				m_nStepTableWidth = 0;
				m_sStepCaption = "";
			}

			if (m_nPrevStepTableType == 0x1) {
				// ��ʂ���菇�̏I��
				String tCellAttrTopInset = "5.669291338582678";
				if (m_sStepNumber.equals("�P")) {
					tCellAttrTopInset = "0";
				}
				ret += "<CellEnd:><CellStart:1,1<tCellAttrLeftInset:0><tCellAttrTopInset:"
						+ tCellAttrTopInset
						+ "><tCellAttrRightInset:0><tCellAttrBottomInset:0><tCellAttrLeftStrokeWeight:0><tCellAttrRightStrokeWeight:0><tCellAttrTopStrokeWeight:0><tCellAttrBottomStrokeWeight:0><tCellLeftStrokeColor:Black><tCellTopStrokeColor:Black><tCellRightStrokeColor:Black><tCellBottomStrokeColor:Black><tcLeftStrokeType:Solid><tcRightStrokeType:Solid><tcTopStrokeType:Solid><tcBottomStrokeType:Solid><tCellAttrLeftStrokeTint:100><tCellAttrRightStrokeTint:100><tCellAttrTopStrokeTint:100><tCellAttrBottomStrokeTint:100><tCellLeftStrokeOverprint:0><tCellRightStrokeOverprint:0><tCellTopStrokeOverprint:0><tCellBottomStrokeOverprint:0><tCellLeftStrokeGapTint:100><tCellRightStrokeGapTint:100><tCellTopStrokeGapTint:100><tCellBottomStrokeGapTint:100><tCellLeftStrokeGapColor:Paper><tCellRightStrokeGapColor:Paper><tCellTopStrokeGapColor:Paper><tCellBottomStrokeGapColor:Paper>><CellEnd:><CellStart:1,1<tCellAttrLeftInset:0><tCellAttrTopInset:"
						+ tCellAttrTopInset
						+ "><tCellAttrRightInset:0><tCellAttrBottomInset:0><tCellAttrLeftStrokeWeight:0><tCellAttrRightStrokeWeight:0><tCellAttrTopStrokeWeight:0><tCellAttrBottomStrokeWeight:0><tCellLeftStrokeColor:Black><tCellTopStrokeColor:Black><tCellRightStrokeColor:Black><tCellBottomStrokeColor:Black><tcLeftStrokeType:Solid><tcRightStrokeType:Solid><tcTopStrokeType:Solid><tcBottomStrokeType:Solid><tCellAttrLeftStrokeTint:100><tCellAttrRightStrokeTint:100><tCellAttrTopStrokeTint:100><tCellAttrBottomStrokeTint:100><tCellLeftStrokeOverprint:0><tCellRightStrokeOverprint:0><tCellTopStrokeOverprint:0><tCellBottomStrokeOverprint:0><tCellLeftStrokeGapTint:100><tCellRightStrokeGapTint:100><tCellTopStrokeGapTint:100><tCellBottomStrokeGapTint:100><tCellLeftStrokeGapColor:Paper><tCellRightStrokeGapColor:Paper><tCellTopStrokeGapColor:Paper><tCellBottomStrokeGapColor:Paper>><ParaStyle:body00>"
						+ Tx2x.getCRLF(m_bMac);
				ret += "<ParaStyle:body00>" + Tx2x.getCRLF(m_bMac);
				ret += "<ParaStyle:body00>" + Tx2x.getCRLF(m_bMac);
				ret += "<ParaStyle:body00>" + Tx2x.getCRLF(m_bMac);
				ret += "<ParaStyle:body00>" + Tx2x.getCRLF(m_bMac);
				ret += "<ParaStyle:body00>" + Tx2x.getCRLF(m_bMac);
				ret += "<ParaStyle:body00>" + Tx2x.getCRLF(m_bMac);
				ret += "<ParaStyle:body00>" + Tx2x.getCRLF(m_bMac);
				ret += "<ParaStyle:-space\\_2mm>" + Tx2x.getCRLF(m_bMac);
				ret += "<ParaStyle:table-cap01>" + m_sPrevStepCaption
						+ "<CellEnd:><RowEnd:>";
				m_sPrevStepCaption = "";

				if (m_nStepTableType != m_nPrevStepTableType) {
					// System.out
					// .println("��:" + m_nStepTableWidth + "�̕\�g�݂ɂ��܂����B");
					ret += "<TableEnd:>" + Tx2x.getCRLF(m_bMac);
					m_nStepTableType = 0;
				}
			}

			m_sStepNumber = sStepNumber; // ���f����i�x���j
			// �o�͂���
			if (m_nStepTableType == 0) {
				// System.out.println("�\�g�݂ɂ��܂���B");
			} else if (m_nStepTableType == 0x1) {
				// ��ʂ���Ȃ����B
				if (m_nStepTableType != m_nPrevStepTableType) {
					// TableStart
					// ���̑O��tBeforeSpace�����߂�
					String tBeforeSpace;
					if (m_sPrevLongStyle.equals("�y�߁z�y�߁z")) {
						tBeforeSpace = "1.4173228346456694";
					} else {
						tBeforeSpace = "0";
					}

					ret += "<ParaStyle:body00><TableStart:1,3:0:0<tCellDefaultCellType:Text><tBeforeSpace:"
							+ tBeforeSpace
							+ "><tAfterSpace:0>><ColStart:<tColAttrWidth:160.15748031496065>><ColStart:<tColAttrWidth:4.251968503937008>><ColStart:<tColAttrWidth:53.85826771653544>>";
				}
				// RowStart
				String tCellAttrTopInset = "5.669291338582678";
				if (m_sStepNumber.equals("�P")) {
					tCellAttrTopInset = "0";
				}
				ret += "<RowStart:<tRowAttrHeight:5.669291338582678><tRowAttrMinRowSize:5.669291338582678>><CellStart:1,1<tCellAttrLeftInset:0><tCellAttrTopInset:"
						+ tCellAttrTopInset
						+ "><tCellAttrRightInset:0><tCellAttrBottomInset:0><tCellAttrLeftStrokeWeight:0><tCellAttrRightStrokeWeight:0><tCellAttrTopStrokeWeight:0><tCellAttrBottomStrokeWeight:0><tCellLeftStrokeColor:Black><tCellTopStrokeColor:Black><tCellRightStrokeColor:Black><tCellBottomStrokeColor:Black><tcLeftStrokeType:Solid><tcRightStrokeType:Solid><tcTopStrokeType:Solid><tcBottomStrokeType:Solid><tCellAttrLeftStrokeTint:100><tCellAttrRightStrokeTint:100><tCellAttrTopStrokeTint:100><tCellAttrBottomStrokeTint:100><tCellLeftStrokeOverprint:0><tCellRightStrokeOverprint:0><tCellTopStrokeOverprint:0><tCellBottomStrokeOverprint:0><tCellLeftStrokeGapTint:100><tCellRightStrokeGapTint:100><tCellTopStrokeGapTint:100><tCellBottomStrokeGapTint:100><tCellLeftStrokeGapColor:Paper><tCellRightStrokeGapColor:Paper><tCellTopStrokeGapColor:Paper><tCellBottomStrokeGapColor:Paper>>";
			} else if (m_nStepTableType == 0x2) {
				iText.setText("");

				m_nPrevStepTableType = m_nStepTableType;
				m_nPrevStepTableWidth = m_nStepTableWidth;
				m_sPrevStepCaption = m_sStepCaption;
				m_sPrevLongStyle = longStyle;
				return "";
			}

			ret += "<ParaStyle:���X�g>";

			m_nPrevStepTableType = m_nStepTableType;
			m_nPrevStepTableWidth = m_nStepTableWidth;
			m_sPrevStepCaption = m_sStepCaption;
			m_sPrevLongStyle = longStyle;
			return ret;
		}

		if (longStyle.equals("�y�菇�z�y�菇�z�y�{���z�y�{���z")
				|| longStyle.equals("�y�菇�z�y�菇�z�y��ʁz�y��ʁz")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:���X�g�⑫>";
		}

		if (longStyle.equals("�y�菇�z�y�菇�z�y�ӏ������E�z�y�ӏ������E�z")) {
			String text = iText.getText();
			text = text.replaceFirst("�E", "<2022>");
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:���X�g�⑫�o���b�g>";
		}

		if (longStyle.compareTo("�y�菇�z�y�菇�z�y�ӏ������E�z�y�ӏ������E�z�y�{���z�y�{���z") == 0) {
			m_sPrevLongStyle = longStyle;

			String text = iText.getText();

			// <b></b>
			text = text.replace("\\<b\\>", "<CharStyle:body-M>");
			text = text.replace("\\</b\\>", "<CharStyle:>");
			iText.setText(text);

			return "<ParaStyle:step-body01\\_a>\t";
		}

		if (longStyle.compareTo("�y�菇�z�y�菇�z�y�ӏ������E�z�y�ӏ������E�z�y�L�[�����z�y�L�[�����z") == 0) {
			m_sPrevLongStyle = longStyle;

			String text = iText.getText();
			text = text.replaceFirst("�F", "�F" + KOKOMADE_INDENT_CHAR);

			// <b></b>
			text = text.replace("\\<b\\>", "<CharStyle:body-M>");
			text = text.replace("\\</b\\>", "<CharStyle:>");
			iText.setText(text);

			return "<ParaStyle:step-body01\\_a>\t";
		}

		if (longStyle.compareTo("�y�菇�z�y�菇�z�y�ӏ������E�z�y�ӏ������E�z�y���z�y���z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:step-body02\\_02\\_kome>";
		}

		if (longStyle.compareTo("�y�菇�z�y�菇�z�y�ӏ������E�z�y�ӏ������E�z�y�ӏ������|�z�y�ӏ������|�z") == 0) {
			String text = iText.getText();
			text = text.replaceFirst("\t", KOKOMADE_INDENT_CHAR);
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:step-body01\\_a>\t";
		}

		if (longStyle.compareTo("�y�菇�z�y�菇�z�y�ӏ������E�z�y�ӏ������E�z�y��F�z�y��F�z") == 0) {
			String text = iText.getText();
			int c = text.indexOf("��F");
			if (c != -1) {
				text = "��F" + KOKOMADE_INDENT_CHAR + text.substring(c + 2);
			}
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:step-body01\\_a>\t";
		}

		if (longStyle.equals("�y�菇�z�y�菇�z�y�ӏ������E�z�y�ӏ������E�z�y�\�z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:step-body01\\_a>";
		}

		if (longStyle.compareTo("�y�菇�z�y�菇�z�y�ӏ������E�z�y�ӏ������E�z�y�\�z�y�s�z�y�Z���z�y�{���z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body02>";
		}
		if (longStyle.compareTo("�y�菇�z�y�菇�z�y�ӏ������E�z�y�ӏ������E�z�y�\�z�y�s�z�y�Z���F�w�b�_�[�z�y�{���z") == 0) {
			String text = iText.getText();
			m_sPrevLongStyle = longStyle;
			if (text.matches(".*�y���������z.*")) {
				iText.setText(text.replaceFirst("�y���������z", ""));
				return "<ParaStyle:table-body01-center01>";
			} else {
				return "<ParaStyle:table-body02>";
			}
		}
		if (longStyle.compareTo("�y�菇�z�y�菇�z�y���z�y���z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap02>";
		}

		if (longStyle.compareTo("�y�菇�z�y�菇�z�y��0�z�y��0�z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap02\\_a>";
		}

		if (longStyle.compareTo("�y�菇�z�y�菇�z�y�\�z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:step-body01>";
		}

		if (longStyle.compareTo("�y�菇�z�y�菇�z�y�\�z�y�s�z�y�Z���F�w�b�_�[�z�y�{���z") == 0) {
			String text = iText.getText();
			m_sPrevLongStyle = longStyle;
			if (text.matches(".*�y���������z.*")) {
				iText.setText(text.replaceFirst("�y���������z", ""));
				return "<ParaStyle:table-body01-center01>";
			} else {
				return "<ParaStyle:table-body01>";
			}
		}

		if (longStyle.compareTo("�y�菇�z�y�菇�z�y�\�z�y�s�z�y�Z���z�y�{���z") == 0) {
			m_sPrevLongStyle = longStyle;
			String text = iText.getText();
			// int c2 = text.indexOf("�F");
			// if (c2 != -1) {
			// text = "<CharStyle:body-M>" + text.substring(0, c2)
			// + "<CharStyle:>" + text.substring(c2);
			// }
			// <b></b>
			text = text.replace("\\<b\\>", "<CharStyle:body-M>");
			text = text.replace("\\</b\\>", "<CharStyle:>");
			if (text.matches(".*�y���������z.*")) {
				iText.setText(text.replaceFirst("�y���������z", "")
						+ "<pTextAlignment:>");
				return "<ParaStyle:table-body02><pTextAlignment:Center>";
			} else {
				iText.setText(text);
				return "<ParaStyle:table-body02>";
			}
		}

		if (longStyle.compareTo("�y�菇�z�y�菇�z�y�\�z�y�s�z�y�Z���z�y��F�z�y��F�z") == 0) {
			String text = iText.getText();
			int c = text.indexOf("��F");
			if (c != -1) {
				text = "��F" + KOKOMADE_INDENT_CHAR + text.substring(c + 2);
			}
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body02>";
		}

		if (longStyle.compareTo("�y�菇�z�y�菇�z�y�\�z�y�s�z�y�Z���z�y�ӏ������E�z�y�ӏ������E�z") == 0) {
			String text = iText.getText();
			text = text.replaceFirst("�E", "<2022>");
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body02\\_a>";
		}

		if (longStyle.compareTo("�y�菇�z�y�菇�z�y�\�z�y�s�z�y�Z���z�y�ӏ������E�z�y�ӏ������E�z�y�{���z�y�{���z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body02\\_a>\t";
		}

		if (longStyle.compareTo("�y�菇�z�y�菇�z�y�\�z�y�s�z�y�Z���z�y�ӏ������E�z�y�ӏ������E�z�y���z�y���z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap03>";
		}

		if (longStyle.compareTo("�y�菇�z�y�菇�z�y�\�z�y�s�z�y�Z���z�y�ӏ������E�z�y�ӏ������E�z�y�Q�Ɓz�y�Q�Ɓz") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body02\\_a>\t";
		}

		if (longStyle.compareTo("�y�菇�z�y�菇�z�y�\�z�y�s�z�y�Z���z�y�Q�Ɓz�y�Q�Ɓz") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body02\\_a>";
		}

		if (longStyle.compareTo("�y�菇�z�y�菇�z�y�\�z�y�s�z�y�Z���z�y���z�y���z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap01>";
		}

		if (longStyle.compareTo("�y�菇�z�y�菇�z�y�\�z�y�s�z�y�Z���z�y�菇����z�y�菇����z") == 0) {
			iText.setText(iText.getText().substring(1)); // �����폜�������
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body01-Bold01>";
		}

		if (longStyle.compareTo("�y�菇�z�y�菇�z�y�菇����z�y�菇����z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:step-body02\\_01\\_M>";
		}

		if (longStyle.compareTo("�y�菇�z�y�菇�z�y�菇����z�y�菇����z�y�{���z�y�{���z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:step-body02\\_02>";
		}

		if (longStyle.compareTo("�y�菇�z�y�菇�z�y�菇����z�y�菇����z�y��F�z�y��F�z") == 0) {
			String text = iText.getText();
			int c = text.indexOf("��F");
			if (c != -1) {
				text = "��F" + KOKOMADE_INDENT_CHAR + text.substring(c + 2);
			}
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:step-body02\\_02>";
		}

		if (longStyle.compareTo("�y�菇�z�y�菇�z�y�菇����z�y�菇����z�y���z�y���z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:step-body02\\_02\\_kome>";
		}

		if (longStyle.compareTo("�y�菇�z�y�菇�z�y�菇����z�y�菇����z�y�ӏ������E�z�y�ӏ������E�z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:step-body02\\_03\\_bullet>";
		}

		if (longStyle.compareTo("�y�菇�z�y�菇�z�y�菇����z�y�菇����z�y�ӏ������E�z�y�ӏ������E�z�y�{���z�y�{���z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:step-body02\\_03\\_bullet>\t";
		}

		if (longStyle.compareTo("�y�\�z�y�s�z�y�Z���z�y�菇�z�y�菇�z") == 0
				|| longStyle.compareTo("�y�菇�z�y�菇�z�y�\�z�y�s�z�y�Z���z�y�菇�z�y�菇�z") == 0) {
			String ret;
			String text = iText.getText();
			if (text.matches("^�O\t.*")) {
				text = text.replaceFirst("^�O\t", "");
				ret = "<ParaStyle:table-body01-Bold02>";
			} else {
				text = text.replaceFirst("^�P", "1.");
				text = text.replaceFirst("^�Q", "2.");
				text = text.replaceFirst("^�R", "3.");
				text = text.replaceFirst("^�S", "4.");
				text = text.replaceFirst("^�T", "5.");
				text = text.replaceFirst("^�U", "6.");
				text = text.replaceFirst("^�V", "7.");
				text = text.replaceFirst("^�W", "8.");
				text = text.replaceFirst("^�X", "9.");
				ret = "<ParaStyle:table-body01-Bold01>";
			}
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return ret;
		}

		if (longStyle.compareTo("�y�菇�z�y�菇�z�y�\�z�y�s�z�y�Z���z�y�菇�z�y�菇�z�y�{���z�y�{���z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body02\\_b>\t";
		}

		if (longStyle.compareTo("�y�菇�z�y�菇�z�y�L�[�����z�y�L�[�����z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:step-body01>";
		}

		if (longStyle.compareTo("�y�菇�z�y�菇�z�y�L�[�����z�y�{���z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:step-body01>";
		}

		if (longStyle.compareTo("�y�菇����z�y�菇����z") == 0) {
			iText.setText("��\t" + iText.getText().substring(1));
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body02-Bold01\\_02>";
		}

		if (longStyle.compareTo("�y�t�^�z�y�菇����z�y�菇����z") == 0) {
			iText.setText("��\t" + iText.getText().substring(1));
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:step-title02>";
		}

		if (longStyle.compareTo("�y�t�^�z�y�菇����z�y�菇����z�y�{���z�y�{���z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:step-title02>\t";
		}

		if (longStyle.compareTo("�y�t�^�z�y�菇����z�y�菇����z�y���z�y���z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap04>";
		}

		if (longStyle.compareTo("�y�菇�z�y�菇�z�y�@�z�y�@�z") == 0) {
			m_sPrevLongStyle = longStyle;
			String text = iText.getText();

			int c1 = text.indexOf("\t");
			int c2 = text.indexOf("�F");
			if (c1 != -1 && c2 != -1) {
				// �@ xxxxx�Fabcdefghijklmnopqrstuvwxyz
				// "xxxxx"�͑����ɂȂ�
				text = text.substring(0, c1 + 1) + "<CharStyle:body-M>"
						+ text.substring(c1 + 1, c2) + "<CharStyle:>"
						+ text.substring(c2);
			} else if (text.matches("^.*[^�B]$")) {
				// �@ xxxxx �i�r���ɁF�������u�B�v�ŏI����Ă��Ȃ��ꍇ�j
				// "xxxxx"�͑����ɂȂ�
				text = text.substring(0, c1 + 1) + "<CharStyle:body-M>"
						+ text.substring(c1 + 1) + "<CharStyle:>";
			}
			iText.setText(text);
			return "<ParaStyle:step-body01\\_b>";
		}

		if (longStyle.compareTo("�y�菇�z�y�菇�z�y�iM�j�@�z�y�iM�j�@�z") == 0) {
			String text = iText.getText();
			text = text.replaceAll("�iM�j", "");
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:step-body01\\_b>";
		}

		if (longStyle.equals("�y�菇�z�y�菇�z�y�@�z�y�@�z�y�{���z�y�{���z")
				|| longStyle.equals("�y�菇�z�y�菇�z�y�iM�j�@�z�y�iM�j�@�z�y�{���z�y�{���z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:step-body01\\_b>\t";
		}

		if (longStyle.compareTo("�y�菇�z�y�菇�z�y�@�z�y�@�z�y�ӏ������E�z�y�ӏ������E�z") == 0) {
			String text = iText.getText();
			text = text.replaceFirst("�E\t", "<2022>" + KOKOMADE_INDENT_CHAR);
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:step-body01\\_b>\t";
		}

		if (longStyle.compareTo("�y�@�z�y�@�z") == 0) {
			String text = iText.getText();
			int c1 = text.indexOf("\t");
			int c2 = text.indexOf("�F");
			if (c1 != -1 && c2 != -1) {
				text = text.substring(0, c1 + 1) + "<CharStyle:body-M>"
						+ text.substring(c1 + 1, c2) + "<CharStyle:>"
						+ text.substring(c2, c2 + 1) + KOKOMADE_INDENT_CHAR
						+ text.substring(c2 + 1);
			} else if (text.matches("^.*[^�B]$")) {
				text = text.substring(0, c1 + 1) + "<CharStyle:body-M>"
						+ text.substring(c1 + 1) + "<CharStyle:>";
			}
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body02>";
		}

		if (longStyle.compareTo("�y�@�z�y�@�z�y�{���z�y�{���z") == 0
				|| longStyle.equals("�y�iM�j�@�z�y�iM�j�@�z�y�{���z�y�{���z")
				|| longStyle.equals("�y�iM�j�z�y�iM�j�z�y�{���z�y�{���z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body02>\t";
		}

		if (longStyle.compareTo("�y�@�z�y�@�z�y�ӏ������E�z�y�ӏ������E�z") == 0) {
			String text = iText.getText();
			text = text.replaceFirst("�E", "<2022>");
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body02\\_a>";
		}

		if (longStyle.compareTo("�y�@�z�y�@�z�y���z�y���z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:cap04>";
		}

		if (longStyle.equals("�y�@�z�y�@�z�y�\�z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body02>";
		}

		if (longStyle.compareTo("�y�@�z�y�@�z�y�\�z�y�s�z�y�Z���F�w�b�_�[�z�y�{���z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:table-body01>";
		}

		if (longStyle.compareTo("�y�@�z�y�@�z�y�\�z�y�s�z�y�Z���z�y�{���z") == 0) {
			m_sPrevLongStyle = longStyle;
			// String text = iText.getText();
			// int c2 = text.indexOf("�F");
			// if (c2 != -1) {
			// text = "<CharStyle:body-M>" + text.substring(0, c2)
			// + "<CharStyle:>" + text.substring(c2);
			// }
			// iText.setText(text);
			return "<ParaStyle:table-body02>";
		}

		if (longStyle.equals("�y�iM�j�@�z�y�iM�j�@�z") || longStyle.equals("�y�iM�j�z�y�iM�j�z")) {
			String text = iText.getText();
			int c1 = text.indexOf("\t");
			if (c1 != -1) {
				text = text.substring(0, c1 + 1).replaceAll("�iM�j", "")
						+ "<CharStyle:body-M>" + text.substring(c1 + 1)
						+ "<CharStyle:>";
			}
			iText.setText(text);
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body02>";
		}
		if (longStyle.compareTo("�y�L�[�����z�y�L�[�����z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body00>";
		}

		if (longStyle.equals("�y�ӏ��������z�y�ӏ��������z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body02>";
		}

		if (longStyle.equals("�y�t�^�z�y�ӏ��������z�y�ӏ��������z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body02-Bold01\\_01>";
		}

		if (longStyle.equals("�y�ӏ��������z�y�ӏ��������z�y�{���z�y�{���z")
				|| longStyle.equals("�y�t�^�z�y�ӏ��������z�y�ӏ��������z�y�{���z�y�{���z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body02>\t";
		}

		if (longStyle.compareTo("�y1.�z�y1.�z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body02>";
		}

		if (longStyle.compareTo("�y1.�z�y1.�z�y�{���z�y�{���z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body02>\t";
		}

		if (longStyle.compareTo("�y1.�z�y1.�z�y�ӏ������E�z�y�ӏ������E�z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body02\\_a>";
		}

		if (longStyle.compareTo("�y1.�z�y1.�z�y�ӏ������E�z�y�ӏ������E�z�y�{���z�y�{���z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body02\\_a>\t";
		}

		if (longStyle.compareTo("�y1.�z�y1.�z�y1�z�y1�z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body02\\_a>";
		}

		if (longStyle.compareTo("�y1.�z�y1.�z�y�\�z") == 0) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body02>";
		}

		if (longStyle.equals("�y1.�z�y1.�z�y�\�z�y�s�z�y�Z���z�y�{���z")) {
			m_sPrevLongStyle = longStyle;
			String text = iText.getText();
			if (text.matches(".*�y���������z.*")) {
				// �����������̂�dummy�ɂ��܂��B
				iText.setText(text.replaceFirst("�y���������z", ""));
				return "<ParaStyle:table-body01-center01>";
			} else {
				return "<ParaStyle:table-body02>";
			}
		}

		if (longStyle.equals("�y1.�z�y1.�z�y�\�z�y�s�z�y�Z���F�w�b�_�[�z�y�{���z")) {
			m_sPrevLongStyle = longStyle;
			String text = iText.getText();
			if (text.matches(".*�y���������z.*")) {
				iText.setText(text.replaceFirst("�y���������z", ""));
				return "<ParaStyle:table-title01>";
			} else {
				return "<ParaStyle:table-body01>";
			}
		}

		if (longStyle.equals("�y��F�z�y��F�z")) {
			m_sPrevLongStyle = longStyle;
			return "<ParaStyle:body02-Bold01\\_01>";
		}

		if (longStyle.equals("�yStep 1�z�yStep 1�z")) {
			m_sPrevLongStyle = longStyle;
			String text = "<CharStyle:�i���o�[�X�e�b�v>" + iText.getText();
			text = text.replaceFirst("\t", "<CharStyle:>\t");
			iText.setText(text);
			return "<ParaStyle:�X�e�b�v>";
		}

		if (longStyle.equals("�y�ʎ��^�C�g���z�y�ʎ��^�C�g���z")
				|| longStyle.equals("�y���S��̂����Ӂz�y�ʎ��^�C�g���z�y�ʎ��^�C�g���z")
				|| longStyle.equals("�y�t�^�z�y�ʎ��^�C�g���z�y�ʎ��^�C�g���z")
				|| longStyle.equals("�y���p�����z�y�ʎ��^�C�g���z�y�ʎ��^�C�g���z")) {
			m_sStepCaption = "";
			// �ʎ�xxx-x��ǂݎ���āA���y�[�W�̈ʒu�𒲐�����
			String text = iText.getText();
			text = text.replaceAll("��+", "");
			text = text.replaceFirst("�ʎ�k?", "");
			if (m_bMac) {
				text = text.replaceFirst("(<2013>|-)[0-9-A-Z].*", "");
			} else {
				text = text.replaceFirst("-[0-9-A-Z].*", "");
			}
			text = text.replaceFirst("�\1", "0000");
			text = text.replaceFirst("�\2", "0000");
			text = text.replaceFirst("�\3", "50000");
			text = text.replaceFirst("�\4", "50000");

			Integer nPrefix = 0;
			String sPrefix = text.replaceFirst("[0-9]+$", "");
			if (sPrefix.length() > 0) {
				text = text.replaceFirst("[^0-9]+", "");
				nPrefix = m_cPrefixTable.get(sPrefix);
				if (nPrefix == null) {
					nPrefix = (m_cPrefixTable.size() + 1) * 1000;
					m_cPrefixTable.put(sPrefix, nPrefix);
				}
			}

			// �y�[�W�ԍ����܂�ł����ꍇ�͉��y�[�W����������
			int pageNum;
			String ret = "";
			try {
				pageNum = Integer.parseInt(text) / 2;
				pageNum *= 2;
				pageNum += nPrefix;
				if (/* m_nPrevPageNum != -1 && */m_nPrevPageNum != pageNum) {
					ret += "<ParaStyle:body00><cNextXChars:EvenPage>"
							+ Tx2x.getCRLF(m_bMac);
				}
				m_nPrevPageNum = pageNum;
			} catch (NumberFormatException e) {
				// �p�X
			}

			// �t�^�̏ꍇ�́A�i�t�^�j�Ɠ����
			if (longStyle.indexOf("�y�t�^�z") == 0) {
				text = iText.getText();
				Pattern pattern = Pattern.compile("(��+)");
				Matcher matcher = pattern.matcher(text);
				if (matcher.find())
					text = text.replaceFirst("��+", matcher.group(1) + "�i�t�^�j");
				iText.setText(text);
			}

			// ���p�����̏ꍇ�́A�i���p�����j�Ɠ����
			if (longStyle.indexOf("�y���p�����z") == 0) {
				text = iText.getText();
				Pattern pattern = Pattern.compile("(��+)");
				Matcher matcher = pattern.matcher(text);
				if (matcher.find())
					text = text.replaceFirst("��+", matcher.group(1) + "�i���p�����j");
				iText.setText(text);
			}

			// Eng�̏ꍇ�́A�iEng�j�Ɠ����
			if (longStyle.indexOf("�yEng�z") == 0) {
				text = iText.getText();
				Pattern pattern = Pattern.compile("(��+)");
				Matcher matcher = pattern.matcher(text);
				if (matcher.find())
					text = text.replaceFirst("��+", matcher.group(1) + "�iEng�j");
				iText.setText(text);
			}

			m_sPrevLongStyle = longStyle;
			ret += "<ParaStyle:body00>";
			return ret;
		}

		// �ȍ~�A�_�~�[�X�^�C���̏���
		m_sPrevLongStyle = longStyle;
		return dummyStyle(longStyle);// + longStyle;
		// throw new IOException("Unknown Style:" + longStyle);
	}

	private String dummyStyle(String longStyle) {
		String style = m_cDummyStyleHashTable.get(longStyle);
		if (style != null) {
			return style;
		}
		DecimalFormat df = new DecimalFormat();
		df.applyLocalizedPattern("0000");
		style = "<ParaStyle:dummy" + df.format(m_nDummyCounter) + ">";
		System.out.println(longStyle + "�́A" + style + "�Ƃ��ďo�͂���܂����B");
		m_cDummyStyleHashTable.put(longStyle, style);
		m_nDummyCounter++;
		return style;
	}

	public void addStyle(Style style) {
		m_StyleLinkedList.add(style);
	}

	public void removeLastStyle() {
		m_StyleLinkedList.removeLast();
	}

	public String getLongStyle() {
		String longStyle = "";
		Iterator<Style> it2 = m_StyleLinkedList.iterator();

		// longStyle�̎擾
		while (it2.hasNext()) {
			Style r2 = it2.next();
			if (r2 == null) {
				longStyle += "�y�{���z";
			} else {
				longStyle += r2.getStyleName();
			}
		}
		return longStyle;
	}

	public void setPrevLongStyle(String prevLongStyle) {
		m_sPrevLongStyle = prevLongStyle;
	}

	public void addLongStyleToArrayList() {
		m_cLongStyleArrayList.add(getLongStyle());
	}

	public String getLongStyleFromArrayList(int nLsIndex) {
		if (nLsIndex == m_cLongStyleArrayList.size()) {
			return ""; // �X�^�C���Ȃ�
		}
		return m_cLongStyleArrayList.get(nLsIndex);
	}

	public String getPrevLongStyle() {
		return m_sPrevLongStyle;
	}

	public static String zenkakuNumberToHankakuNumber(String s) {
		StringBuffer sb = new StringBuffer(s);
		for (int i = 0; i < sb.length(); i++) {
			char c = sb.charAt(i);
			if (c >= '�O' && c <= '�X') {
				sb.setCharAt(i, (char) (c - '�O' + '0'));
			}
		}
		return sb.toString();
	}
}
