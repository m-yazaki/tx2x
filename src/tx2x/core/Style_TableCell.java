package tx2x.core;

public class Style_TableCell extends Style_Table {

	public Style_TableCell() {
		super("【セル】", "^-----.*", "^(-----.*|=====.*|▲表?)");
	}
}
