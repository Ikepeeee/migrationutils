package jp.tolz.migrationutils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.HashMap;

public class TEST_Table<T> implements TableImpl {
	protected T[][] table;
	protected T[] heads;

	public TEST_Table() {
	}

	/**
	 * ヘッダ情報とテーブルの内容が別に存在するときに使います。
	 * 
	 * @param heads
	 *            ヘッダ情報
	 * @param table
	 *            テーブル内容
	 */
	public TEST_Table(T[] heads, T[][] table) {
		this.heads = (T[]) copy(heads);
		this.table = (T[][]) copy(table);
		int i = 0;
		for (T head : heads) {
			i++;
		}
	}

	/**
	 * ヘッダ情報がテーブルの0行目に存在するときに使用します。
	 * 
	 * @param tableWithHead
	 *            カラム付きテーブル
	 */
	public TEST_Table(T[][] tableWithHead) {
		int size = tableWithHead.length;
		this.heads = tableWithHead[0];
		this.table = (T[][]) new Object[size - 1][];
		for (int i = 1; i < size; i++) {
			table[i - 1] = tableWithHead[i];
		}
	}

	/**
	 * 任意のオブジェクトのディープコピーを生成します。
	 * 
	 * @param target
	 *            コピーをするオブジェクト。
	 * @ @throws
	 *       ClassNotFoundException return 引数をコピーしたオブジェクト。
	 * @throws Exception
	 */
	private static Object copy(Object target) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos;
		ObjectInputStream ois;
		Object ret = null;
		try {
			oos = new ObjectOutputStream(bos);
			oos.writeObject(target);
			ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
			ret = ois.readObject();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * @return テーブルの行数を返却.
	 */
	public int rowCount() {
		return table.length;
	}

	/**
	 * @return テーブルのカラム数を返却.
	 */
	public int columnCount() {
		return heads.length;
	}

	/**
	 * @param row
	 *            指定する行.
	 * @return 指定した行の配列.
	 * @throws Exception
	 */
	public T[] rowAt(int row) {
		return (T[]) copy(table[row]);
	}

	/**
	 * @param row
	 *            指定する行.
	 * @return 指定した行の配列.
	 * @throws Exception
	 */
	public T[][] rowsAt(int... row) {
		int colCount = columnCount();
		T[][] rows = (T[][]) new Object[row.length][colCount];
		for (int i = 0; i < row.length; i++) {
			rows[i] = (T[]) copy(table[row[i]]);
		}
		return rows;
	}

	/**
	 * @param column
	 *            指定する列数.
	 * @return 指定した列の配列.
	 */
	public T[] columnAt(int column) {
		int rowCount = rowCount();
		T[] columns = (T[]) new Object[rowCount];
		for (int i = 0; i < rowCount; i++) {
			columns[i] = table[column][i];
		}
		return columns;
	}

	/**
	 * 
	 * @param column
	 *            指定する列.
	 * @return 指定した列の配列.
	 */
	public T[][] columnsAt(int... column) {
		int rowCount = rowCount();
		T[][] columns = (T[][]) new Object[column.length][rowCount];
		for (int i = 0; i < rowCount; i++) {
			for (int j = 0; j < column.length; j++) {
				columns[i][j] = table[i][column[j]];
			}
		}
		return columns;
	}

	/**
	 * 指定したセルを取得します.
	 * 
	 * @param row
	 *            指定列
	 * @param column
	 *            指定行
	 * @return 指定したセル.
	 * @throws Exception
	 */
	public T valueAt(int row, int column) {
		return (T) copy(table[row][column]);
	}

	private static Object[] addlast(Object[] list,Object[] e){
		Object[] ret = Arrays.copyOf(list, list.length + e.length);
		for(int i = 0; i < e.length; i++){
			ret[ret.length - 1] = copy(e.getClass());
		}
		return ret;
	}

	/**
	 * 最後尾に行を追加します.
	 * 
	 */
	public T[][] addRow(Object[] row) {
		T[][] rettable = Arrays.copyOf(table, table.length + 1);
		rettable[rettable.length - 1] = (T[]) copy(row);
		return null;
	}

	@Override
	public T[][] addRows(Object[][] rows) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T[][] addColumn(Object[] column) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T[][] addColumns(Object[][] columns) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T[][] deleteRows(int... row) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T[][] deleteColumns(int... columns) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T[][] updateRow(int rowIndex, Object[] row) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T[][] updateColumn(int columnIndex, Object[] column) {
		// TODO Auto-generated method stub
		return null;
	}

}