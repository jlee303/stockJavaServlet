package jlee3900_CSCI201L_Assignment4_v2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

public class Database {
	Connection conn;
	// Statement st;
	// ResultSet rs;
	// PreparedStatement ps;

	public Database() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");// you need to load class driver before starting connection
			conn = DriverManager.getConnection("jdbc:mysql://localhost/assignment_4_database?user=root&password=root");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("sqle: " + e.getMessage());
			// Runscript

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String addUser(User u) {
		try {

			PreparedStatement ps = conn
					.prepareStatement("INSERT INTO Assignment_4_Database.users(Username, Email, Password, Balance) "
							+ "VALUES(\'" + u.getUsername() + "\', \'" + u.getEmail() + "\', \'" + u.getPassword()
							+ "\', \'" + 50000 + "\');");
			ps.executeUpdate();
			ps.close();

			return validateUser(u.getEmail(), null, true);
		} catch (SQLException e) {
			System.out.println("sqle: " + e.getMessage());
		}

		try {

			PreparedStatement ps = conn.prepareStatement(
					"SELECT * FROM Assignment_4_Database.users WHERE Email = \'" + u.getEmail() + "\';");
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return "AccountReg";
			}

			rs.close();
			ps.close();

			PreparedStatement ps2 = conn.prepareStatement(
					"SELECT * FROM Assignment_4_Database.users WHERE Username = \'" + u.getUsername() + "\';");
			ResultSet rs2 = ps2.executeQuery();

			if (rs2.next()) {
				return "AlreadyTaken";
			}
			rs2.close();
			ps2.close();
		} catch (SQLException e) {
			System.out.println("sqle: " + e.getMessage());
		}

