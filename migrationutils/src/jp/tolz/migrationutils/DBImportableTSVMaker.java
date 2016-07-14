package jp.tolz.migrationutils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * CSVFixerとセットです。
 * CSVFixerから出力されたTSVを利用してください。
 * DBに取込可能なTSVを出力します。
 * このクラスは今後RelationalTableクラスに置き換わります.
 * 第一列にインデックが追加されます。
 * 最終列移行にaddColsAtLastが追加されます.
 */

public class DBImportableTSVMaker {
	private File tsvFile = null;
	private File exportDir = null;
	private String[] addColsAtLast = null;
	private int tsvNumCol;
	private int index = 0;

	private String importableTsvPath = null;
	private OutputStreamWriter importableTsv = null;

	/**
	 * @param tsvFile 変換対象のTSVファイル
	 * @param tsvNumCol TSVのカラム数
	 * @param exportDir 取込用TSV出力先
	 * @param addColsAtLast 最後列以降に追加したい配列
	 * @throws FileNotFoundException ファイル例外
	 */
	public DBImportableTSVMaker(File tsvFile, int tsvNumCol, File exportDir, String[] addColsAtLast)
			throws FileNotFoundException {
		this.tsvFile = tsvFile;
		this.tsvNumCol = tsvNumCol;
		this.exportDir = exportDir;
		this.addColsAtLast = addColsAtLast;

		importableTsvPath = exportDir.getPath() + "/" + tsvFile.getName() + "ForImport.tsv";

	}
	
	/**
	 * @param tsvFile 変換対象のTSVファイル
	 * @param tsvNumCol TSVのカラム数
	 * @param exportDir 取込用TSV出力先
	 * @param addColsAtLast 最後列以降に追加したい配列
	 * @param startIndex 先頭の番号
	 * @throws FileNotFoundException ファイル例外
	 */
	public DBImportableTSVMaker(File tsvFile, int tsvNumCol, File exportDir, String[] addColsAtLast, int startIndex)
			throws FileNotFoundException {
		this.tsvFile = tsvFile;
		this.tsvNumCol = tsvNumCol;
		this.exportDir = exportDir;
		this.addColsAtLast = addColsAtLast;
		this.index = startIndex + 1;
		importableTsvPath = exportDir.getPath() + "/" + tsvFile.getName() + "ForImport.tsv";

	}

	/**
	 * TSV出力を開始します。
	 * @throws IOException ファイル例外
	 */
	public void make() throws IOException {
		System.out.println(importableTsvPath + "に取込用TSV出力を開始します.");
		importableTsv = new OutputStreamWriter(new FileOutputStream(importableTsvPath), "UTF-8");
		BufferedReader tsv = new BufferedReader(new InputStreamReader(new FileInputStream(tsvFile.getPath()), "UTF-8"));
		String line = null;
		// +1は先頭のindex分
		int exLineSize = tsvNumCol + addColsAtLast.length + 1;
		while ((line = tsv.readLine()) != null) {
			index--;
			importableTsv.write(String.valueOf(index));
			importableTsv.write('\t');
			importableTsv.write(line);

			for (String col : addColsAtLast) {
				importableTsv.write('\t');
				importableTsv.write(col);
			}
			importableTsv.write('\n');
		}
		importableTsv.close();
		tsv.close();
		System.out.println(importableTsvPath + "に取込用TSVを出力しました.");
	}

}