package jlee3900_CSCI201L_Assignment4_v2;

public class Stock {

	String stockTicker;
	String stockCompany;
	String stockTrade;
	String description;
	String startDate;

	int quantity;
	double totalCost;
	double averageCost;
	double currentPrice;
	double change;
	double marketValue;
	int PID;

	/*
	 * public Stock(String st, String sc, double q, double tc, double ac, double cp,
	 * double c, double mv) { stockTicker = st; stockCompany = sc;
	 * 
	 * quantity = q; totalCost = tc; averageCost = ac; currentPrice = cp; change =
	 * c; marketValue = mv; }
	 * 
	 * public Stock(String t, String sc, double p, double c) { stockTicker = t;
	 * stockCompany = sc; currentPrice = p; change = c; }
	 */

	public Stock(String t, String tn) {
		stockTicker = t;
		stockCompany = tn;
	}

	public Stock(String t, String tn, int q, double tc) {
		quantity = q;
		totalCost = tc;
		stockCompany = tn;
		stockTicker = t;
	}

	public String getStockTicker() {
		return stockTicker;
	}

	public String getStockCompany() {
		return stockCompany;
	}

	public String getStockTrade() {
		return stockTrade;
	}

	public String getDescription() {
		return description;
	}

	public String getStartDate() {
		return startDate;
	}

	public int getQuantity() {
		return quantity;
	}

	public double getTotalCost() {
		String str = String.format("%.2f", totalCost);
		return Double.parseDouble(str);
	}

	public double getAverageCost() {
		String str = String.format("%.2f", averageCost);
		return Double.parseDouble(str);
	}

	public double getCurrentPrice() {
		String str = String.format("%.2f", currentPrice);
		return Double.parseDouble(str);
	}

	public int getPID() {
		return PID;
	}

	public double getChange() {
		String str = String.format("%.2f", change);
		return Double.parseDouble(str);
	}

	public double getMarketValue() {
		String str = String.format("%.2f", marketValue);
		return Double.parseDouble(str);
	}

	public void setCurrentPrice(double cp) {
		currentPrice = cp;

	}

	public void setAverageCost(double d) {
		averageCost = d;

	}

	public void setMarketValue(double d) {
		marketValue = d;
	}

	public void setChange(double d) {
		change = d;
	}

	public void setPID(int i) {
		PID = i;
	}

}
