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
 * CSVFixer�ƃZ�b�g�ł��B
 * CSVFixer����o�͂��ꂽTSV�𗘗p���Ă��������B
 * DB�Ɏ捞�\��TSV���o�͂��܂��B
 * ���̃N���X�͍���RelationalTable�N���X�ɒu�������܂�.
 * ����ɃC���f�b�N���ǉ�����܂��B
 * �ŏI��ڍs��addColsAtLast���ǉ�����܂�.
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
	 * @param tsvFile �ϊ��Ώۂ�TSV�t�@�C��
	 * @param tsvNumCol TSV�̃J������
	 * @param exportDir �捞�pTSV�o�͐�
	 * @param addColsAtLast �Ō��ȍ~�ɒǉ��������z��
	 * @throws FileNotFoundException �t�@�C����O
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
	 * @param tsvFile �ϊ��Ώۂ�TSV�t�@�C��
	 * @param tsvNumCol TSV�̃J������
	 * @param exportDir �捞�pTSV�o�͐�
	 * @param addColsAtLast �Ō��ȍ~�ɒǉ��������z��
	 * @param startIndex �擪�̔ԍ�
	 * @throws FileNotFoundException �t�@�C����O
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
	 * TSV�o�͂��J�n���܂��B
	 * @throws IOException �t�@�C����O
	 */
	public void make() throws IOException {
		System.out.println(importableTsvPath + "�Ɏ捞�pTSV�o�͂��J�n���܂�.");
		importableTsv = new OutputStreamWriter(new FileOutputStream(importableTsvPath), "UTF-8");
		BufferedReader tsv = new BufferedReader(new InputStreamReader(new FileInputStream(tsvFile.getPath()), "UTF-8"));
		String line = null;
		// +1�͐擪��index��
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
		System.out.println(importableTsvPath + "�Ɏ捞�pTSV���o�͂��܂���.");
	}

}