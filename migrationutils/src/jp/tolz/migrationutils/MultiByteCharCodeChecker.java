package jp.tolz.migrationutils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.bind.DatatypeConverter;

import au.com.bytecode.opencsv.CSVReader;

/**
 * 特殊文字や文字化けを起こす文字をチェックします。 現在のところとりあえずSJISのみ対応。SJISにあってUTF8にないものは文字化け対象。<br>
 * 参考文献<br>
 * @see <a href="http://ash.jp/code/unitbl21.htm">JIS第一水準漢字・文字コード表</a>
 * @see <a href="http://ash.jp/code/unitbl22.htm">JIS第二水準漢字・文字コード表</a>
 */
public class MultiByteCharCodeChecker {
	private File file;
	private File codeFile;
	private HashMap<String, String> SJISToUTF8 = new HashMap<String, String>();

	/**
	 * ファイルのパスを設定します。
	 * 
	 * @param file
	 *            チェック対象のファイル
	 * @param codeTable
	 *            文字コード対応表(サンプルはjp.tolz.migrationutils.Unicode.csv)
	 */
	public MultiByteCharCodeChecker(File file, File codeTable) {
		this.file = file;
		this.codeFile = codeTable;
	}

	private void createCodeMap() {
		CSVReader reader;
		try {
			reader = new CSVReader(new BufferedReader(new FileReader(codeFile.getPath())));
			String[] row;
			while ((row = reader.readNext()) != null) {
				SJISToUTF8.put(row[3], row[5]);
			}
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * ファイルのチェックを開始します。結果はすべてコンソールに表示されます。
	 */
	public void check() {
		createCodeMap();
		int error = 0;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file.getPath()));
			String s;
			int rowi = 0;

			while ((s = reader.readLine()) != null) {
				rowi++;
				int length = s.length();
				for (int i = 0; i < length; i++) {
					byte[] c = (byte[]) s.substring(i, i + 1).getBytes();
					String b = DatatypeConverter.printHexBinary(c);
					if (b.length() == 2)
						continue;

					if (!SJISToUTF8.containsKey(b)) {
						System.out.println((rowi) + "行目 : コード表にない文字です。文字コード: 0x" + b);
						error++;
						continue;
					}

					if (SJISToUTF8.get(b).equals("------")) {
						System.out.println((rowi) + "行目 : 文字化けする可能性のある文字です。文字コード: 0x" + b);
						error++;
						continue;
					}
				}
			}
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(error + "件の異常が発見されました。");
	}

}