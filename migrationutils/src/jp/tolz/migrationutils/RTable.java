package jp.tolz.migrationutils;

/**
 * @author 池田 透
 * @version 1.0
 * BDのテーブル的データ構造を扱うためのクラスです。
 * 各種集合演算をサポートします。
 * conditionは独自に構文解析してtrueかfalseを決定する内部クラスです。
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
	 * カラム名をキーとしたはハッシュをリストにしたものからインスタンスを作成します。
	 */
	public RTable(ArrayList<LinkedHashMap<String, String>> tbl) {
		try {
			this.tableHash = (ArrayList<LinkedHashMap<String, String>>) copy(tbl);
			if (this.tableHash == null)
				this.tableHash = new ArrayList<LinkedHashMap<String, String>>();
		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
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
	 * ヘッダ付き2次元配列からインスタンスを生成します。
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
	 * コピーコンストラクタ。ディープコピーをとります。
	 */
	public RTable(RTable table) {
		try {
			RTable cp = (RTable) copy(table);
			name = cp.name;
			tableHash = cp.tableHash;
			columns = cp.columns;
		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	/**
	 * 任意のオブジェクトのディープコピーを返却します。
	 * 
	 * @param target
	 *            コピー対象のオブジェクト
	 * @return コピーされたオブジェクトを返却します.
	 */
	private Object copy(Object target) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(target);

		ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
		return ois.readObject();
	}

	/**
	 * 自身のテーブルをカラムをハッシュとしたリストとして返却します。
	 */
	public ArrayList<LinkedHashMap<String, String>> toHashList() {
		ArrayList<LinkedHashMap<String, String>> tblcp = null;
		try {
			tblcp = (ArrayList<LinkedHashMap<String, String>>) copy(tableHash);
		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		return tblcp;
	}

	public String[] getColumns() {
		return columns;
	}

	/**
	 * 射影演算をサポートします。
	 * 
	 * @param columns
	 *            作成したいカラムの列挙です。
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
	 * 選択演算をサポートします。
	 * 
	 * @param condition
	 *            条件を記述します。ex)[商品名] = 時計 and [商品ID] = A001 or...
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
	 * 特定のカラムの差集合演算子をサポートします。
	 * 
	 * @param thiscolumn
	 *            このインスタンスの引かれる側のカラム
	 * @param other
	 *            引く側のテーブル
	 * @param othercolumn
	 *            引くカラム
	 * @return 差演算結果のテーブル
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
	 * conditionのもとでテーブル自然結合を行います。
	 * 
	 * @param thisname
	 *            このテーブルにつける名前 ex)商品tbl
	 * @param other
	 *            結合対象のテーブル
	 * @param othername
	 *            結合対象のテーブルにつける名前 ex)売上tbl
	 * @param condition
	 *            条件を記述します。ex)[商品tbl.商品ID] = [売上tbl.商品ID]
	 * @return 結合されたテーブル
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
	 * @return テーブルの行数を返却します。
	 */
	public int count() {
		return tableHash.size();
	}

	/**
	 * 指定したカラムが一意に決まるかのチェックを行います。
	 * 
	 * @param column
	 *            カラム名
	 * @return 一意に決まる場合はtrueを返却します。
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
	 * テーブルをある程度整った形で文字列にします。
	 */
	public String toString() {
		String info = "";
		for (LinkedHashMap<String, String> row : tableHash) {
			info += row.toString() + "\n";
		}
		return info + "count = " + tableHash.size() + "\n";
	}

	/**
	 * 論理式字句解析および構文解析クラスです。 行のハッシュと条件式をもらうことで真偽を決定します。 <br>
	 * ---conditionの構文規則BNF---<br>
	 * 必ずこの構文規則でないと利用できない。 字句解析の都合上必ず要素の間には空白を入れる。<br>
	 * 
	 * <論理式> := <論理積> "or" <論理式> | <論理積> <br>
	 * <論理積> := <命題> "and" <論理積> | <命題> <br>
	 * <命題> := <値> <論理演算子> <値> | "true" | "false"<br>
	 * 
	 * 以下は厳密に判断しない。 <br>
	 * <値> := <定数> | <カラム> //実際には<カラム>は<定数>に前処理済み> <br>
	 * <論理演算子> := "=" | ">=" | "<=" | "!=" | "<" | ">" |<br>
	 * <カラム> := "[" <文字列> "]" <br>
	 * <定数> := <文字列> | <数> | "null"<br>
	 * <br>
	 * <>:非終端記号 "":終端記号<br>
	 * <br>
	 * ex) [値段] >= 100 and [商品コード] != null <br>
	 * 最左導出 <br>
	 * <論理式> <br>
	 * = <論理積> <br>
	 * = <命題> and <論理積> <br>
	 * = <値> <論理演算子> <値> and <論理積><br>
	 * = <カラム> <論理演算子> <値> and <論理積><br>
	 * = [値段] <論理演算子> <値> and <論理積><br>
	 * = [値段] <論理演算子> <値> and <論理積><br>
	 * = [値段] >= <定数> and <論理積><br>
	 * = [値段] >= 100 and <論理積><br>
	 * = [値段] >= 100 and <命題><br>
	 * ... <br>
	 * = [値段] >= 100 and [商品コード] != null<br>
	 * 
	 * @author 池田 透
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
			answer = 論理式();
		}

		void nextToken() {
			if (scan.hasNext()) {
				token = scan.next();
				Pattern colPat = Pattern.compile("\\[(.*?)\\]");
				Matcher m = colPat.matcher(token);
				// カラムは値に変換する前処理。
				if (m.find()) {
					String colName = m.group(1);
					token = (String) row.get(colName);

				}
			} else {
				token = "EOF";
			}
		}

		boolean 論理式() {
			boolean ret = 論理積();
			if (token.equals("or")) {
				nextToken();
				ret |= 論理式();
			}
			return ret;
		}

		boolean 論理積() {
			boolean ret = 命題();
			if (token.equals("and")) {
				nextToken();
				ret &= 論理積();
			}
			return ret;
		}

		boolean 命題() {
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