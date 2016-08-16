package tx2x.xhtml;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

import tx2x.Converter;
import tx2x.Tx2xOptions;
import tx2x.core.ControlText;

public class ConvertToXHTML extends Converter {
	private boolean m_bDebugMode;
	static File m_cTargetFile;

	public ConvertToXHTML() {
		super();
		m_bDebugMode = Tx2xOptions.getInstance().getBoolean("debug");
	}

	@Override
	public void convert(File cTargetFile) {
		if (!m_bDebugMode) {
			// System.out.println("■XHTMLの出力...");
		} else {
			System.out.println("■XHTML用テキストの出力（DEBUG）...");
		}

		try {
			// iTextツリーを作成
			IntermediateTextTreeBuilderXHTML ciTextTreeBuilder = new IntermediateTextTreeBuilderXHTML(m_bDebugMode);
			NavPointManager cNavPointManager = new NavPointManager();
			LongStyleManagerXHTML lsManager = new LongStyleManagerXHTML(m_bDebugMode, cTargetFile, cNavPointManager);
			ControlText resultRootText = ciTextTreeBuilder.parse_file(cTargetFile, lsManager);
			// これ以降、resultRootTextはFixされていると仮定する
			IntermediateTextTreeWalker cTreeWalker = new IntermediateTextTreeWalker(resultRootText);

			// 出力ファイル名の決定
			String sTextFilename = cTargetFile.getAbsolutePath();
			String sOutputFilename = sTextFilename.replaceFirst(".[Tt][Xx][Tt]$", ".html"); // XHTML

			if (sTextFilename.equals(sOutputFilename)) {
				System.out.println("上書きされるため中止しました。ファイル名を確認してください。");
			} else {
				// 出力
				File cOutputFile = new File(sOutputFilename);
				IntermediateTextTreeToXHTML ciTextTreeToXHTML = new IntermediateTextTreeToXHTML(true, m_bDebugMode,
						cNavPointManager);
				ciTextTreeToXHTML.output(cOutputFile, resultRootText, lsManager, cTreeWalker);
				checkXHTMLFile(cOutputFile);

			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (IntermediateTextTreeBuildException e) {
			e.printStackTrace();
		}
		if (!m_bDebugMode) {
		} else {
			System.out.println("done.");
		}
	}

	// XHTMLファイルの構文チェック
	private void checkXHTMLFile(File cOutputFile) {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			// DTDの検証を省略する設定
			factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			SAXParser parser;
			parser = factory.newSAXParser();
			DefaultHandler2 handler = new DefaultHandler2();
			parser.parse(cOutputFile, handler);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			System.out.println("【警告】" + e.toString().replaceAll(";", ";\n"));
			// e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setup(File cFile) {
		GioPriNasMenuManager cMenuManager = GioPriNasMenuManager.getInstance();
		File cMenuHtml;
		if (cFile.isDirectory()) {
			cMenuHtml = new File(cFile.getAbsolutePath() + "\\menu.html");
		} else {
			cMenuHtml = new File(cFile.getParent() + "\\menu.html");
		}
		System.out.println("==========目次ファイル（menu.html）を読み込みます==========");
		System.out.println(cMenuHtml.getAbsolutePath());
		cMenuManager.readMenuHtml(cMenuHtml);
	}
}
