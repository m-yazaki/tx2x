package tx2x.core;

public class Style_Table extends Style {
	public Style_Table() {
		super("【表】", "^▼表.*", "▲表?");
	}

	@Override
	public boolean bTableLikeStyle() {
		return true;
	}
}
