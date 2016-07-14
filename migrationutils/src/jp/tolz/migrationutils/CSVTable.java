package jp.tolz.migrationutils;

import jp.tolz.migrationutils.RTable;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class CSVTable {
	CSVReader reader;

	public CSVTable(String dir, String fileName, char separater) {
		try {
			reader = new CSVReader(new FileReader(dir + fileName), separater);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void exportCSV(RTable tbl, String dir, String fileName) {
		try {
			ArrayList<LinkedHashMap<String, String>> hashlist = tbl.toHashList();
			String[] cols = tbl.getColumns();
			if (cols == null)
				return;
			CSVWriter writer = new CSVWriter(new FileWriter(dir + fileName));
			int colsSize = cols.length;
			writer.writeNext(cols);
			for (LinkedHashMap<String, String> hash : hashlist) {
				String[] row = new String[colsSize];
				for (int i = 0; i < colsSize; i++) {
					String col = cols[i];
					row[i] = hash.get(col);
				}
				writer.writeNext(row);
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public RTable createTable() {
		ArrayList<LinkedHashMap<String, String>> tbl = new ArrayList<LinkedHashMap<String, String>>();

		try {
			String[] nextLine = null;
			String[] cols = null;
			if ((nextLine = reader.readNext()) != null) {
				cols = nextLine;
			}
			while ((nextLine = reader.readNext()) != null) {
				LinkedHashMap<String, String> row = new LinkedHashMap<String, String>();
				int size = nextLine.length;
				for (int i = 0; i < size; i++) {
					row.put(cols[i], nextLine[i]);
				}
				tbl.add(row);
			}
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new RTable(tbl);
	}

	public RTable createTable(String[] cols) {
		ArrayList<LinkedHashMap<String, String>> tbl = new ArrayList<LinkedHashMap<String, String>>();

		try {
			String[] nextLine = null;
			while ((nextLine = reader.readNext()) != null) {
				LinkedHashMap<String, String> row = new LinkedHashMap<String, String>();
				int size = nextLine.length;
				for (int i = 0; i < size; i++) {
					row.put(cols[i], nextLine[i]);
				}
				tbl.add(row);
			}
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new RTable(tbl);
	}

	public void export(String fileName) {

	}

}