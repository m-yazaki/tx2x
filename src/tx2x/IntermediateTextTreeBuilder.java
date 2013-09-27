package tx2x;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import tx2x_core.ControlText;
import tx2x_core.IntermediateText;
import tx2x_core.Style;

/*
 * ��񎟃t�H�[�}�b�g�iTx2x�e�L�X�g�𐮌`����j
 */
public class IntermediateTextTreeBuilder {
	boolean m_bMac = true;
	private boolean m_bDebugMode;

	public IntermediateTextTreeBuilder(boolean bMac, boolean bDebugMode) {
		m_bMac = bMac;
		m_bDebugMode = bDebugMode;
	}

	public void parse_file(String sTextFilename, String sMaker)
			throws IOException {
		/*
		 * ��ƗpArrayList������
		 */
		ArrayList<String> allText = new ArrayList<String>();

		/*
		 * Tx2x�`���̃e�L�X�g�t�@�C�����o�b�t�@�iArrayList<String> allText�j�ɓǂݍ���
		 */
		try {
			// ���̓t�@�C��
			File inputFile = new File(sTextFilename);
			BufferedReader bf = new BufferedReader(new FileReader(inputFile));

			String line;
			while ((line = bf.readLine()) != null) {
				// ���[�v
				allText.add(line);
			}

			bf.close();
		} catch (FileNotFoundException e1) {
			Tx2x.appendWarn("�t�@�C����������܂���@IntermediateTextTreeBuilder�F"
					+ sTextFilename);
			return;
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}

		/*
		 * allText�����߂���IntermediateText�c���[�𐶐�
		 * resultRootText���A�c���[�̍��i���[�g�I�u�W�F�N�g�j�ł��B
		 */
		ControlText resultRootText = new ControlText(null, null);

		/*
		 * �ϊ��J�n�B
		 *
		 * allText�����߂��āAresultRootText��IntermediateText�c���[��o�^���܂��B
		 */
		try {
			compileText(allText, resultRootText);
		} catch (IOException e1) {
			// TODO �����������ꂽ catch �u���b�N
			throw new IOException(e1.getMessage());
		}

		/*
		 * ���ʏo�́i����1�j
		 *
		 * �R���\�[���֏o�͂��܂��B�ȈՃf�o�b�O�p�B
		 */
		/*
		 * LinkedList<Style> ruleLinkedList = new LinkedList<Style>(); try {
		 * outputResult(resultRootText, ruleLinkedList); //
		 * outputDump(resultRootText, ruleLinkedList, 0); } catch (IOException
		 * e) { // TODO �����������ꂽ catch �u���b�N e.printStackTrace(); }
		 */
		/*
		 * ���ʏo�́i����2�j
		 *
		 * InDesign�p�̃^�O�t���f�[�^���t�@�C���ɏo�͂��܂��B
		 */
		String sOutputFilename;
		if (m_bMac) {
			sOutputFilename = sTextFilename.replaceFirst(".[Tt][Xx][Tt]$",
					".indesign.txt");
		} else {
			sOutputFilename = sTextFilename.replaceFirst(".[Tt][Xx][Tt]$",
					".win.indesign.txt");
		}
		if (sTextFilename.equals(sOutputFilename)) {
			System.out.println("�㏑������邽�ߒ��~���܂����B�t�@�C�������m�F���Ă��������B");
		} else {
			IntermediateTextTreeToInDesign converter = new IntermediateTextTreeToInDesign(
					sOutputFilename, sMaker, m_bMac, m_bDebugMode);
			try {
				converter.output(resultRootText);
			} catch (IOException e) {
				// TODO �����������ꂽ catch �u���b�N
				e.printStackTrace();
			}
		}
	}

