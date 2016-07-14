package jp.tolz.migrationutils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * CSVをきれいにしてTSVとして吐き出します。 問題のあるデータはログとして吐き出します。
 */

public class CSVFixer {
	private int numCol = -1;
	private File csvFile = null;
	private File exportDir = null;
	private BufferedReader csv = null;
	private OutputStreamWriter result;
	private OutputStreamWriter fixTsv;
	private OutputStreamWriter errRows;
	private String csvPath = null;
	private String resultPath = null;
	private String fixTsvPath = null;
	private String errRowsPath = null;

	/**
	 * 必要情報はすべてここでインスタンスに伝える必要があります。
	 * 
	 * @param numCol
	 *            整合性チェック対象のCSVのカラム数を指定します。
	 * @param csvFile
	 *            対象CSVを指定します。(new File("path/to/csv/target.csv"))
	 * @param exportDir
	 *            生成物は複数になりますので出力先をディレクトリとして指定します。(new File("export/dir/"))
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public CSVFixer(int numCol, File csvFile, File exportDir)
			throws FileNotFoundException, UnsupportedEncodingException {
		this.numCol = numCol;
		this.csvFile = csvFile;
		this.exportDir = exportDir;
		csvPath = csvFile.getPath();
		resultPath = exportDir.getPath() + "/" + csvFile.getName() + ".log";
		fixTsvPath = exportDir.getPath() + "/" + csvFile.getName() + ".tsv";
		errRowsPath = exportDir.getPath() + "/" + csvFile.getName() + "_ERROR" + ".csv";

		csv = new BufferedReader(new InputStreamReader(new FileInputStream(csvPath), "SJIS"));
		result = new OutputStreamWriter(new FileOutputStream(resultPath), "UTF-8");
		fixTsv = new OutputStreamWriter(new FileOutputStream(fixTsvPath), "UTF-8");
		errRows = new OutputStreamWriter(new FileOutputStream(errRowsPath), "UTF-8");
	}

	/**
	 * @return 正常な行のみを出力したファイルを示すパスを返却します。
	 */
	public String fixTsvPath() {
		return fixTsvPath;
	}

