package server;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

import basic.RoomInformation;

public class Database {
	static Connection con = null;
	PreparedStatement userPS = null;
	PreparedStatement roomPS = null;

	Statement stmt = null;
	ResultSet rs = null;

//	public static void main(String[] args) {
//		Database redb = new Database();
//
//		System.out.println(redb.InsertUserInfor("ChanYoung", "young221718@gmail.com", "1234"));
//		System.out.println(redb.CheckPassword("young221718@gmail.com", "1234"));
//
//		// try {
//		// //redb.con.commit();
//		// } catch (SQLException e) {
//		// // TODO Auto-generated catch block
//		// e.printStackTrace();
//		// }
//	}

	/**
	 * constructor
	 */
	public Database() {
		try {
			ConnectDB();
			synchronized (con) {
				con.setAutoCommit(false);
				String userPSQL = "insert into user_information(user_name,email,password) values(?, ?, ?);";
				userPS = (PreparedStatement) con.prepareStatement(userPSQL);

				String roomPSQL = "insert into room_informtaion values(?,?,?,?,?,?,?);";
				roomPS = (PreparedStatement) con.prepareStatement(roomPSQL);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ConnectDB �����ͺ��̽��� �����ϴ� �޼����Դϴ�.
	 */
	public void ConnectDB() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://localhost/re_db";
			String user = "root", passwd = "12345";
			synchronized (con) {
				con = (Connection) DriverManager.getConnection(url, user, passwd);
			}
			System.out.println(con);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * insertUserInfor ���� ������ �����ͺ��̽��� �߰��ϰ� ���� �� ���� �Ǵ� �Լ��̴�.
	 * 
	 * @param name
	 *            - user's name
	 * @param email
	 *            - user's email
	 * @param pw
	 *            - user's password
	 * @return int // 1: success // -1: already exist // 0: fail: sql error
	 */
	public int InsertUserInfor(String name, String email, String pw) {

		try {
			// check if email is already existed
			stmt = (Statement) con.createStatement();
			String sql = "select * from user_information where email ='" + email + "'";
			rs = stmt.executeQuery(sql);
			if (rs.next()) {
				return -1; // already exist
			}

			// insert user information
			userPS.setString(1, name);
			userPS.setString(2, email);
			userPS.setString(3, pw);

			int count = userPS.executeUpdate();
			if (count != 1) {
				System.out.println(count);
				return -2;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return -2; // fail: sql error
		}
		return 1; // success
	}

	/**
	 * CheckPassword - �α����� �ϱ� ���� �޼���
	 * 
	 * @param email
	 *            - �̸���
	 * @param pw
	 *            - ��й�ȣ
	 * @return int // 1: correct password // 0: SQL error // -1: wrong password //
	 *         -2: not exist email
	 */
	public int CheckPassword(String email, String pw) {

		try {
			stmt = (Statement) con.createStatement();
			String sql = "select password from user_information where email ='" + email + "'";
			rs = stmt.executeQuery(sql);

			if (rs.next()) {
				if (rs.getString(1).equals(pw))
					return 1; // correct password
				else
					return -1; // wrong password
			} else
				return -2; // not exist email
		} catch (SQLException e) {
			e.printStackTrace();
			return 0; // SQL error
		}
	}

	/**
	 * InsertRoomInfor �� ������ �����ͺ��̽��� �߰���.
	 * 
	 * @param rf
	 *            - RoomInformation Ŭ������ �޾ƿ�
	 * @return boolean - if success return true, else return false
	 */
	public boolean InsertRoomInfor(RoomInformation rf) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		try {
			Integer maxPeople = rf.howManyPeople;

			Calendar temp = rf.startDate;
			temp.add(rf.startDate.DATE, 1);
			String id = ((Integer) rf.port).toString() + "." + sdf.format(temp.getTime()).substring(2);

			roomPS.setString(1, rf.groupName);
			roomPS.setString(2, id);
			roomPS.setString(3, df.format(rf.startDate.getTime()));
			roomPS.setString(4, df.format(rf.endDate.getTime()));
			roomPS.setString(5, maxPeople.toString());
			roomPS.setString(6, rf.securityQuestion);
			roomPS.setString(7, rf.securityAnswer);

			int count = roomPS.executeUpdate();
			if (count != 1) {
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * DisconnectDB ������ ���̽��� ������ ���� �޼����̴�.
	 */
	public void DisconnectDB() {

		try {
			if (con != null && !con.isClosed())
				con.close();
			if (userPS != null && !userPS.isClosed())
				userPS.close();
			if (roomPS != null && !roomPS.isClosed())
				roomPS.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * CommitDB : �����ͺ��̽��� Ŀ���Ѵ�.
	 */
	public void CommitDB() {
		try {
			synchronized (con) {
				con.commit();
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}

}
