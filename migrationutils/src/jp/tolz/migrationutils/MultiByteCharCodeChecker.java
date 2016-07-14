package jp.tolz.migrationutils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.bind.DatatypeConverter;

import au.com.bytecode.opencsv.CSVReader;

/**
 * ���ꕶ���╶���������N�����������`�F�b�N���܂��B ���݂̂Ƃ���Ƃ肠����SJIS�̂ݑΉ��BSJIS�ɂ�����UTF8�ɂȂ����͕̂��������ΏہB<br>
 * �Q�l����<br>
 * @see <a href="http://ash.jp/code/unitbl21.htm">JIS��ꐅ�������E�����R�[�h�\</a>
 * @see <a href="http://ash.jp/code/unitbl22.htm">JIS��񐅏������E�����R�[�h�\</a>
 */
public class MultiByteCharCodeChecker {
	private File file;
	private File codeFile;
	private HashMap<String, String> SJISToUTF8 = new HashMap<String, String>();

	/**
	 * �t�@�C���̃p�X��ݒ肵�܂��B
	 * 
	 * @param file
	 *            �`�F�b�N�Ώۂ̃t�@�C��
	 * @param codeTable
	 *            �����R�[�h�Ή��\(�T���v����jp.tolz.migrationutils.Unicode.csv)
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
	 * �t�@�C���̃`�F�b�N���J�n���܂��B���ʂ͂��ׂăR���\�[���ɕ\������܂��B
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
						System.out.println((rowi) + "�s�� : �R�[�h�\�ɂȂ������ł��B�����R�[�h: 0x" + b);
						error++;
						continue;
					}

					if (SJISToUTF8.get(b).equals("------")) {
						System.out.println((rowi) + "�s�� : ������������\���̂��镶���ł��B�����R�[�h: 0x" + b);
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
		System.out.println(error + "���ُ̈킪��������܂����B");
	}

}