	/*
	 * allText�iTx2x�`���̃e�L�X�g�t�@�C���j��ϊ����āArootText�ɂԂ牺����
	 */
	private void compileText(ArrayList<String> allText, ControlText rootText)
			throws IOException {
		StyleManager cStyleManager = StyleManager.getInstance();
		for (int i = 0; i < allText.size();) {
			/*
			 * 1�s�ǂݎ���āArbControlText�̌���
			 */
			Style styleControlText = cStyleManager.getMatchStyle_Start(allText
					.get(i));

			/*
			 * controlText�̐���
			 */
			ControlText controlText;
			if (styleControlText == null) {
				controlText = new ControlText(null, null);
			} else if (styleControlText.getStyleName().equals("�y�s�z")) {
				throw new IOException("controlText���y�s�z�ɂȂ�܂����B");
			} else {
				controlText = new ControlText(styleControlText,
						styleControlText.getStyleName());
			}

			/* controlText��rootText�̎q���ɓo�^ */
			rootText.getChildList().add(controlText);

			/* �ϊ��I */
			try {
				int prev_i = i;
				i = compileBlock(controlText, allText, i);
				if (i == prev_i) {
					throw new IOException("i == prev_i");
				}
			} catch (IOException e) {
				// TODO �����������ꂽ catch �u���b�N
				// e.printStackTrace();
				String error = "@compileText ===== �ȉ��̕��͂���n�܂�u���b�N�ŃG���[���� =====\n";
				for (int j = 0; j < 3 && j < allText.size(); j++) {
					error += "|" + allText.get(j) + "\n";
				}
				error += "�G���[�����s(" + (i + 1) + "�s��): " + allText.get(i) + "\n";
				Tx2x.appendWarn("�G���[�����s(" + (i + 1) + "�s��)");

				throw new IOException(error + "�@"
						+ e.getMessage().replaceAll("\n", "\n�@"));
			}
		}
	}

