/**
 * IntermediateText���AInDesign�̃^�O�t���e�L�X�g�ɕϊ�����
 */
package tx2x;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import tx2x_core.ControlText;
import tx2x_core.IntermediateText;
import tx2x_core.Style;
import tx2x_core.TableManager;

public class IntermediateTextTreeToInDesign {
	String m_sTagFilename = null;
	LinkedList<TableWriter> m_TableWriterList = null;
	private boolean m_bMac;
	int m_nLsIndex = 0;
	private String m_sMaker;
	private boolean m_bDebugMode;

	public IntermediateTextTreeToInDesign(String tagFilename, String sMaker,
			boolean bMac, boolean bDebugMode) {
		super();
		m_sTagFilename = tagFilename;
		m_TableWriterList = new LinkedList<TableWriter>();
		m_bMac = bMac;
		m_sMaker = sMaker;
		m_bDebugMode = bDebugMode;
	}

	void output(ControlText resultRootText) throws IOException {
		IDTTG_FileWriter fwInDesign;
		File aInDesign = new File(m_sTagFilename);
		try {
			fwInDesign = new IDTTG_FileWriter(aInDesign);
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
			return;
		}

		// ��������
		LongStyleManager lsManager = new LongStyleManager(m_sMaker, m_bMac);
		preScan(resultRootText, lsManager); // �v���X�L�����BlsManager�ɃX�^�C�����ilongStyle�j��ArrayList����������
		outputHeader(fwInDesign);
		outputResult(fwInDesign, resultRootText, lsManager);

		try {
			fwInDesign.close(m_bMac);
		} catch (IOException e1) {
			// TODO �����������ꂽ catch �u���b�N
			e1.printStackTrace();
		}
	}

	private void preScan(ControlText resultText, LongStyleManager lsManager) {
		// TODO �����������ꂽ���\�b�h�E�X�^�u
		Iterator<IntermediateText> it = resultText.getChildList().iterator();
		while (it.hasNext()) {
			IntermediateText iText = it.next();
			if (iText.hasChild()) {
				// �q�������遁ControlText�ł���
				ControlText cText = (ControlText) iText;
				Style currentStyle = cText.getStyle();

				lsManager.addStyle(currentStyle);

				/*
				 * ControlText�ł��A�菇�E�\�̏ꍇ�͏������ʂȏo�͕��@���Ƃ�
				 */
				// �\�E�s�E�Z���̊J�n
				if (currentStyle != null && currentStyle.bTableLikeStyle()) {
					if (currentStyle.getStyleName().compareTo("�y�\�z") == 0) {
						if (m_bDebugMode)
							System.out.println("�y�\�z");
						// width���擾
						String sTableInfo = cText.getChildList().get(0)
								.getText();
						String sWidth = sTableInfo.replaceFirst(
								"���\[\\(�i]([0-9]+).*", "$1");
						// String sStyle = "";
						// if (sTableInfo.indexOf("style:") != -1) {
						// sStyle = sTableInfo.replaceFirst(
						// ".*style:([^),]+.*)", "$1");
						// }
						if (Integer.parseInt(sWidth) == 0) {
							Tx2x.appendWarn("sWidth==0");
						}

						// // height���擾
						// ArrayList<IntermediateText> child = cText
						// .getChildList();
						// int nHeight;
						// if (child == null) {
						// nHeight = 0;
						// } else {
						// nHeight = child.size() - 2;
						// }

						/* ���ڂ��Ă���cText�͕\�̎n�܂�Ȃ̂ŁAWidth,Height���擾���ď������n�߂� */
						TableManager currentTable = new TableManager(cText,
								m_bDebugMode);
						TableWriter tWriter = new TableWriter(currentTable);
						m_TableWriterList.add(tWriter);

					} else if (currentStyle.getStyleName().compareTo("�y�s�z") == 0) {
						// TableWriter tWriter = m_TableWriterList.getLast();
						if (m_bDebugMode)
							System.out.println("�y�s�z");
					} else if (currentStyle.getStyleName().compareTo("�y�Z���z") == 0) {
						// TableWriter tWriter = m_TableWriterList.getLast();
						if (m_bDebugMode)
							System.out.println("�y�Z���z");
						if (cText.getChildList().get(0).getText()
								.matches(".*�y�w�b�_�[�z.*")) {
							StyleManager styleManager = StyleManager
									.getInstance();
							Style newStyle = styleManager.getStyle("�y�Z���F�w�b�_�[�z");
							lsManager.removeLastStyle();
							lsManager.addStyle(newStyle);
						}
					}
				}
				preScan(cText, lsManager); // ����ɉ��[���ցi�ċA�j
				// �\�E�s�E�Z���̏I��
				if (currentStyle != null && currentStyle.bTableLikeStyle()) {
					if (currentStyle.getStyleName().compareTo("�y�\�z") == 0) {
						m_TableWriterList.removeLast(); // �\�I��
						lsManager.setPrevLongStyle("�y�\�z��");
					} else if (currentStyle.getStyleName().compareTo("�y�s�z") == 0) {
					} else if (currentStyle.getStyleName().compareTo("�y�Z���z") == 0) {
					}
				}
				lsManager.removeLastStyle();
			} else {
				// �q�������Ȃ�
				Style currentStyle = iText.getStyle();
				if (currentStyle != null) {
					// �X�^�C��������
					if (currentStyle.getStyleName().compareTo("�y�\�z") == 0) {
						// �\�̏ꍇ�͉������Ȃ��c�H
					} else if (iText.getText() != null) {
						// �\�ȊO�̏ꍇ�́c

						// �i���ʁj�e�L�X�g���o��
						lsManager.addStyle(currentStyle); // �X�^�C����push
						lsManager.addLongStyleToArrayList();
						lsManager.removeLastStyle(); // �X�^�C����pop
					}
				} else {
					// �X�^�C�����Ȃ��̂Ńe�L�X�g���o�͂���̂�
					if (iText.getText() != null) {
						lsManager.addLongStyleToArrayList();
					}
				}
			}
		}
	}

