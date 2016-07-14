package jp.tolz.migrationutils;

/**
 * @author �r�c ��
 * @version 1.0
 * BD�̃e�[�u���I�f�[�^�\�����������߂̃N���X�ł��B
 * �e��W�����Z���T�|�[�g���܂��B
 * condition�͓Ǝ��ɍ\����͂���true��false�����肷������N���X�ł��B
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.math.NumberUtils;

public class RTable {
	private String name;
	private ArrayList<LinkedHashMap<String, String>> tableHash;
	private String[] columns;

	/**
	 * �J���������L�[�Ƃ����̓n�b�V�������X�g�ɂ������̂���C���X�^���X���쐬���܂��B
	 */
	public RTable(ArrayList<LinkedHashMap<String, String>> tbl) {
		try {
			this.tableHash = (ArrayList<LinkedHashMap<String, String>>) copy(tbl);
			if (this.tableHash == null)
				this.tableHash = new ArrayList<LinkedHashMap<String, String>>();
		} catch (Exception e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
		if (tableHash.size() == 0)
			return;
		int size = tableHash.get(0).keySet().size();
		columns = new String[size];
		int i = 0;
		for (String col : tableHash.get(0).keySet()) {
			columns[i] = col;
			i++;
		}
	}

	/**
	 * �w�b�_�t��2�����z�񂩂�C���X�^���X�𐶐����܂��B
	 */
	public RTable(String[][] tbl) {
		tableHash = new ArrayList<LinkedHashMap<String, String>>();
		int colIndex = 0;
		int startRow = 1;
		int endRow = tbl.length - 1;
		int startCol = 0;
		int endCol = tbl[colIndex].length - 1;
		columns = new String[endCol - startCol + 1];
		for (int i = startCol; i <= endCol; i++) {
			columns[i] = tbl[colIndex][i];
		}
		for (int i = startRow; i <= endRow; i++) {
			LinkedHashMap<String, String> row = new LinkedHashMap<String, String>();
			for (int j = startCol; j <= endCol; j++) {
				row.put(tbl[colIndex][j], tbl[i][j]);
			}
			tableHash.add(row);
		}
	}

	/**
	 * �R�s�[�R���X�g���N�^�B�f�B�[�v�R�s�[���Ƃ�܂��B
	 */
	public RTable(RTable table) {
		try {
			RTable cp = (RTable) copy(table);
			name = cp.name;
			tableHash = cp.tableHash;
			columns = cp.columns;
		} catch (Exception e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
	}

	/**
	 * �C�ӂ̃I�u�W�F�N�g�̃f�B�[�v�R�s�[��ԋp���܂��B
	 * 
	 * @param target
	 *            �R�s�[�Ώۂ̃I�u�W�F�N�g
	 * @return �R�s�[���ꂽ�I�u�W�F�N�g��ԋp���܂�.
	 */
	private Object copy(Object target) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(target);

		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
		return ois.readObject();
	}

	/**
	 * ���g�̃e�[�u�����J�������n�b�V���Ƃ������X�g�Ƃ��ĕԋp���܂��B
	 */
	public ArrayList<LinkedHashMap<String, String>> toHashList() {
		ArrayList<LinkedHashMap<String, String>> tblcp = null;
		try {
			tblcp = (ArrayList<LinkedHashMap<String, String>>) copy(tableHash);
		} catch (Exception e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
		return tblcp;
	}

	public String[] getColumns() {
		return columns;
	}

	/**
	 * �ˉe���Z���T�|�[�g���܂��B
	 * 
	 * @param columns
	 *            �쐬�������J�����̗񋓂ł��B
	 * @return
	 */
	public RTable select(String... columns) {
		ArrayList<LinkedHashMap<String, String>> retTable = new ArrayList<LinkedHashMap<String, String>>();
		for (LinkedHashMap<String, String> trow : tableHash) {
			LinkedHashMap<String, String> row = new LinkedHashMap<String, String>();
			for (String col : columns) {
				String[] tokens = col.split("as");
				if (tokens.length == 2) {
					row.put(tokens[1].trim(), trow.get(tokens[0].trim()));
				} else {
					row.put(col.trim(), trow.get(col.trim()));
				}
			}
			retTable.add(row);
		}
		return new RTable(retTable);
	}

	/**
	 * �I�����Z���T�|�[�g���܂��B
	 * 
	 * @param condition
	 *            �������L�q���܂��Bex)[���i��] = ���v and [���iID] = A001 or...
	 */
	public RTable where(String condition) {
		ArrayList<LinkedHashMap<String, String>> retTable = new ArrayList<LinkedHashMap<String, String>>();
		for (LinkedHashMap<String, String> row : tableHash) {
			if ((new Condition(condition, row)).answer) {
				retTable.add(row);
			}
		}
		return new RTable(retTable);
	}

	/**
	 * ����̃J�����̍��W�����Z�q���T�|�[�g���܂��B
	 * 
	 * @param thiscolumn
	 *            ���̃C���X�^���X�̈�����鑤�̃J����
	 * @param other
	 *            �������̃e�[�u��
	 * @param othercolumn
	 *            �����J����
	 * @return �����Z���ʂ̃e�[�u��
	 */
	public RTable sub(String thiscolumn, RTable other, String othercolumn) {
		int thissize = this.count();
		ArrayList<LinkedHashMap<String, String>> ret = new ArrayList<LinkedHashMap<String, String>>();
		for (int i = 0; i < thissize; i++) {
			String val = this.tableHash.get(i).get(thiscolumn);
			if (other.where("[" + othercolumn + "] = " + val).count() == 0) {
				ret.add(new LinkedHashMap<String, String>(this.tableHash.get(i)));
			}
		}
		return new RTable(ret);
	}

	/**
	 * condition�̂��ƂŃe�[�u�����R�������s���܂��B
	 * 
	 * @param thisname
	 *            ���̃e�[�u���ɂ��閼�O ex)���itbl
	 * @param other
	 *            �����Ώۂ̃e�[�u��
	 * @param othername
	 *            �����Ώۂ̃e�[�u���ɂ��閼�O ex)����tbl
	 * @param condition
	 *            �������L�q���܂��Bex)[���itbl.���iID] = [����tbl.���iID]
	 * @return �������ꂽ�e�[�u��
	 */
	public RTable join(String thisname, RTable other, String othername, String condition) {
		ArrayList<LinkedHashMap<String, String>> retTable = new ArrayList<LinkedHashMap<String, String>>();
		for (LinkedHashMap<String, String> thisrow : tableHash) {
			for (LinkedHashMap<String, String> otherrow : other.tableHash) {
				LinkedHashMap<String, String> row = new LinkedHashMap<String, String>();
				for (Object key : thisrow.keySet().toArray()) {
					row.put(thisname + "." + key, (String) thisrow.get(key));
				}
				for (Object key : otherrow.keySet().toArray()) {
					row.put(othername + "." + key, (String) otherrow.get(key));
				}
				if ((new Condition(condition, row)).answer) {
					retTable.add(row);
				}
			}
		}
		return new RTable(retTable);
	}

	/**
	 * @return �e�[�u���̍s����ԋp���܂��B
	 */
	public int count() {
		return tableHash.size();
	}

	/**
	 * �w�肵���J��������ӂɌ��܂邩�̃`�F�b�N���s���܂��B
	 * 
	 * @param column
	 *            �J������
	 * @return ��ӂɌ��܂�ꍇ��true��ԋp���܂��B
	 */
	public boolean isUnique(String column) {
		ArrayList checklist = new ArrayList<String>();
		for (LinkedHashMap<String, String> row : tableHash) {
			if (checklist.contains(row.get(column)))
				return false;
			checklist.add(row.get(column));
		}
		return true;
	}

	@Override
	/**
	 * �e�[�u����������x�������`�ŕ�����ɂ��܂��B
	 */
	public String toString() {
		String info = "";
		for (LinkedHashMap<String, String> row : tableHash) {
			info += row.toString() + "\n";
		}
		return info + "count = " + tableHash.size() + "\n";
	}

	/**
	 * �_���������͂���э\����̓N���X�ł��B �s�̃n�b�V���Ə����������炤���ƂŐ^�U�����肵�܂��B <br>
	 * ---condition�̍\���K��BNF---<br>
	 * �K�����̍\���K���łȂ��Ɨ��p�ł��Ȃ��B �����͂̓s����K���v�f�̊Ԃɂ͋󔒂�����B<br>
	 * 
	 * <�_����> := <�_����> "or" <�_����> | <�_����> <br>
	 * <�_����> := <����> "and" <�_����> | <����> <br>
	 * <����> := <�l> <�_�����Z�q> <�l> | "true" | "false"<br>
	 * 
	 * �ȉ��͌����ɔ��f���Ȃ��B <br>
	 * <�l> := <�萔> | <�J����> //���ۂɂ�<�J����>��<�萔>�ɑO�����ς�> <br>
	 * <�_�����Z�q> := "=" | ">=" | "<=" | "!=" | "<" | ">" |<br>
	 * <�J����> := "[" <������> "]" <br>
	 * <�萔> := <������> | <��> | "null"<br>
	 * <br>
	 * <>:��I�[�L�� "":�I�[�L��<br>
	 * <br>
	 * ex) [�l�i] >= 100 and [���i�R�[�h] != null <br>
	 * �ō����o <br>
	 * <�_����> <br>
	 * = <�_����> <br>
	 * = <����> and <�_����> <br>
	 * = <�l> <�_�����Z�q> <�l> and <�_����><br>
	 * = <�J����> <�_�����Z�q> <�l> and <�_����><br>
	 * = [�l�i] <�_�����Z�q> <�l> and <�_����><br>
	 * = [�l�i] <�_�����Z�q> <�l> and <�_����><br>
	 * = [�l�i] >= <�萔> and <�_����><br>
	 * = [�l�i] >= 100 and <�_����><br>
	 * = [�l�i] >= 100 and <����><br>
	 * ... <br>
	 * = [�l�i] >= 100 and [���i�R�[�h] != null<br>
	 * 
	 * @author �r�c ��
	 *
	 */
	private static class Condition {
		private String question;
		private boolean answer;
		private LinkedHashMap row;
		private String token;
		private Scanner scan;

		Condition(String question, LinkedHashMap row) {
			scan = new Scanner(question);
			this.row = row;
			token = "";
			nextToken();
			answer = �_����();
		}

		void nextToken() {
			if (scan.hasNext()) {
				token = scan.next();
				Pattern colPat = Pattern.compile("\\[(.*?)\\]");
				Matcher m = colPat.matcher(token);
				// �J�����͒l�ɕϊ�����O�����B
				if (m.find()) {
					String colName = m.group(1);
					token = (String) row.get(colName);

				}
			} else {
				token = "EOF";
			}
		}

		boolean �_����() {
			boolean ret = �_����();
			if (token.equals("or")) {
				nextToken();
				ret |= �_����();
			}
			return ret;
		}

		boolean �_����() {
			boolean ret = ����();
			if (token.equals("and")) {
				nextToken();
				ret &= �_����();
			}
			return ret;
		}

		boolean ����() {
			boolean ret = false;
			if (token.equals("true")) {
				nextToken();
				return true;
			}
			if (token.equals("false")) {
				nextToken();
				return false;
			}
			String a = token;
			nextToken();
			String oper = token;
			nextToken();
			String b = token;
			if (b.equals("null"))
				b = "";
			switch (oper) {
			case "=":
				ret = a.equals(b);
				break;
			case "!=":
				ret = !a.equals(b);
				break;
			case ">=":
				if (NumberUtils.isNumber(a) && NumberUtils.isNumber(b))
					ret = (Long.valueOf(a) >= Long.valueOf(b));
				break;
			case "<=":
				if (NumberUtils.isNumber(a) && NumberUtils.isNumber(b))
					ret = (Long.valueOf(a) <= Long.valueOf(b));
				break;
			case ">":
				if (NumberUtils.isNumber(a) && NumberUtils.isNumber(b))
					ret = (Long.valueOf(a) > Long.valueOf(b));
				break;
			case "<":
				if (NumberUtils.isNumber(a) && NumberUtils.isNumber(b))
					ret = (Long.valueOf(a) < Long.valueOf(b));
				break;
			}
			nextToken();
			return ret;
		}

		boolean answer() {
			return answer;
		}

	}
}