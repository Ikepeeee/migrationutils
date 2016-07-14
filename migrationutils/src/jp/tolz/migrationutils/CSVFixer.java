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
 * CSV�����ꂢ�ɂ���TSV�Ƃ��ēf���o���܂��B ���̂���f�[�^�̓��O�Ƃ��ēf���o���܂��B
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
	 * �K�v���͂��ׂĂ����ŃC���X�^���X�ɓ`����K�v������܂��B
	 * 
	 * @param numCol
	 *            �������`�F�b�N�Ώۂ�CSV�̃J���������w�肵�܂��B
	 * @param csvFile
	 *            �Ώ�CSV���w�肵�܂��B(new File("path/to/csv/target.csv"))
	 * @param exportDir
	 *            �������͕����ɂȂ�܂��̂ŏo�͐���f�B���N�g���Ƃ��Ďw�肵�܂��B(new File("export/dir/"))
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
	 * @return ����ȍs�݂̂��o�͂����t�@�C���������p�X��ԋp���܂��B
	 */
	public String fixTsvPath() {
		return fixTsvPath;
	}

	/**
	 * �������̃`�F�b�N����ѐ��f�[�^�o�͂��J�n���܂��B ���s��Ɍ��ʃt�@�C����exportDir�Ŏw�肵����ɐ�������܂��B �ȈՂȃ��O����ʂɏo�͂���܂��B
	 * 
	 * @throws IOException
	 */
	public void fix() throws IOException {
		System.out.println(csvPath + "�̓ǂݎ����J�n���܂�.");

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
			// �e�s�̏ڍׂ�c���B
			int lineSize = line.length();
			String lineInfo = String.valueOf(rowCount) + "�s�� : ";

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

			// �e�p�^�[���ɏ]���ďo�́B
			if (singleQ > 0) {
				// �x���̂݁B
				warningSingleQCount++;
				lineInfo += "[�x��]�V���O���N�I�[�e�[�V�������g���Ă��܂�.";

			}

			if (comma == numCol - 1) {
				if (doubleQ % 2 == 0) {
					if (inDoubleComma == 0) {
						// ����ȍs.
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
						// �󗝉\�ȍs.
						warning++;
						warningDoubleQCount++;
						lineInfo += "[�x��]�J�������Ń_�u���N�I�[�e�[�V�����̐����s���ł�.";
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
					// �󗝉\�ȍs.
					warning++;
					warningDoubleQCount++;
					lineInfo += "[�x��]�_�u���N�I�[�e�[�V�����̐����s���ł�.";
					lineInfo += String.valueOf(doubleQ) + "�̃_�u���N�I�[�g������܂�.";
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
					// �󗝉\�ȍs.
					warning++;
					warningCommaCount++;
					lineInfo += "[�x��]�J���}�̐����s���ł�.";
					lineInfo += String.valueOf(comma) + "�̃J���}������܂�.";
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
					// �󗝕s�\�ȍs.
					error++;
					incorrectCommaCount++;
					incorrectDoubleQCount++;
					lineInfo += "[����]�J���}���y�у_�u���N�I�[�g���s���ł�.�󗝂ł��܂���.";
					lineInfo += String.valueOf(comma) + "�̃J���}������܂�.";
					lineInfo += String.valueOf(doubleQ) + "�̃_�u���N�I�[�g������܂�.";
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
		fileInfo += csvPath + "�̓ǂݎ�肪�������܂���.\n";
		fileInfo += fixTsvPath + "�ɏC�����ꂽ�f�[�^���o�͂��܂���.\n";
		fileInfo += resultPath + "�ɏڂ����ǂݎ�茋�ʂ��o�͂��܂���.\n";
		fileInfo += errRowsPath + "�ɋ��ۋy�ьx�����ꂽ�s���o�͂��܂����B\n";
		fileInfo += "[�W�v]" + rowCount + "�s�����o����܂����B\n";
		fileInfo += "[�W�v]" + success + "�s�̐���\n";
		fileInfo += "[�W�v]" + warning + "�s�̌x��\n";
		fileInfo += "[�W�v]" + warningSingleQCount + "�s�̃V���O���N�I�[�e�[�V�����Ɋւ���x��\n";
		fileInfo += "[�W�v]" + warningDoubleQCount + "�s�̃_�u���N�N�I�[�e�[�V�����Ɋւ���x��\n";
		fileInfo += "[�W�v]" + warningCommaCount + "�s�̃J���}�Ɋւ���x��\n";
		fileInfo += "[�W�v]" + error + "�s�̋���\n";
		fileInfo += "[�W�v]" + incorrectDoubleQCount + "�s�̃_�u���N�I�[�e�[�V�����Ɋւ��鋑��\n";
		fileInfo += "[�W�v]" + incorrectCommaCount + "�s�̃J���}�Ɋւ��鋑��\n";
		result.write(fileInfo);
		System.out.println(fileInfo);
		fixTsv.close();
		errRows.close();
		result.close();
		if (error == 0)
			new File(errRowsPath).delete();
	}
}