	/**
	 * 整合性のチェックおよび洗浄データ出力を開始します。 実行後に結果ファイルがexportDirで指定した先に生成されます。 簡易なログが画面に出力されます。
	 * 
	 * @throws IOException
	 */
	public void fix() throws IOException {
		System.out.println(csvPath + "の読み取りを開始します.");

		String line = null;
		int rowCount = 0;
		int incorrectDoubleQCount = 0;
		int warningDoubleQCount = 0;
		int warningSingleQCount = 0;
		int incorrectCommaCount = 0;
		int warningCommaCount = 0;

		int success = 0;
		int warning = 0;
		int error = 0;

		while ((line = csv.readLine()) != null) {
			rowCount++;
			// 各行の詳細を把握。
			int lineSize = line.length();
			String lineInfo = String.valueOf(rowCount) + "行目 : ";

			int comma = 0;
			int doubleQ = 0;
			int inDoubleComma = 0;
			boolean[] inDoubleCommas = new boolean[lineSize];
			boolean inDouble = false;
			int singleQ = 0;
			for (int i = 0; i < lineSize; i++) {
				char l = line.charAt(i);
				switch (l) {

				case '\'':
					singleQ++;
					break;

				case ',':
					comma++;
					if (inDouble) {
						inDoubleComma++;
						inDoubleCommas[i] = true;
					}
					break;

				case '"':
					inDouble = !inDouble;
					doubleQ++;
					break;

				}
			}

			// 各パターンに従って出力。
			if (singleQ > 0) {
				// 警告のみ。
				warningSingleQCount++;
				lineInfo += "[警告]シングルクオーテーションが使われています.";

			}

			if (comma == numCol - 1) {
				if (doubleQ % 2 == 0) {
					if (inDoubleComma == 0) {
						// 正常な行.
						success++;
						lineInfo = "";
						line = line.replace('"', ' ');
						String[] cells = line.split(",");
						fixTsv.write(cells[0].trim());
						for (int i = 1; i < cells.length; i++) {
							fixTsv.write("\t".concat(cells[i].trim()));
						}
						fixTsv.write('\n');
					} else {
						// 受理可能な行.
						warning++;
						warningDoubleQCount++;
						lineInfo += "[警告]カラム内でダブルクオーテーションの数が不正です.";
						String[] cells = line.split(",");
						int cellsSize = cells.length;

						cells[0] = cells[0].trim();
						if (cells[0].length() > 0) {
							if (cells[0].charAt(0) == '"' && cells[0].charAt(cells[0].length() - 1) == '"') {
								cells[0] = cells[0].substring(1, cells[0].length() - 1);
							}
							fixTsv.write(cells[0]);
						}
						for (int i = 1; i < cellsSize; i++) {
							cells[i] = cells[i].trim();
							if (cells[i].length() > 0) {
								if (cells[i].charAt(0) == '"' && cells[i].charAt(cells[i].length() - 1) == '"') {
									cells[i] = cells[i].substring(1, cells[i].length() - 1);
									cells[i] = cells[i].replace("\"", "''").trim();
								}
							}
							fixTsv.write('\t');
							fixTsv.write(cells[i]);
						}
						// fixTsv.write(line.replace(',', '\t'));
						fixTsv.write('\n');
					}

				} else {
					// 受理可能な行.
					warning++;
					warningDoubleQCount++;
					lineInfo += "[警告]ダブルクオーテーションの数が不正です.";
					lineInfo += String.valueOf(doubleQ) + "個のダブルクオートがあります.";
					// errRows.write(line);
					// errRows.write('\n');
					String[] cells = line.split(",");
					int cellsSize = cells.length;

					cells[0] = cells[0].trim();
					if (cells[0].length() > 0) {
						if (cells[0].charAt(0) == '"' && cells[0].charAt(cells[0].length() - 1) == '"') {
							cells[0] = cells[0].substring(1, cells[0].length() - 1);
						}
						fixTsv.write(cells[0]);
					}
					for (int i = 1; i < cellsSize; i++) {
						cells[i] = cells[i].trim();
						if (cells[i].length() > 0) {
							if (cells[i].charAt(0) == '"' && cells[i].charAt(cells[i].length() - 1) == '"') {
								cells[i] = cells[i].substring(1, cells[i].length() - 1);
								cells[i] = cells[i].replace("\"", "''").trim();
							}
						}
						fixTsv.write('\t');
						fixTsv.write(cells[i]);
					}
					// fixTsv.write(line.replace(',', '\t'));
					fixTsv.write('\n');
				}

			} else {
				if (comma - inDoubleComma == numCol - 1) {
					// 受理可能な行.
					warning++;
					warningCommaCount++;
					lineInfo += "[警告]カンマの数が不正です.";
					lineInfo += String.valueOf(comma) + "個のカンマがあります.";
					// errRows.write(line);
					// errRows.write('\n');
					// for (int i = 0; i < numCol - 1; i++) {
					// fixTsv.write('\t');
					// }
					// fixTsv.write('\n');
					String exCell = "";
					for (int i = 0; i < lineSize; i++) {
						if (line.charAt(i) == ',' && !inDoubleCommas[i]) {
							exCell = exCell.trim();
							if (exCell.length() == 0)
								continue;
							if (exCell.charAt(0) == '"' && exCell.charAt(exCell.length() - 1) == '"') {
								exCell = exCell.substring(1, exCell.length() - 1);
							}
							fixTsv.write(exCell);
							exCell = "";
							fixTsv.write('\t');
						} else {
							exCell = exCell.concat(String.valueOf(line.charAt(i)));
							continue;
						}
					}
					fixTsv.write('\n');
				} else {
					// 受理不可能な行.
					error++;
					incorrectCommaCount++;
					incorrectDoubleQCount++;
					lineInfo += "[拒否]カンマ数及びダブルクオートが不正です.受理できません.";
					lineInfo += String.valueOf(comma) + "個のカンマがあります.";
					lineInfo += String.valueOf(doubleQ) + "個のダブルクオートがあります.";
					for (int i = 0; i < numCol - 1; i++) {
						fixTsv.write('\t');
					}
					fixTsv.write('\n');
					errRows.write(line);
					errRows.write('\n');
				}

			}

			if (lineInfo.length() > 0) {
				result.write(lineInfo);
				result.write('\n');
			}

		}

		String fileInfo = "";
		fileInfo += csvPath + "の読み取りが完了しました.\n";
		fileInfo += fixTsvPath + "に修正されたデータを出力しました.\n";
		fileInfo += resultPath + "に詳しい読み取り結果を出力しました.\n";
		fileInfo += errRowsPath + "に拒否及び警告された行を出力しました。\n";
		fileInfo += "[集計]" + rowCount + "行が検出されました。\n";
		fileInfo += "[集計]" + success + "行の成功\n";
		fileInfo += "[集計]" + warning + "行の警告\n";
		fileInfo += "[集計]" + warningSingleQCount + "行のシングルクオーテーションに関する警告\n";
		fileInfo += "[集計]" + warningDoubleQCount + "行のダブルククオーテーションに関する警告\n";
		fileInfo += "[集計]" + warningCommaCount + "行のカンマに関する警告\n";
		fileInfo += "[集計]" + error + "行の拒否\n";
		fileInfo += "[集計]" + incorrectDoubleQCount + "行のダブルクオーテーションに関する拒否\n";
		fileInfo += "[集計]" + incorrectCommaCount + "行のカンマに関する拒否\n";
		result.write(fileInfo);
		System.out.println(fileInfo);
		fixTsv.close();
		errRows.close();
		result.close();
		if (error == 0)
			new File(errRowsPath).delete();
	}
}
