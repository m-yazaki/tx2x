package tx2x;

import java.util.ArrayList;
import java.util.Iterator;

import tx2x_core.Style;
import tx2x_core.Style_BulletLike;
import tx2x_core.Style_NoteLike;
import tx2x_core.Style_Table;
import tx2x_core.Style_TableCell;
import tx2x_core.Style_TableCellHeader;
import tx2x_core.Style_TableRow;

public class StyleManager {
	private static StyleManager instance = new StyleManager();
	private ArrayList<Style> m_cStyleList;

	private StyleManager() {
		// StyleManager��Style��o�^�Z��
		m_cStyleList = new ArrayList<Style>();

		// �ӏ������̗ގ��i
		m_cStyleList.add(new Style_BulletLike("�y�ӏ������E�z", "^�E\t.*", "^[^�E].*")); // �ӏ������u�E�v
		m_cStyleList.add(new Style_BulletLike("�y�ӏ������|�z", "^�|\t.*", "^[^�|].*")); // �ӏ������u�|�v
		m_cStyleList.add(new Style_BulletLike("�y�ӏ��������z", "^��\t.*", "^[^��].*")); // �ӏ������u���v
		m_cStyleList.add(new Style_BulletLike("�y�ӏ��������z", "^��\t.*", "^[^��].*")); // �ӏ������u���v
		m_cStyleList.add(new Style_BulletLike("�y���z", "^��\t.*", "^[^��].*")); // ��
		m_cStyleList.add(new Style_BulletLike("�y*�z", "^\\*+\t.*", "^[^\\*].*")); // *
		m_cStyleList
				.add(new Style_BulletLike("�y��0�z", "^��[0-9]\t.*", "^[^��].*")); // ��[0-9]
		m_cStyleList.add(new Style_BulletLike("�y���E�z", "^���E\t.*", "^[^��].*")); // ���E
		m_cStyleList.add(new Style_BulletLike("�y�菇�z", "^[�O-�X]+\t.*",
				"^[^�O-�X].*")); // �菇
		m_cStyleList.add(new Style_BulletLike("�y�菇����z", "^��[^��].*", "^[^��].*")); // �菇����
		m_cStyleList.add(new Style_BulletLike("�y�@�z",
				"^[�@�A�B�C�D�E�F�G�H�I�J�K�L�M�N�O�P�Q�R�S]\t.*", "[^�@�A�B�C�D�E�F�G�H�I�J�K�L�M�N�O�P�Q�R�S].*")); // �@�`�S
		m_cStyleList.add(new Style_BulletLike("�y�@�F�z",
				"^[�@�A�B�C�D�E�F�G�H�I�J�K�L�M�N�O�P�Q�R�S]�F\t.*", "[^�@�A�B�C�D�E�F�G�H�I�J�K�L�M�N�O�P�Q�R�S].*")); // �@�F�`�S�F
		m_cStyleList.add(new Style_BulletLike("�y�iM�j�@�z",
				"^�iM�j[�@�A�B�C�D�E�F�G�H�I�J�K�L�M�N�O�P�Q�R�S]\t.*", "(^�iM�j).*"));
		m_cStyleList.add(new Style_BulletLike("�y�iM�j�z", "^�iM�j\t.*", "(^�iM�j).*"));
		m_cStyleList.add(new Style_BulletLike("�y�L�[�����z", "^�y.*�L�[�z(�i.+�j)?�F.*",
				"^[^�y]].*")); // �L�[�����B�I���͂����ԓK���c�B
		m_cStyleList.add(new Style_BulletLike("�y���p�����_��i1�j�z", "^�i[0-9]+�j\t.*",
				"^[^�i].*")); // ���p�����_��
		m_cStyleList.add(new Style_BulletLike("�y1.�z", "^[0-9]+\\.\t.*",
				"^[^0-9].*")); // 1.
		m_cStyleList
				.add(new Style_BulletLike("�y1�z", "^[0-9]+\t.*", "^[^0-9].*")); // 1.
		m_cStyleList.add(new Style_BulletLike("�yStep 1�z", "^Step [0-9]+\t.*",
				"^(?!Step [0-9]\t).*")); // Step 1.

		// �ǂ��炩�Ƃ����Ɖӏ������̗ގ��i
		m_cStyleList.add(new Style_BulletLike("�y���z", "^�y���z.*", ".*")); // �y���z
		m_cStyleList.add(new Style_BulletLike("�y��2�z", "^�y��2�z.*", ".*")); // �y��2�z
		m_cStyleList.add(new Style_BulletLike("�y�����z", "^�y�����z.*", ".*")); // �y�����z
		m_cStyleList.add(new Style_BulletLike("�y�߁z", "^�y�߁z.*", ".*")); // �y�߁z
		m_cStyleList.add(new Style_BulletLike("�y��2�z", "^�y��2�z.*", ".*")); // �y��2�z
		m_cStyleList.add(new Style_BulletLike("�y�́z", "^�y�́z.*", ".*")); // �y�́z
		m_cStyleList.add(new Style_BulletLike("�y�ҁz", "^�y�ҁz.*", ".*")); // �y�ҁz
		m_cStyleList.add(new Style_BulletLike("�y��F�z", "^��F.*", ".*")); // �y��F�z
		m_cStyleList.add(new Style_BulletLike("�y��ʁz", "^�y���.*�z.*", ".*")); // �y��ʁz
		m_cStyleList.add(new Style_BulletLike("�y�Q�Ɓz", "^��P.[��0-9]+�u.*�v", ".*")); // �y�Q�Ɓz
		m_cStyleList.add(new Style_BulletLike("�y�ȏ�z", "^�|�ȁ@��|", ".*"));
		m_cStyleList.add(new Style_BulletLike("�y�ʎ��^�C�g���z", "^�����������ʎ�.*", ".*"));
		m_cStyleList.add(new Style_BulletLike("�y�댯�z", "^���댯��\t.*", ".*"));
		m_cStyleList.add(new Style_BulletLike("�y�x���z", "^���x����\t.*", ".*"));
		m_cStyleList.add(new Style_BulletLike("�y���Ӂz", "^�����Ӂ�\t.*", ".*"));

		// ���m�点�̗ގ��i
		m_cStyleList.add(new Style_NoteLike("�y�����z", "^������.*", "��.*")); // memo
		m_cStyleList.add(new Style_NoteLike("�y��ʈ͂݁z", "^����ʈ͂�.*", "��.*")); // ��ʈ͂�
		m_cStyleList.add(new Style_NoteLike("�y�t�^�z", "^���t�^.*", "��.*")); // �t�^
		m_cStyleList.add(new Style_NoteLike("�y���p�����z", "^�����p����.*", "��.*")); // ���p����
		m_cStyleList.add(new Style_NoteLike("�yEng�z", "^��Eng.*", "��.*")); // Eng
		m_cStyleList.add(new Style_NoteLike("�y�Җڎ��z", "^���Җڎ�.*", "��.*")); // �Җڎ�
		m_cStyleList.add(new Style_NoteLike("�y�ڎ��z", "^���ڎ�.*", "��.*")); // �Җڎ�
		m_cStyleList.add(new Style_NoteLike("�y�����z", "^������.*", "��.*")); // ����
		m_cStyleList.add(new Style_NoteLike("�y���S��̂����Ӂz", "^�����S��̂�����.*", "��.*")); // ���S��̂�����

		// �\�g�֘A
		m_cStyleList.add(new Style_Table()); // �\�g��
		m_cStyleList.add(new Style_TableCell()); // �Z��
		m_cStyleList.add(new Style_TableCellHeader()); // �Z���F�w�b�_�[
		m_cStyleList.add(new Style_TableRow()); // �s
	}

	public static StyleManager getInstance() {
		return instance;
	}

	/*
	 * �X�^�C���̎n�܂�s���ǂ������m�F���郁�\�b�h�B �n�܂�s�ł������ꍇ�́AStyle��ԋp
	 */
	public Style getMatchStyle_Start(String line) {
		Iterator<Style> it = m_cStyleList.iterator();
		while (it.hasNext()) {
			Style style = it.next();
			if (style.isMatch_Start(line)) {
				return style;
			}
		}
		return null;
	}

	/*
	 * �X�^�C���̏I���s���ǂ������m�F���郁�\�b�h�B �I���s�ł������ꍇ�́AStyle��ԋp
	 */
	public Style getMatchStyle_Last(String line) {
		Iterator<Style> it = m_cStyleList.iterator();
		while (it.hasNext()) {
			Style style = it.next();
			if (style.isMatch_Last(line)) {
				return style;
			}
		}
		return null;
	}

	/*
	 * �X�^�C�����̂���Style���擾����
	 */
	public Style getStyle(String styleName) {
		Iterator<Style> it = m_cStyleList.iterator();
		while (it.hasNext()) {
			Style style = it.next();
			if (style.getStyleName().equals(styleName)) {
				return style;
			}
		}
		return null;
	}
}
