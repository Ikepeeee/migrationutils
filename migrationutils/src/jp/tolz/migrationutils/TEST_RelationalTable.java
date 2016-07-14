package jp.tolz.migrationutils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.math.NumberUtils;

/**
 * ���݂�RelationalTable��u�������\��̃N���X�B BD�̃e�[�u���I�f�[�^�\�����������߂̃N���X�ł��B �e��W�����Z���T�|�[�g���܂��B
 * RelationalTable�̉��Z���ʂ�RelationalTable�ɂ��邱�Ƃŕ���������A�A���������Z���\�ɂ��Ă��܂��B
 * �\����͂̓s����Table<String>�Ƃ��Ă��܂��B
 * 
 * @author �r�c ��
 *
 */
public class TEST_RelationalTable extends Table<String> {
	HashMap<String, Integer> headHash;

	public TEST_RelationalTable(String[] heads, String[][] table) {
		super(heads, table);
		headHash = new HashMap<String, Integer>();
		int i = 0;
		for (String head : heads) {
			headHash.put(head, i);
			i++;
		}
	}

	public TEST_RelationalTable(String[][] tableWithHead) {
		super(tableWithHead);
		String[] heads = null;
		headHash = new HashMap<String, Integer>();
		int i = 0;
		for (String head : heads) {
			headHash.put(head, i);
			i++;
		}
	}

	/**
	 * �ˉe���Z���T�|�[�g���܂��B
	 * 
	 * @param columns
	 *            �I���������J�����̗�
	 * @return
	 */
	public RTable select(String... columns) {
		int[] colNums = new int[columns.length];
		for (int i = 0; i < columns.length; i++) {
			colNums[i] = headHash.get(columns[i]);
		}
		return new RTable(columnsAt(colNums));
	}

	/**
	 * �I�����Z���T�|�[�g���܂��B
	 * 
	 * @param booleanExp
	 *            ���� ex)[���i��] = ���v and [���iID] = A001 or...
	 */
	public RTable where(String booleanExp) {
		return null;
	}

	/**
	 * condition�̂��ƂŃe�[�u�����R�������s���܂��B condition�𖞂������ׂĂ̑g�ݍ��킹���o�͂ƂȂ�܂��B
	 * 
	 * @param thisname
	 *            ���̃e�[�u���ɂ��閼�O ex)���itbl
	 * @param other
	 *            �����Ώۂ̃e�[�u��
	 * @param othername
	 *            �����Ώۂ̃e�[�u���ɂ��閼�O ex)����tbl
	 * @param booleanExp
	 *            �������L�q ex)[���itbl.���iID] = [����tbl.���iID]
	 * @return �������ꂽ�e�[�u��
	 */
	public RTable join(String thisname, RTable other, String othername, String booleanExp) {
		return null;
	}

	/**
	 * ����̃J�����̍��W�����Z�q���T�|�[�g���܂��B �܂�,�����̃J�����Ɋ܂܂�Ă���,�������̃J�����Ɋ܂܂�Ă�����̂������������̂�ԋp���܂��B
	 * 
	 * @param thiscolumn
	 *            ���̃C���X�^���X�̈�����鑤�̃J����
	 * @param other
	 *            �������̃e�[�u��
	 * @param othercolumn
	 *            �����J����
	 * @return �����Z���ʂ̃e�[�u��
	 */
	public RTable Sub(String thiscolumn, RTable other, String othercolumn) {
		return null;
	}

	/**
	 * �w�肵���J��������ӂɌ��܂邩�̃`�F�b�N���s���܂��B
	 * 
	 * @param column
	 *            �J������
	 * @return ��ӂɌ��܂�ꍇ��true��ԋp���܂��B
	 */
	public boolean isUnique(String column) {
		ArrayList<String> checklist = new ArrayList<String>();
		String[] vals = columnAt(headHash.get(column));
		for (String val : vals) {
			if (checklist.contains(val))
				return false;
			checklist.add(val);
		}
		return true;
	}

	/**
	 * ����ȕ��������ւ����T�|�[�g���܂��B
	 * @see http://www.javadrive.jp/regex/replace/index2.html
	 * @param column
	 *            �ΏۃJ����
	 * @param booleanExp
	 *            �Ώۍs�i�荞�ݏ���
	 * @param afterStringScript
	 *            ����ւ�������쐬�p�X�N���v�g
	 * @return �ύX���ꂽ����
	 */
	public int replace(String column, String booleanExp, String afterStringScript) {
		int targetCol = headHash.get(column);
		
		return 0;
	}

	/**
	 * No�p�J������ǉ����܂��Bex) startIndex = -1, step = -1 -> -1,-2,-3<br>
	 * 
	 * @param column
	 *            �ǉ��J������
	 * @param startIndex
	 *            �擪�s�̃C���f�b�N�X
	 * @param step
	 *            index�̑���
	 * @return
	 */
	public RTable addNoColumn(String column, long startIndex, long step) {
		return null;
	}

	/**
	 * RelationalTable�p��BooleanExp�N���X�ł��B �J��������̓I�Ȓl�ɒu��������O�������s���܂��B
	 * 
	 * @author �r�c ��
	 *
	 */
	private class RTableBooleanExp extends BooleanExp {
		HashMap<String, String> row = new HashMap<String, String>();

		RTableBooleanExp(String question, HashMap<String, String> row) {
			super(question);
			this.row = row;
		}
		
		RTableBooleanExp(String question){
			super(question);
			
		}

		/**
		 * "[�J����]" -> ��̓I�l<br>
		 * �ɕϊ����܂��B
		 */
		void preprocessing() {
			Set<String> keys = row.keySet();
			for (String key : keys) {
				String val = row.get(key);
				if (NumberUtils.isNumber(val)) {
					question = question.replace("[".concat(key).concat("]"), val);
				} else {
					question = question.replace("[".concat(key).concat("]"), "'".concat(val).concat("'"));
				}
			}
		}

	}

	/**
	 * 
	 * @author �r�c ��
	 *
	 */
	private class RTableStringScript extends StringScript {
		HashMap<String, String> row = new HashMap<String, String>();

		RTableStringScript(String script, HashMap<String, String> row) {
			super(script);
			this.row = row;
		}

		/**
		 * "[�J����]" -> ��̓I�l<br>
		 * �ɕϊ����܂��B
		 */
		void preprocessing() {
			Set<String> keys = row.keySet();
			for (String key : keys) {
				String val = row.get(key);
				// ���������Ɋւ�炸������Ƃ���B
				script = script.replace("[".concat(key).concat("]"), "'".concat(val).concat("'"));

			}
		}

	}

}