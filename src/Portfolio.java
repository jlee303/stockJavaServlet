
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jlee3900_CSCI201L_Assignment4_v2.Database;
import jlee3900_CSCI201L_Assignment4_v2.Stock;

/**
 * Servlet implementation class Portfolio
 */
@WebServlet("/Portfolio")
public class Portfolio extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Database db = new Database();

	public Portfolio() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.flush();
		String filepath = this.getServletContext().getRealPath("portfolio.html");

		try {
			InputStream fin = new FileInputStream(filepath);
			int i = 0;
			while ((i = fin.read()) != -1) {
				out.write(i);
			}
			fin.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();

		String field = request.getParameter("field");
		String UID = request.getParameter("UID");

		if (field.equals("loadPortfolio")) {
			// System.out.println(UID);
			Vector<Stock> vs = db.getPortfolioStock(UID);

			int i = 0;

			while (i < vs.size()) {
				Boolean alreadyP = false;
				int index = i;

				for (int j = 0; j < i; j++) {
					if (vs.elementAt(j).getStockTicker().equals(vs.elementAt(i).getStockTicker())) {

						Stock s = new Stock(vs.elementAt(j).getStockTicker(), vs.elementAt(j).getStockCompany(),
								vs.elementAt(j).getQuantity() + vs.elementAt(i).getQuantity(),
								vs.elementAt(j).getTotalCost() + vs.elementAt(i).getTotalCost());
						s.setCurrentPrice(vs.elementAt(j).getCurrentPrice());
						s.setAverageCost(s.getTotalCost() / s.getQuantity());
						s.setMarketValue(s.getCurrentPrice() * s.getQuantity());
						s.setChange(s.getCurrentPrice() - s.getAverageCost());
						vs.set(j, s);
						alreadyP = true;
						index = j;
						vs.remove(i);
						break;
					}
				}
				if (!alreadyP) {
					String tiingoLink = "https://api.tiingo.com/iex?tickers=" + vs.elementAt(index).getStockTicker()
							+ "&token=0f7a21272ea6b3f39f27174758c4022c80e141da";
					try {
						URLConnection tiingo = new URL(tiingoLink).openConnection();
						BufferedReader in = new BufferedReader(new InputStreamReader(tiingo.getInputStream()));
						String line = in.readLine();
						int ind = line.indexOf("last\":");
						if (ind != -1) {
							line = line.substring(ind + 6, line.indexOf(",", ind));
						}

						double cp = Double.parseDouble(line);
						vs.elementAt(index).setCurrentPrice(cp);
						vs.elementAt(index)
								.setAverageCost(vs.elementAt(index).getTotalCost() / vs.elementAt(index).getQuantity());
						vs.elementAt(index).setMarketValue(cp * vs.elementAt(index).getQuantity());
						vs.elementAt(index).setChange(cp - vs.elementAt(index).getAverageCost());

					} catch (Exception e) {
						System.out.println("Error: " + e.getMessage());
					}
					i++;
				}

			}

			String vsJson = "[";
			for (i = 0; i < vs.size(); i++) {
				vsJson += "{\"stockTicker\": \"" + vs.elementAt(i).getStockTicker() + "\", \"stockCompany\": \""
						+ vs.elementAt(i).getStockCompany() + "\", \"quantity\": \"" + vs.elementAt(i).getQuantity()
						+ "\", \"totalCost\": \"" + vs.elementAt(i).getTotalCost() + "\", \"averageCost\": \""
						+ vs.elementAt(i).getAverageCost() + "\", \"currentPrice\": \""
						+ vs.elementAt(i).getCurrentPrice() + "\", \"change\": \"" + vs.elementAt(i).getChange()
						+ "\", \"marketValue\": \"" + vs.elementAt(i).getMarketValue() + "\"},";

			}
			vsJson = vsJson.substring(0, vsJson.length() - 1) + "]";
			if (vsJson.equals("]")) {
				vsJson = "[]";
			}
			out.println(vsJson);
		}

		if (field.equals("loadAccountInfo")) {
			double info = db.getAccountInfo(UID);
			Vector<Stock> vs = db.getPortfolioStock(UID);
			Double totalAcctValue = 0.0;

			for (int i = 0; i < vs.size(); i++) {
				String tiingoLink = "https://api.tiingo.com/iex?tickers=" + vs.elementAt(i).getStockTicker()
						+ "&token=0f7a21272ea6b3f39f27174758c4022c80e141da";

				URLConnection tiingo = new URL(tiingoLink).openConnection();
				BufferedReader in = new BufferedReader(new InputStreamReader(tiingo.getInputStream()));
				String line = in.readLine();
				int ind = line.indexOf("last\":");
				line = line.substring(ind + 6, line.indexOf(",", ind));

				double cp = Double.parseDouble(line);
				totalAcctValue += cp * vs.elementAt(i).getQuantity();
			}

			double amt = info + totalAcctValue;
			// System.out.println("Amount: " + amt + " Cash Balance: " + info);
			out.println("{ \"cashBalance\": \"" + info + "\", \"totalAccountValue\": \"" + amt + "\"}");
		}

		if (field.equals("buysell")) {
			String type = request.getParameter("type");
			String ticker = request.getParameter("ticker");
			String tickerName = request.getParameter("tickerName");
			int quantity = Integer.parseInt(request.getParameter("quantity"));
			Double total = Double.parseDouble(request.getParameter("total")) * quantity;

			// System.out.println(type + " " + ticker + " " + tickerName + " " + quantity +
			// " " + total);
			if (type.equals("sell")) {
				// System.out.println("Ere");
				out.println("{" + db.deletePortfolio(new Stock(ticker, tickerName, quantity, total), UID) + "}");
			} else {
				// System.out.println("Ere123");
				out.println("{" + db.addPortfolio(new Stock(ticker, tickerName, quantity, total), UID) + "}");
			}
		}
	}

}