	/*
	 * smallPartText��startPos����n�܂�u���b�N����AcontrolText�̎q���ɂ����镔����ϊ�����
	 * �q���ɂ����镔���̕ϊ����I�������A���̓ǂݎ��s�ԍ���Ԃ��B
	 */
	private int compileBlock(ControlText controlText,
			ArrayList<String> smallPartText, int startPos) throws IOException {

		/* controlText�̃X�^�C�������o���Ă��� */
		Style styleControlText = controlText.getStyle();

		/* �u���b�N�𓝊�����IntermediateText�ɁAIntermediateText��o�^���������� */
		ArrayList<IntermediateText> childArrayControlText = controlText
				.getChildList();

		/*
		 * ����ȍ~�A�ϊ����ʂ�childArrayControlText�ɓo�^����
		 */
		/* ���߂�1�s���������� */
		if (styleControlText != null)
			startPos = styleControlText.compileLine(controlText, smallPartText,
					startPos);

		StyleManager cStyleManager = StyleManager.getInstance();

		/* **** �J�n�s�ȍ~���m�F **** */
		for (int currentPos = startPos; currentPos < smallPartText.size();) {

			if (styleControlText != null) {
				// System.out.println(styleControlText.getStyleName() + ":"
				// + smallPartText.get(currentPos));
			} else {
				// System.out.println("�y�{���z:" + smallPartText.get(currentPos));
			}

			String currentLine = smallPartText.get(currentPos);

			/* ����q�̃u���b�N���Ȃ����m�F */
			Style styleCurrentLine = cStyleManager
					.getMatchStyle_Start(currentLine);
			if (styleCurrentLine != null) {
				if (styleControlText == null) {
					/*
					 * ���ہF�{���u���b�N�ŁA�X�^�C���s������ �Ή��F�{���u���b�N�̏I��
					 */
					return currentPos; // ���̃X�^�C���i�u���b�N�j�́A���݈ʒu����ł��邱�Ƃ��Ăяo�����ɓ`����
				} else if (styleControlText.bBulletLikeStyle()) {
					if (styleCurrentLine == styleControlText) {
						// ���ہF�ӏ������^�C�v�ŁA�����X�^�C����������
						// �Ή��FcontrolText�ɓo�^���ׂ��q���ł���Ɖ��߂��āAchildArrayControlText�ɒǉ��B
						IntermediateText textCurrentLine = new IntermediateText(
								styleCurrentLine, currentLine); // 1�s��
						childArrayControlText.add(textCurrentLine);
						currentPos++; // ���̃��[�v�ł́A���s��ǂ�
						continue; // ������controlText���̖{���ł���\�������邽�߁Acontinue�B
					} else {
						/*
						 * ���ہF�ӏ������^�C�v�ŁA�ʂ̃X�^�C����������
						 * �Ή��FcontrolText���I�������Ɣ��f���A���̃��\�b�h�̎d���͏I���B
						 */
						return currentPos; // ���̃X�^�C���i�u���b�N�j�́A���݈ʒu����ł��邱�Ƃ��Ăяo�����ɓ`����
					}
				} else if (styleCurrentLine.bTableLikeStyle()) {
					if (styleControlText.bTableLikeStyle() == false) {
						/*
						 * �u���m�点�v�����́u�\�v�Ȃ� �Ή��F����q�̕\�Ɖ��߂��A�V�����\�����
						 */
						/*
						 * nextControlText�̐���
						 */
						if (styleCurrentLine.getStyleName().equals("�y�s�z")
								|| styleCurrentLine.getStyleName().equals(
										"�y�Z���z")) {
							throw new IOException("���\(n) �������̂ɁA" + currentLine
									+ "�����܂����B");
						}
						ControlText nextControlText = new ControlText(
								styleCurrentLine,
								styleCurrentLine.getStyleName());

						/* controlText�̎q���ɓo�^ */
						controlText.getChildList().add(nextControlText);

						// �y�\�z�̉��߂��n�߂�
						currentPos = compileBlock(nextControlText,
								smallPartText, currentPos);

						continue; // currentPos�́u���v�̎��̍s���w���Ă���͂�
					}
					/*
					 * �m�F���̍s���\�֘A�̃X�^�C��
					 *
					 * �\�X�^�C���̓�����A�ȉ���6�p�^�[���������݂����Ȃ�
					 *
					 * (1)controlText�F�\ currentLine�F�s �c���߂Ă̍s�i1�s�ځj�̎n�܂�
					 *
					 * (2)controlText�F�s currentLine�F�s �c�V�����s�i2�s�ڈȍ~�j�̎n�܂�
					 *
					 * (3)controlText�F�s currentLine�F�Z�� �c���߂ẴZ���i1��ځj�̎n�܂�
					 *
					 * (4)controlText�F�Z�� currentLine�F�\ �c����q�̕\�̎n�܂�
					 *
					 * (5)controlText�F�Z�� currentLine�F�s �c�Ō�̃Z�����I���A2�s�ڈȍ~�̎n�܂�
					 *
					 * (6)controlText�F�Z�� currentLine�F�Z�� �c�V�����Z���i1��ځj�̎n�܂�
					 */
					if (styleCurrentLine == styleControlText) {
						/*
						 * ���ہFcontrolText�Ɠ����X�^�C���������Ă���
						 */
						if (styleControlText.getStyleName().compareTo("�y�s�z") == 0) {
							if (startPos == currentPos) {
								/*
								 * (����2)controlText�F�s currentLine�F�s �i�\��1�s�ځj
								 *
								 * (1)�m�F���̍s���\�֘A�̃X�^�C��
								 * (2)controlText�Ɠ����X�^�C���i�y�s�z�j�ł���
								 * (3)���݂̃u���b�N�̏��߂̈�s�̂Ƃ����ŏ��́y�s�z
								 *
								 * �Ή��F����currentLine�̓Z���̂���œo�^
								 */

								/* nextControlText�i�Z���j�̐��� */
								Style styleNextControlText = cStyleManager
										.getStyle("�y�Z���z");

								ControlText nextControlText = new ControlText(
										styleNextControlText,
										styleNextControlText.getStyleName());

								/* controlText�̎q���ɓo�^ */
								controlText.getChildList().add(nextControlText);

								// currentPos��==========���w���Ă���̂ŁA���̍s����ϊ��J�n
								currentPos = compileBlock(nextControlText,
										smallPartText, currentPos + 1);

								// �Z�����I������Ƃ��́A
								continue; // currentPos�́u-----�v���u=========�v���u���v�̎��̍s���w���Ă���
							} else {
								/*
								 * (����2)controlText�F�s currentLine�F�s �i�\��2�s�ڈȍ~�j
								 *
								 * �Ή��FcontrolText���I�������Ɣ��f���A���̃��\�b�h�̎d���͏I���B
								 */
								return currentPos; // currentPos�́u==========�v���w���Ă���
							}
						} else if (styleControlText.getStyleName().compareTo(
								"�y�Z���z") == 0) {
							/*
							 * (����6)controlText�F�Z�� currentLine�F�Z��
							 *
							 * �Ή��FcontrolText���I�������Ɣ��f���A���̃��\�b�h�̎d���͏I���B
							 */
							return currentPos; // currentPos�́u-----�v���w���Ă���
						}
					} else {
						/* ���ہF�����X�^�C���ł͂Ȃ� */
						if ((styleControlText.getStyleName().compareTo("�y�\�z") == 0 && styleCurrentLine
								.getStyleName().compareTo("�y�s�z") == 0)
								|| (styleControlText.getStyleName().compareTo(
										"�y�s�z") == 0 && styleCurrentLine
										.getStyleName().compareTo("�y�Z���z") == 0)) {
							/*
							 * (����1)controlText�F�\ currentLine�F�s
							 *
							 * (����3)controlText�F�s currentLine�F�Z��
							 *
							 * �Ή��F�ʂ̃u���b�N������q�Ŏn�܂������Ƃɂ���
							 */
							/*
							 * nextControlText�̐���
							 */
							ControlText nextControlText = new ControlText(
									styleCurrentLine,
									styleCurrentLine.getStyleName());

							/* controlText�̎q���ɓo�^ */
							controlText.getChildList().add(nextControlText);

							// currentPos��==========��-----���w���Ă���̂ŁA���̍s����ϊ��J�n
							currentPos = compileBlock(nextControlText,
									smallPartText, currentPos + 1);

							continue; // currentPos�́u-----�v���u=========�v���u���v�̎��̍s���w���Ă���͂�
						} else if ((styleControlText.getStyleName().compareTo(
								"�y�Z���z") == 0 && styleCurrentLine.getStyleName()
								.compareTo("�y�\�z") == 0)) {
							/*
							 * (����4)controlText�F�Z�� currentLine�F�\
							 *
							 * �Ή��F����q�̕\�Ɖ��߂��A�V�����\�����
							 */
							/*
							 * nextControlText�̐���
							 */
							ControlText nextControlText = new ControlText(
									styleCurrentLine,
									styleCurrentLine.getStyleName());

							/* controlText�̎q���ɓo�^ */
							controlText.getChildList().add(nextControlText);

							// �y�\�z�̉��߂��n�߂�
							currentPos = compileBlock(nextControlText,
									smallPartText, currentPos);

							continue; // currentPos�́u���v�̎��̍s���w���Ă���͂�
						} else {
							/*
							 * (����5)controlText�F�Z�� currentLine�F�s
							 *
							 * �Ή��FcontrolText���I�������Ɣ��f���A���̃��\�b�h�̎d���͏I���B
							 */
							return currentPos; // currentPos�́u==========�v���w���Ă���
						}
					}
				} else {
					// ���ہF��L�ȊO
					// �Ή��F�ʂ̃u���b�N������q�Ŏn�܂������Ƃɂ���
					/*
					 * controlText�̐���
					 */
					ControlText nextControlText = new ControlText(
							styleCurrentLine, styleCurrentLine.getStyleName());

					/* controlText�̎q���ɓo�^ */
					controlText.getChildList().add(nextControlText);

					currentPos = compileBlock(nextControlText, smallPartText,
							currentPos);

					// compileBlock�ŁAsmallPartText��S���ǂ�łȂ��H
					if (currentPos < smallPartText.size()) {
						continue; // �S���ǂ�łȂ���I
					} else {
						break; // �Ō�܂œǂ񂶂�����E�E�E�H
					}
				}
				throw new IOException("�X�^�C������`���ꂽ�s�ɂ�������炸��������Ă��܂���");
			}

			/* �^�u�u���b�N�̊m�F */
			if (currentLine.length() > 0 && currentLine.charAt(0) == '\t') {
				// �^�u�����Ŏn�܂�s�͂��ׂ�tabPartText�ɓ����
				ArrayList<String> tabPartText = new ArrayList<String>();
				for (; currentPos < smallPartText.size(); currentPos++) {
					currentLine = smallPartText.get(currentPos);
					if (currentLine.length() > 0
							&& currentLine.charAt(0) == '\t')
						tabPartText.add(currentLine.substring(1));
					else
						break;
				}

				// �^�u�u���b�N��controlText
				ControlText tabBlockControlText = new ControlText(
						styleControlText, null);

				compileText(tabPartText, tabBlockControlText);
				childArrayControlText.add(tabBlockControlText);
				continue;
			}

			/* �u���b�N���I�����Ă��Ȃ����m�F */
			if (styleControlText != null
					&& styleControlText.isMatch_Last(currentLine)) {
				// ���̃u���b�N���I�������I
				if (styleControlText.bNoteLikeStyle()) {
					// ���`���^�C�v
					// �Ō��1�s�i���Ȃǁj��O�̂��ߓo�^����
					IntermediateText lastLineText = new IntermediateText(
							styleControlText, currentLine);
					childArrayControlText.add(lastLineText);
					return currentPos + 1; // currentPos�́u���v���w���Ă���
				} else if (styleControlText.bTableLikeStyle()) {
					// �\�̃^�C�v
					if (styleControlText.getStyleName().compareTo("�y�\�z") == 0) {
						// �Ō��1�s�i���Ȃǁj��O�̂��ߓo�^����
						IntermediateText lastLineText = new IntermediateText(
								styleControlText, currentLine);
						childArrayControlText.add(lastLineText);
						return currentPos + 1; // currentPos�́u���v���w���Ă���
					} else if (styleControlText.getStyleName().equals("�y�Z���z")) {
						return currentPos;
					} else {
						return currentPos;
					}
				}
				return currentPos; // currentPos�́A�I�������炩�ɂȂ����s���w���Ă���B�ʏ�͎��̃X�^�C���̊J�n�s�ł���
			}

			// �n�܂�ł��I���ł��Ȃ��Ƃ���i�܂�Ƃ��낽���̖{���j
			IntermediateText textBody = new IntermediateText(null, currentLine);
			childArrayControlText.add(textBody);
			currentPos++; // ���̍s�ցE�E�E
		}
		// ����O��it���Ȃ��Ȃ���������B�B�B
		if (styleControlText != null) {
			if (styleControlText.bNoteLikeStyle()
					|| styleControlText.bTableLikeStyle()) {
				Tx2x.appendWarn("�u���b�N���I������O�Ƀe�L�X�g���Ȃ��Ȃ�܂����B");
				throw new IOException("�u���b�N���I������O�Ƀe�L�X�g���Ȃ��Ȃ�܂����B\n"
						+ smallPartText);
			}
		}
		return smallPartText.size();
	}

