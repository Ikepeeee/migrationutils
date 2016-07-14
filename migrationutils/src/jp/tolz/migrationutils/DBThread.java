package jp.tolz.migrationutils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/*
 * 危険!とりあえず使わないこと。
 * DB処理の高速化のためのクラス。
 * 継承禁止とスレッドで高速化。
 * ただし、結果は返さず不明のまま。
 */

final public class DBThread extends Thread{
	private String hostip;
	private String dbname;
	private String dbuser;
	private String dbpasswd;
	public String sql;

	public DBThread(String hostip, String dbname, String dbuser, String dbpasswd) {
		this.hostip = hostip;
		this.dbname = dbname;
		this.dbuser = dbuser;
		this.dbpasswd = dbpasswd;
		this.sql = sql;
		
	}
	
	@Override
	public void run(){
		Connection conn;
		try {
			conn = DriverManager.getConnection("jdbc:postgresql://" + hostip + ":5432/" + dbname, dbuser, dbpasswd);
			conn.createStatement().execute(sql);
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}