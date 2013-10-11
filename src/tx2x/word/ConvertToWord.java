package tx2x.word;

import java.io.File;
import java.io.IOException;

import tx2x.Converter;
import tx2x.IntermediateTextTreeBuilder;
import tx2x.Tx2xOptions;
import tx2x.core.ControlText;

public class ConvertToWord extends Converter {
	private boolean m_bDebugMode;

	public ConvertToWord() {
		super();
		m_bDebugMode = Tx2xOptions.getInstance().getBoolean("debug");
	}

	@Override
	public void convert(File cTargetFile) {
		System.out.println("==========Wordデータを出力します==========");
		// System.out.println(System.currentTimeMillis());

		ControlText resultRootText;
		try {
			// iTextツリーを作成
			IntermediateTextTreeBuilder ciTextTreeBuilder = new IntermediateTextTreeBuilder(
					m_bDebugMode);
			resultRootText = ciTextTreeBuilder.parse_file(cTargetFile);

			// 出力ファイル名の決定
			String sTextFilename = cTargetFile.getAbsolutePath();
			String sOutputFilename = "NG.docx";

			// Word（実行するためにはWordがインストールされている必要があります）
			sOutputFilename = sTextFilename.replaceFirst(".[Tt][Xx][Tt]$",
					".docx");
			if (sTextFilename.equals(sOutputFilename)) {
				System.out.println("上書きされるため中止しました。ファイル名を確認してください。");
			} else {
				// 出力
				File cOutputFile = new File(sOutputFilename);
				IntermediateTextTreeToWord ciTextTreeToWord = new IntermediateTextTreeToWord(
						m_bDebugMode);
				ciTextTreeToWord.output(cOutputFile, resultRootText);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		// System.out.println(System.currentTimeMillis());
	}
}