	/* �o�̓e�X�g */
	// �u���b�N�𓝊�����IntermediateText��n������
	@SuppressWarnings("unused")
	private void outputResult(ControlText controlText,
			LinkedList<Style> ruleLinkedList) throws IOException {
		ArrayList<IntermediateText> childArrayList = controlText.getChildList();
		if (childArrayList == null) {
			throw new IOException(
					"�u���b�N�𓝊�����IntermediateText�ɁAchildArrayList���ݒ肳��Ă��Ȃ�");
		}
		Iterator<IntermediateText> it = childArrayList.iterator();
		while (it.hasNext()) {
			IntermediateText iText = it.next();
			if (iText.hasChild()) {
				// �q�������遁ControlText�ł���
				ControlText cText = (ControlText) iText;
				Style rb = cText.getStyle();
				ruleLinkedList.addLast(cText.getStyle());
				if (rb != null
						&& (rb.bBulletLikeStyle() || rb.bTableLikeStyle())
						&& cText.getText() != null) {
					// �ӏ������X�^�C���̏ꍇ�́A1�s�ڂ��u���b�N�𓝊�����IntermediateText���ێ����Ă���
					// �e�L�X�g�̏o��
					outputText(ruleLinkedList, cText);
				}
				outputResult(cText, ruleLinkedList);
				ruleLinkedList.removeLast();
			} else {
				// �q�������Ȃ���IntermediateText�ł���
				Style rb = (Style) iText.getStyle();
				if (rb != null) {
					// �X�^�C��������
					// �X�^�C�����v�b�V�����ăe�L�X�g���o��
					if (iText.getText() != null) {
						ruleLinkedList.addLast(rb);
						outputText(ruleLinkedList, iText);
						ruleLinkedList.removeLast();
					}
				} else {
					// �X�^�C�����Ȃ�
					// �e�L�X�g���o�͂���̂�
					if (iText.getText() != null) {
						outputText(ruleLinkedList, iText);
					}
				}
			}
		}
	}

	private void outputText(LinkedList<Style> ruleLinkedList,
			IntermediateText iText) {
		// String outputText = "";
		Iterator<Style> it = ruleLinkedList.iterator();
		while (it.hasNext()) {
			Style style = it.next();
			if (style == null) {
				// outputText += "�y�{���z";
			} else {
				// outputText += style.getStyleName();
			}
		}
		if (iText.getStyle() == null) {
			// System.out.println(outputText + "�y�{���z:" + iText.getText());
		} else {
			// System.out.println(outputText + ":" + iText.getText());
		}
	}
}
