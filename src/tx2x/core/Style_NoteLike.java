package tx2x.core;

public class Style_NoteLike extends Style {

	public Style_NoteLike(String styleName, String regexpFirst,
			String regexpLast) {
		super(styleName, regexpFirst, regexpLast);
	}

	public boolean bNoteLikeStyle() {
		return true;
	}
}
