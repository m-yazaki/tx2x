package tx2x.core;

public class Style_BulletLike extends Style {
	public Style_BulletLike(String styleName, String regexpFirst,
			String regexpLast) {
		super(styleName, regexpFirst, regexpLast);
	}

	public boolean bBulletLikeStyle() {
		return true;
	}
}
