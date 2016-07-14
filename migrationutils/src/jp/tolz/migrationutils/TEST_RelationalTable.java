package jp.tolz.migrationutils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.math.NumberUtils;

/**
 * 現在のRelationalTableを置き換え予定のクラス。 BDのテーブル的データ構造を扱うためのクラスです。 各種集合演算をサポートします。
 * RelationalTableの演算結果をRelationalTableにすることで閉包性をもたせ、連続した演算を可能にしています。
 * 構文解析の都合上Table<String>としています。
 * 
 * @author 池田 透
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
	 * 射影演算をサポートします。
	 * 
	 * @param columns
	 *            選択したいカラムの列挙
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
	 * 選択演算をサポートします。
	 * 
	 * @param booleanExp
	 *            条件 ex)[商品名] = 時計 and [商品ID] = A001 or...
	 */
	public RTable where(String booleanExp) {
		return null;
	}

	/**
	 * conditionのもとでテーブル自然結合を行います。 conditionを満たすすべての組み合わせが出力となります。
	 * 
	 * @param thisname
	 *            このテーブルにつける名前 ex)商品tbl
	 * @param other
	 *            結合対象のテーブル
	 * @param othername
	 *            結合対象のテーブルにつける名前 ex)売上tbl
	 * @param booleanExp
	 *            条件を記述 ex)[商品tbl.商品ID] = [売上tbl.商品ID]
	 * @return 結合されたテーブル
	 */
	public RTable join(String thisname, RTable other, String othername, String booleanExp) {
		return null;
	}

	/**
	 * 特定のカラムの差集合演算子をサポートします。 つまり,自分のカラムに含まれていて,引く側のカラムに含まれているものを消去したものを返却します。
	 * 
	 * @param thiscolumn
	 *            このインスタンスの引かれる側のカラム
	 * @param other
	 *            引く側のテーブル
	 * @param othercolumn
	 *            引くカラム
	 * @return 差演算結果のテーブル
	 */
	public RTable Sub(String thiscolumn, RTable other, String othercolumn) {
		return null;
	}

	/**
	 * 指定したカラムが一意に決まるかのチェックを行います。
	 * 
	 * @param column
	 *            カラム名
	 * @return 一意に決まる場合はtrueを返却します。
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
	 * 特殊な文字列入れ替えをサポートします。
	 * @see http://www.javadrive.jp/regex/replace/index2.html
	 * @param column
	 *            対象カラム
	 * @param booleanExp
	 *            対象行絞り込み条件
	 * @param afterStringScript
	 *            入れ替え文字列作成用スクリプト
	 * @return 変更された件数
	 */
	public int replace(String column, String booleanExp, String afterStringScript) {
		int targetCol = headHash.get(column);
		
		return 0;
	}

	/**
	 * No用カラムを追加します。ex) startIndex = -1, step = -1 -> -1,-2,-3<br>
	 * 
	 * @param column
	 *            追加カラム名
	 * @param startIndex
	 *            先頭行のインデックス
	 * @param step
	 *            indexの増分
	 * @return
	 */
	public RTable addNoColumn(String column, long startIndex, long step) {
		return null;
	}

	/**
	 * RelationalTable用のBooleanExpクラスです。 カラムを具体的な値に置き換える前処理を行います。
	 * 
	 * @author 池田 透
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
		 * "[カラム]" -> 具体的値<br>
		 * に変換します。
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
	 * @author 池田 透
	 *
	 */
	private class RTableStringScript extends StringScript {
		HashMap<String, String> row = new HashMap<String, String>();

		RTableStringScript(String script, HashMap<String, String> row) {
			super(script);
			this.row = row;
		}

		/**
		 * "[カラム]" -> 具体的値<br>
		 * に変換します。
		 */
		void preprocessing() {
			Set<String> keys = row.keySet();
			for (String key : keys) {
				String val = row.get(key);
				// 数字文字に関わらず文字列とする。
				script = script.replace("[".concat(key).concat("]"), "'".concat(val).concat("'"));

			}
		}

	}

}