	private void outputHeader(IDTTG_FileWriter fwInDesign) throws IOException {
		if (m_bMac == true) {
			fwInDesign.write("<SJIS-MAC>", true, m_bMac);
		} else {
			fwInDesign.write("<SJIS-WIN>", true, m_bMac);
		}
		fwInDesign.write(
				"<Version:7><FeatureSet:InDesign-Japanese><ColorTable:=>",
				true, m_bMac);
	}

	private void outputResult(IDTTG_FileWriter fwInDesign,
			ControlText resultText, LongStyleManager lsManager)
			throws IOException {
		Iterator<IntermediateText> it = resultText.getChildList().iterator();
		while (it.hasNext()) {
			IntermediateText iText = it.next();
			if (iText.hasChild()) {
				// �q�������遁ControlText�ł���
				ControlText cText = (ControlText) iText;
				Style currentStyle = cText.getStyle();

				lsManager.addStyle(currentStyle);

				/*
				 * ControlText�ł��A�菇�E�\�̏ꍇ�͏������ʂȏo�͕��@���Ƃ�
				 */
				// �\�E�s�E�Z���̊J�n
				if (currentStyle != null && currentStyle.bTableLikeStyle()) {
					if (currentStyle.getStyleName().compareTo("�y�\�z") == 0) {
						// width���擾
						String sTableInfo = cText.getChildList().get(0)
								.getText();
						String sWidth = sTableInfo.replaceFirst(
								"���\[\\(�i]([0-9]+).*", "$1");
						// String sStyle = "";
						// if (sTableInfo.indexOf("style:") != -1) {
						// sStyle = sTableInfo.replaceFirst(
						// ".*style:([^),]+).*", "$1");
						// }
						if (Integer.parseInt(sWidth) == 0) {
							Tx2x.appendWarn("sWidth==0");
						}

						// // height���擾
						// ArrayList<IntermediateText> child = cText
						// .getChildList();
						// int nHeight;
						// if (child == null) {
						// nHeight = 0;
						// } else {
						// nHeight = child.size() - 2;
						// }

						/* ���ڂ��Ă���cText�͕\�̎n�܂�Ȃ̂ŁAWidth,Height���擾���ď������n�߂� */
						TableManager currentTable = new TableManager(cText,
								m_bDebugMode);
						TableWriter tWriter = new TableWriter(currentTable);
						m_TableWriterList.add(tWriter);

						// coStart���o��
						// lsManager.getInDesignStyle(cText)�́A�\��}������s�̃X�^�C����Ԃ��Ă����
						fwInDesign.write(
								lsManager.getInDesignStyle(cText,
										m_nLsIndex + 1)
										+ tWriter.getHeader(lsManager,
												m_nLsIndex), false, m_bMac);
					} else if (currentStyle.getStyleName().compareTo("�y�s�z") == 0) {
						TableWriter tWriter = m_TableWriterList.getLast();
						fwInDesign.write(tWriter.getRowHeader(lsManager),
								false, m_bMac);
					} else if (currentStyle.getStyleName().compareTo("�y�Z���z") == 0) {
						TableWriter tWriter = m_TableWriterList.getLast();
						fwInDesign.write(tWriter.getCellHeader(lsManager),
								false, m_bMac);

						if (cText.getChildList().get(0).getText()
								.matches(".*�y�w�b�_�[�z.*")) {
							StyleManager styleManager = StyleManager
									.getInstance();
							Style newStyle = styleManager.getStyle("�y�Z���F�w�b�_�[�z");
							lsManager.removeLastStyle();
							lsManager.addStyle(newStyle);
						}
					}
				}
				outputResult(fwInDesign, cText, lsManager); // ����ɉ��[���ցi�ċA�j
				// �\�E�s�E�Z���̏I��
				if (currentStyle != null && currentStyle.bTableLikeStyle()) {
					if (currentStyle.getStyleName().compareTo("�y�\�z") == 0) {
						fwInDesign.write("<TableEnd:>", true, m_bMac);
						m_TableWriterList.removeLast(); // �\�I��
						lsManager.setPrevLongStyle("�y�\�z��");
					} else if (currentStyle.getStyleName().compareTo("�y�s�z") == 0) {
						fwInDesign.write("<RowEnd:>", false, m_bMac);
					} else if (currentStyle.getStyleName().compareTo("�y�Z���z") == 0) {
						fwInDesign.write("<CellEnd:>", false, m_bMac);
					}
				}
				lsManager.removeLastStyle();
			} else {
				// �q�������Ȃ�
				Style currentStyle = iText.getStyle();
				if (currentStyle != null) {
					// �X�^�C��������
					if (currentStyle.getStyleName().compareTo("�y�\�z") == 0) {
						// �\�̏ꍇ�́A�\���J�n����u���\(xx)�v�܂��́A�\�����u���v�B
						// ���̂Ƃ��뉽�����Ȃ�
					} else if (iText.getText() != null) {
						// �\�ȊO�̏ꍇ�́c

						// �i���ʁj�e�L�X�g���o��
						lsManager.addStyle(currentStyle); // �X�^�C����push
						outputText(fwInDesign, lsManager, iText);
						lsManager.removeLastStyle(); // �X�^�C����pop
					}
				} else {
					// �X�^�C�����Ȃ��̂Ńe�L�X�g���o�͂���̂�
					if (iText.getText() != null) {
						outputText(fwInDesign, lsManager, iText);
					}
				}
			}
		}
	}

	private void outputText(IDTTG_FileWriter fwInDesign,
			LongStyleManager lsManager, IntermediateText iText) {
		if (iText.hasChild()) {
			// System.out.println("outputText:" + iText.getText());
			return; // ControlText�̓J�G���I
		}
		// System.out.println("outputText");
		String realtimeStyle = lsManager.getLongStyle();
		String bufferingStyle = lsManager.getLongStyleFromArrayList(m_nLsIndex);
		if (realtimeStyle.compareTo(bufferingStyle) == 0) {
			// ok!
			// System.out.println("longStyle OK");
		} else {
			// NG!
			System.out.println("longStyle NG:" + realtimeStyle + "/"
					+ bufferingStyle);
		}
		// sLongStyle�𐳂����X�^�C���ɕϊ�
		try {
			String style = lsManager.getInDesignStyle(iText, m_nLsIndex + 1);
			if (style.equals("") == false) {
				fwInDesign.write(style + iText.getText(), true, m_bMac);
				if (m_bDebugMode)
					System.out.println("[" + style + "]" + iText.getText());
			}
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
		m_nLsIndex++;
	}
}
