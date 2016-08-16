package tx2x;

import java.awt.FlowLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.TransferHandler;

import tx2x.indesign.ConvertToInDesign;
import tx2x.word.ConvertToWord;
import tx2x.xhtml.ConvertToXHTML;

public class Tx2xGUI implements ActionListener {
	/**
	 * ドロップ操作の処理を行うクラス
	 */
	private class DropFileHandler extends TransferHandler {

		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * ドロップされたものを受け取るか判断 (ファイルのときだけ受け取る)
		 */
		@Override
		public boolean canImport(TransferSupport support) {
			if (!support.isDrop()) {
				// ドロップ操作でない場合は受け取らない
				return false;
			}

			if (!support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				// ドロップされたのがファイルでない場合は受け取らない
				return false;
			}

			return true;
		}

		/**
		 * ドロップされたファイルを受け取る
		 */
		@Override
		public boolean importData(TransferSupport support) {
			// 受け取っていいものか確認する
			if (!canImport(support)) {
				return false;
			}

			// ドロップ処理
			Transferable t = support.getTransferable();
			try {
				@SuppressWarnings("unchecked")
				List<File> files = (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);

				// テキストエリアにファイル名のリストを表示する
				if (files.size() > 0) {
					text_file.setText(files.get(0).toString());
				}
			} catch (UnsupportedFlavorException |

					IOException e) {
				e.printStackTrace();
			}
			return true;
		}
	}

	private JTextField text_file;
	private JButton button_ok;
	private JButton button_cancel;
	private JFrame frame;
	private JCheckBox checkbox_debug;
	private JComboBox<String> combo_mode;

	Tx2xGUI() {
		frame = new JFrame("Tx2x - DEMO");
		frame.setBounds(100, 100, 480, 110);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.setLayout(new FlowLayout(FlowLayout.LEFT));

		JPanel panel_file = new JPanel();
		panel_file.setLayout(new BoxLayout(panel_file, BoxLayout.LINE_AXIS));
		frame.add(panel_file);
		JLabel label_file = new JLabel("FILE: ");
		panel_file.add(label_file);
		text_file = new JTextField(20);
		text_file.setTransferHandler(new DropFileHandler());
		panel_file.add(text_file);

		JPanel panel_mode = new JPanel();
		panel_mode.setLayout(new BoxLayout(panel_mode, BoxLayout.LINE_AXIS));
		frame.add(panel_mode);
		JLabel label_mode = new JLabel("MODE: ");
		panel_mode.add(label_mode);
		String[] combo_data = { "HTML", "InDesign（Windows）", "Word（表示）", "Word（非表示）" };
		combo_mode = new JComboBox<String>(combo_data);
		panel_mode.add(combo_mode);

		checkbox_debug = new JCheckBox("DEBUG");
		frame.add(checkbox_debug);

		JPanel panel_buttons = new JPanel();
		panel_buttons.setLayout(new BoxLayout(panel_buttons, BoxLayout.LINE_AXIS));
		frame.add(panel_buttons);
		button_ok = new JButton("OK");
		button_cancel = new JButton("Cancel");
		panel_buttons.add(button_ok);
		panel_buttons.add(button_cancel);
		button_ok.addActionListener(this);
		button_cancel.addActionListener(this);
		frame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		if (e.getSource() == button_ok) {
			// オプションチェック
			if (checkbox_debug.isSelected())
				Tx2xOptions.getInstance().setOption("debug", true);
			String selectedItem = (String) combo_mode.getSelectedItem();
			if (selectedItem.equals("Word（表示）")) {
				Tx2xOptions.getInstance().setOption("mode", "Word");
				Tx2xOptions.getInstance().setOption("Visible", true);
			} else if (selectedItem.equals("Word（非表示）")) {
				Tx2xOptions.getInstance().setOption("mode", "Word");
				Tx2xOptions.getInstance().setOption("Visible", false);
			} else if (selectedItem.equals("InDesign（Mac）")) {
				Tx2xOptions.getInstance().setOption("mode", "InDesign-Macintosh");
			} else if (selectedItem.equals("InDesign（Windows）")) {
				Tx2xOptions.getInstance().setOption("mode", "InDesign-Windows");
			} else if (selectedItem.equals("HTML")) {
				Tx2xOptions.getInstance().setOption("mode", "HTML");
			}
			if (text_file.getText().equals("")) {
				JOptionPane.showMessageDialog(frame, "変換対象のファイルを指定してください。");
				return;
			}

			File temp = new File(text_file.getText());
			if (temp.exists()) {
				Tx2xOptions.getInstance().setOption("tx2x_folder_file_name", text_file.getText());
			} else {
				JOptionPane.showMessageDialog(frame, "変換対象のファイルが見つかりません。：" + temp.getAbsolutePath());
				return;
			}

			Converter cConverter;
			String mode = Tx2xOptions.getInstance().getString("mode");
			boolean bDebug = Tx2xOptions.getInstance().getBoolean("debug");
			if (mode.equals("Word")) {
				cConverter = new ConvertToWord();
			} else if (mode.equals("InDesign-Windows")) {
				cConverter = new ConvertToInDesign();
			} else if (mode.equals("HTML")) {
				cConverter = new ConvertToXHTML(bDebug);
			} else {
				JOptionPane.showMessageDialog(frame, "変換モードが不正です。");
				return;
			}
			File cFile = new File(Tx2xOptions.getInstance().getString("tx2x_folder_file_name"));
			if (cFile.exists()) {
				IgnoreFile cIgnoreFile = IgnoreFile.getInstance();
				if (cFile.isDirectory()) {
					Tx2xOptions.getInstance().setOption("tx2x_folder_name", cFile.getAbsolutePath());
					cIgnoreFile.setIgnoreFiles(new File(cFile.getAbsolutePath() + File.separator + "tx2x.ignore"));
				} else {
					Tx2xOptions.getInstance().setOption("tx2x_folder_name", cFile.getParentFile().getAbsolutePath());
				}
				try {
					cConverter.parse_filesystem(cFile);
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}

			// メッセージ出力
			String message = "-整形終了-" + Tx2x.getMessageCRLF();
			String warn = Tx2x.getWarn();
			if (warn.length() > 0) {
				message += warn;
			}
			System.out.println(message);
		}

		if (e.getSource() == button_cancel)

		{
			System.out.println("Cancel");
			frame.dispose();
		}
	}

}
