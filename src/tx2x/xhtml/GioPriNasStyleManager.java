package tx2x.xhtml;

/**
 * GIO-PRI/NAS用のStyleManager
 */
import tx2x.core.Style_BulletLike;

public class GioPriNasStyleManager extends tx2x.StyleManager {
	GioPriNasStyleManager() {
		super.defineStyles();
		m_cStyle_BulletLike_List.add(new Style_BulletLike("【編】", "^[A-Z]：.*", ".*")); // 【編】
		m_cStyle_BulletLike_List.add(new Style_BulletLike("【節】", "^[0-9]+\\.[0-9]+ .*", ".*")); // 【節】
		m_cStyle_BulletLike_List.add(new Style_BulletLike("【：】", "^：[^：].*", "^[^：].*")); // 【：】
		super.packToStyleList();
	}
}