		return null;
	}

	public String validateUser(String username, String password, Boolean isGoogle) {
		try {

			String statement;
			if (!isGoogle) {
				statement = "SELECT * FROM Assignment_4_Database.users WHERE Username = \'" + username
						+ "\' AND Password = \'" + password + "\';";
			} else {
				statement = "SELECT * FROM Assignment_4_Database.users WHERE Email = \'" + username + "\';";
			}
			PreparedStatement ps = conn.prepareStatement(statement);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				return rs.getString("UID");
			}

			if (isGoogle) {
				// Creating a new account for google login
				addUser(new User(username, username, null));
				statement = "SELECT * FROM Assignment_4_Database.users WHERE Email = \'" + username + "\';";
				PreparedStatement ps2 = conn.prepareStatement(statement);
				ResultSet rs2 = ps2.executeQuery();
				if (rs2.next()) {
					return rs2.getString("UID");
				}
				rs2.close();
				ps2.close();
			}
			rs.close();
			ps.close();

		} catch (SQLException e) {
			// System.out.println("Error in validating users from database: Database.java
			// line 40");
			System.out.println("sqle: " + e.getMessage());
		}

		return null;
	}

	public String addFavoriteStock(Stock s, String UID) {
		try {
			PreparedStatement ps1 = conn.prepareStatement("SELECT * FROM Assignment_4_Database.favorites WHERE UID = \'"
					+ UID + "\' AND Ticker = \'" + s.getStockTicker() + "\';");
			ResultSet rs1 = ps1.executeQuery();
			if (rs1.next()) {
				return deleteFavoriteStock(s, UID);
			} else {
				PreparedStatement ps = conn.prepareStatement(
						"INSERT INTO Assignment_4_Database.favorites(UID, Ticker, Tickername) " + "VALUES( \'" + UID
								+ "\', \'" + s.getStockTicker() + "\', \'" + s.getStockCompany() + "\');");
				ps.executeUpdate();
				ps.close();
				return "Added";
			}

		} catch (SQLException e) {
			// System.out.println("Error in updating favorites from database: Database.java
			// line 61");
			System.out.println("sqle: " + e.getMessage());
			return deleteFavoriteStock(s, UID);
		}
	}

	public String deleteFavoriteStock(Stock s, String UID) {
		try {
			PreparedStatement ps = conn.prepareStatement("DELETE FROM Assignment_4_Database.favorites WHERE Ticker=\'"
					+ s.getStockTicker() + "\' AND UID=\'" + UID + "\' ;");
			ps.executeUpdate();
			ps.close();
			return "Deleted";
		} catch (SQLException e) {
			// System.out.println("Error in updating favorites from database: Database.java
			// line 61");
			System.out.println("sqle: " + e.getMessage());
		}
		return null;
	}

	public Vector<Stock> getFavoriteStock(String UID) {
		Vector<Stock> favorites = new Vector<Stock>();
		try {

			PreparedStatement ps = conn
					.prepareStatement("SELECT * FROM Assignment_4_Database.favorites WHERE UID = " + UID);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				favorites.add(new Stock(rs.getString("Ticker"), rs.getString("TickerName")));
			}
			rs.close();
			ps.close();

		} catch (SQLException e) {
			// System.out.println("Error in getting favorites from database: Database.java
			// line 54");
			System.out.println("sqle: " + e.getMessage());
		}

		return favorites;
	}

	public String addPortfolio(Stock s, String UID) {
		try {
			double updateAmount = canAfford(s.getTotalCost(), UID);
			// System.out.println(updateAmount);
			if (updateAmount >= 0) {
				PreparedStatement ps = conn.prepareStatement(
						"UPDATE Assignment_4_Database.users SET balance = " + updateAmount + "WHERE UID = " + UID);
				ps.executeUpdate();
			} else {
				return "CannotAfford";
			}
			// System.out.println("inside portfolio");
			PreparedStatement ps2 = conn.prepareStatement(
					"INSERT INTO Assignment_4_Database.portfolio(UID, TICKER, QUANTITY, TICKERNAME, TOTALCOST)"
							+ "VALUES( \'" + UID + "\', \'" + s.getStockTicker() + "\', \'" + s.getQuantity() + "\', \'"
							+ s.getStockCompany() + "\', \'" + s.getTotalCost() + "\');");
			ps2.executeUpdate();
			ps2.close();
		} catch (SQLException e) {
			// System.out.println("Error in updating portfolio from database: Database.java
			// line 74");
			System.out.println("sqle: " + e.getMessage());
			return null;
		}
		return "Added";
	}

	public String deletePortfolio(Stock s, String UID) {
		try {

			PreparedStatement ps = conn.prepareStatement("SELECT * FROM assignment_4_database.portfolio WHERE UID = \'"
					+ UID + "\' AND TICKER = \'" + s.getStockTicker() + "\';");
			ResultSet rs = ps.executeQuery();

			Vector<Stock> portfolio = new Vector<Stock>();
			while (rs.next()) {
				Stock stk = new Stock(rs.getString("Ticker"), rs.getString("TickerName"), rs.getInt("Quantity"),
						rs.getDouble("TotalCost"));
				stk.setPID(rs.getInt("PID"));
				portfolio.add(stk);
			}
			rs.close();
			ps.close();

			Collections.sort(portfolio, new StockPortfolioComparator());

			int quantity = s.getQuantity();
			for (int i = 0; i < portfolio.size(); i++) {
				if (quantity == 0) {
					PreparedStatement ps4 = conn
							.prepareStatement("SELECT * FROM Assignment_4_Database.users WHERE UID=\'" + UID + "\';");
					ResultSet rs2 = ps4.executeQuery();

					double currentAmt = 0;
					if (rs2.next()) {
						currentAmt = rs2.getDouble("Balance");
						currentAmt += s.getTotalCost();
					}

					PreparedStatement ps5 = conn.prepareStatement("UPDATE Assignment_4_Database.users SET Balance = "
							+ currentAmt + "WHERE UID = \'" + UID + "\';");
					ps5.executeUpdate();

					return "Deleted";
				}

				if (portfolio.elementAt(i).getQuantity() <= quantity) {

					quantity = quantity - portfolio.elementAt(i).getQuantity();
					PreparedStatement ps2 = conn
							.prepareStatement("DELETE FROM Assignment_4_Database.portfolio WHERE PID=\'"
									+ portfolio.elementAt(i).getPID() + "\';");
					ps2.executeUpdate();
					ps2.close();

				} else {

					int updateBalance = portfolio.elementAt(i).getQuantity() - quantity;

					double updateCost = (portfolio.elementAt(i).getTotalCost() / portfolio.elementAt(i).getQuantity())
							* (portfolio.elementAt(i).getQuantity() - quantity);
					quantity = 0;

					PreparedStatement ps2 = conn
							.prepareStatement("UPDATE Assignment_4_Database.portfolio SET Quantity = \'" + updateBalance
									+ "\' WHERE PID = \'" + portfolio.elementAt(i).getPID() + "\';");
					ps2.executeUpdate();
					ps2.close();

					PreparedStatement ps3 = conn
							.prepareStatement("UPDATE Assignment_4_Database.portfolio SET TotalCost = \'" + updateCost
									+ "\' WHERE PID = \'" + portfolio.elementAt(i).getPID() + "\';");
					ps3.executeUpdate();

					ps3.close();
				}

			}

			PreparedStatement ps4 = conn
					.prepareStatement("SELECT * FROM Assignment_4_Database.users WHERE UID=\'" + UID + "\';");
			ResultSet rs2 = ps4.executeQuery();

			double currentAmt = 0;
			if (rs2.next()) {
				currentAmt = rs2.getDouble("Balance");
				currentAmt += s.getTotalCost();
			}

			PreparedStatement ps5 = conn.prepareStatement(
					"UPDATE Assignment_4_Database.users SET Balance = " + currentAmt + "WHERE UID = \'" + UID + "\';");
			ps5.executeUpdate();

			rs2.close();
			ps5.close();
			ps4.close();

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		return null;
	}

	public double canAfford(double amount, String UID) {
		try {

			PreparedStatement ps = conn
					.prepareStatement("SELECT * FROM Assignment_4_Database.users WHERE UID = " + UID);
			ResultSet rs = ps.executeQuery();

			double balance = 0;
			if (rs.next()) {
				balance = rs.getDouble("balance");
			}

			if (balance - amount >= 0) {
				return balance - amount;
			}
			rs.close();
			ps.close();

		} catch (SQLException e) {
			// System.out.println("Error in updating portfolio from database: Database.java
			// line 74");
			System.out.println("sqle: " + e.getMessage());

		}
		return -1;
	}

	public Vector<Stock> getPortfolioStock(String UID) {

		Vector<Stock> portfolio = new Vector<Stock>();
		// System.out.println(UID);
		try {

			PreparedStatement ps = conn
					.prepareStatement("SELECT * FROM Assignment_4_Database.portfolio WHERE UID= " + UID);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				portfolio.add(new Stock(rs.getString("Ticker"), rs.getString("TickerName"), rs.getInt("Quantity"),
						rs.getDouble("TotalCost")));
			}
			rs.close();
			ps.close();

		} catch (SQLException e) {
			// System.out.println("Error in getting portfolio from database: Database.java
			// line 59");
			System.out.println("sqle: " + e.getMessage());
		}

		return portfolio;
	}

	public double getAccountInfo(String UID) {

		try {

			PreparedStatement ps = conn.prepareStatement("SELECT * FROM Assignment_4_Database.users WHERE UID= " + UID);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				return rs.getDouble("balance");
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			// System.out.println("Error in getting portfolio from database: Database.java
			// line 59");
			System.out.println("sqle: " + e.getMessage());
		}

		return -1;
	}

	class StockPortfolioComparator implements Comparator<Stock> {
		@Override
		public int compare(Stock a, Stock b) {
			if (a.getPID() > b.getPID()) {
				return 1;
			} else if (a.getPID() < b.getPID()) {
				return -1;
			}
			return 0;
		}
	}

	/*
	 * public Boolean checkIfFavorite(String ticker) { try { st =
	 * conn.createStatement(); String statement =
	 * "SELECT * FROM Assignment_4_Database.users WHERE ticker = \'" + ticker +
	 * "\';"; ps = conn.prepareStatement(statement); rs = ps.executeQuery();
	 * 
	 * if (rs.next()) { return true; }
	 * 
	 * } catch (SQLException e) { System.out.
	 * println("Error in validating users from database: Database.java line 40");
	 * System.out.println("sqle: " + e.getMessage()); }
	 * 
	 * return false; }
	 */

	/*
	 * Test database
	 * 
	 * public static void main(String[] args) { Database db = new Database();
	 *
	 * 
	 * db.addUser(new User("Squishmallow", "squish@yahoo.com", "squihypw"));
	 * db.addFavoriteStock(new Stock("GOOG", "Google Inc", 123.34, 0.24));
	 * db.addPortfolio(new Stock("TSLA", "Tesla Inc", 5, 1345.345, 500.45632, 10.67,
	 * 10.67, 678.6));
	 * 
	 * Vector<Stock> s = db.getFavoriteStock(); System.out.println("Favorites"); for
	 * (int i = 0; i < s.size(); i++) {
	 * System.out.print(s.elementAt(i).getStockTicker() + " "); }
	 * 
	 * s = db.getPortfolioStock(); System.out.println();
	 * System.out.println("Portfolio"); for (int i = 0; i < s.size(); i++) {
	 * System.out.print(s.elementAt(i).getStockTicker() + " "); }
	 *
	 * System.out.println(); System.out.println(db.validateUser(new
	 * User("Squishmallow", "squish@yahoo.com", "squihypw")));
	 * System.out.println(db.validateUser(new User("Smallow", "squish@yahoo.com",
	 * "squihypw"))); System.out.println(db.validateUser(new User("Squishmallow",
	 * "squish@yahoo.com", "squipw"))); }
	 */
}
