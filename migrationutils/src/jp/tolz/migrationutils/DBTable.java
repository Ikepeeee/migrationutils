package jp.tolz.migrationutils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import jp.tolz.migrationutils.RTable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class DBTable{
	private String hostip;
	private String dbname;
	private String dbuser;
	private String dbpasswd;
	private String table;

	public DBTable(String hostip, String dbname, String dbuser, String dbpasswd, String table) {
		this.hostip = hostip;
		this.dbname = dbname;
		this.dbuser = dbuser;
		this.dbpasswd = dbpasswd;
		this.table = table;

		try {
			Connection conn;
			conn = DriverManager.getConnection("jdbc:postgresql://" + hostip + ":5432/" + dbname, dbuser, dbpasswd);
			System.out.print("jdbc:postgresql://" + hostip + ":5432/" + dbname + "Ç÷ÇÃê⁄ë±:");
			System.out.println(!conn.isClosed());
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	
	public RTable createRTable() {
		return new RTable(toHashList("SELECT * FROM " + "\"" + table + "\""));
	}

	// îCà”ÇÃSQLÇî≠çsÇ≈Ç´Ç‹Ç∑ÅB
	private ArrayList<LinkedHashMap<String, String>> toHashList(String sql) {
		// TODO Auto-generated method stub
		ResultSet result;
		ResultSetMetaData metaData;
		ArrayList<LinkedHashMap<String, String>> hashList = new ArrayList<LinkedHashMap<String, String>>();
		try {
			Connection conn;
			conn = DriverManager.getConnection("jdbc:postgresql://" + hostip + ":5432/" + dbname, dbuser, dbpasswd);
			Statement stmt = conn.createStatement();
			result = stmt.executeQuery(sql);
			metaData = result.getMetaData();

			List<String> cols = new ArrayList<String>();
			int colSize = metaData.getColumnCount();

			for (int i = 1; i <= colSize; i++) {
				cols.add(metaData.getColumnName(i));
			}

			while (result.next()) {
				LinkedHashMap<String, String> rowHash = new LinkedHashMap<String, String>();
				for (String colName : cols) {
					rowHash.put(colName, result.getString(colName));
				}
				hashList.add(rowHash);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return hashList;
	}


}