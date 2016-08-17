package tx2x;

/*
 * プラグインからStyleManagerを登録してもらって、どこでも自由に使えるようにするためのクラス
 */
public class StyleManagerFactory {
	static StyleManagerFactory instance = null;
	private StyleManager m_cStyleManager;

	public static StyleManagerFactory getInstance() {
		if (instance == null) {
			instance = new StyleManagerFactory();
		}
		return instance;
	}

	public void regist(StyleManager cStyleManager) {
		m_cStyleManager = cStyleManager;
	}

	public StyleManager getStyleManager() {
		return m_cStyleManager;
	}
}
