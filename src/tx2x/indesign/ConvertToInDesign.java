package tx2x.indesign;

import java.io.File;
import java.io.IOException;

import tx2x.Converter;
import tx2x.IntermediateTextTreeBuilder;
import tx2x.Tx2xOptions;
import tx2x.core.ControlText;

public class ConvertToInDesign extends Converter {
	private String m_sMaker;
	private boolean m_bDebugMode;

	public ConvertToInDesign() {
		super();
		m_sMaker = Tx2xOptions.getInstance().getString("maker");
		m_bDebugMode = Tx2xOptions.getInstance().getBoolean("debug");
	}

	@Override
	public void convert(File cTargetFile) {
		String sInDesignOS = Tx2xOptions.getInstance().getString("mode");
		if (!m_bDebugMode) {
			System.out.println("==========" + sInDesignOS
					+ "用テキストを出力します==========");
		} else {
			System.out.println("==========" + sInDesignOS
					+ "用テキストを出力します（DEBUG）==========");
		}

		try {
			// iTextツリーを作成
			IntermediateTextTreeBuilder ciTextTreeBuilder = new IntermediateTextTreeBuilder(
					m_bDebugMode);
			ControlText resultRootText = ciTextTreeBuilder
					.parse_file(cTargetFile);

			// 出力ファイル名の決定
			String sTextFilename = cTargetFile.getAbsolutePath();
			String sOutputFilename = "NG.txt";

			// Windows用InDesignタグ付きテキスト
			sOutputFilename = sTextFilename.replaceFirst(".[Tt][Xx][Tt]$",
					".win.indesign.txt");
			if (sTextFilename.equals(sOutputFilename)) {
				System.out.println("上書きされるため中止しました。ファイル名を確認してください。");
			} else {
				// 出力
				File cOutputFile = new File(sOutputFilename);
				IntermediateTextTreeToInDesign ciTextTreeToInDesign = new IntermediateTextTreeToInDesign(
						false, m_sMaker, m_bDebugMode); // Macintosh用のテキストを出力する場合は、Windows用も出力する
				ciTextTreeToInDesign.output(cOutputFile, resultRootText);
			}

			// inddファイルのコピー
			if (cTargetFile.exists()) {
				try {
					copyFile(cTargetFile.getParent()
							+ "\\Tx2xTemplate.indesign.indd",
							removeFileExtension(cTargetFile.getName())
									+ ".indd");
					System.out.println("InDesignファイル："
							+ removeFileExtension(cTargetFile.getName())
							+ ".indd");
				} catch (IOException e2) {
					// TODO 自動生成された catch ブロック
					e2.printStackTrace();
				}
			}

			// Macintosh用フラグが立っている時は、Macintosh用も出力する
			if (sInDesignOS.equals("InDesign-Macintosh")) {
				sOutputFilename = sTextFilename.replaceFirst(".[Tt][Xx][Tt]$",
						".mac.indesign.txt");
				if (sTextFilename.equals(sOutputFilename)) {
					System.out.println("上書きされるため中止しました。ファイル名を確認してください。");
				} else {
					File cOutputFile = new File(sOutputFilename);
					IntermediateTextTreeToInDesign ciTextTreeToInDesign = new IntermediateTextTreeToInDesign(
							true, m_sMaker, m_bDebugMode);
					ciTextTreeToInDesign.output(cOutputFile, resultRootText);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
