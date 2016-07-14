package jp.tolz.migrationutils;

public interface TableImpl<T>{
	public int rowCount();
	public int columnCount();
	public T[] rowAt(int row);
	public T[][] rowsAt(int... row);
	public T[] columnAt(int column);
	public T[][] columnsAt(int... column);
	public T valueAt(int row,int column);
	public T[][] addRow(T[] row);
	public T[][] addRows(T[][] rows);
	public T[][] addColumn(T[] column);
	public T[][] addColumns(T[][] columns);
	public T[][] deleteRows(int... row);
	public T[][] deleteColumns(int... columns);
	public T[][] updateRow(int rowIndex, T[] row);
	public T[][] updateColumn(int columnIndex, T[] column);
}