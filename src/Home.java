
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jlee3900_CSCI201L_Assignment4_v2.Database;
import jlee3900_CSCI201L_Assignment4_v2.Stock;

/**
 * Servlet implementation class Home
 */
@WebServlet("/Home")
public class Home extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Database db = new Database();

	public Home() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.flush();
		String filepath = this.getServletContext().getRealPath("home.html");

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
		String field = request.getParameter("field");
		String input = request.getParameter("inp");

		PrintWriter out = response.getWriter();
		response.setContentType("application/json");

		String tiingoLink2 = "https://api.tiingo.com/iex?tickers=" + input
				+ "&token=0f7a21272ea6b3f39f27174758c4022c80e141da";

		String tiingoLink1 = "https://api.tiingo.com/tiingo/daily/" + input
				+ "/prices?token=0f7a21272ea6b3f39f27174758c4022c80e141da";

		String tiingoLink = "https://api.tiingo.com/tiingo/daily/" + input
				+ "?token=0f7a21272ea6b3f39f27174758c4022c80e141da";

		try {
			if (field != null && field.equals("basicInfo")) {
				// Get the description using tiingoLink, which is D1 on the assignment
				URLConnection tiingo = new URL(tiingoLink).openConnection();
				BufferedReader in = new BufferedReader(new InputStreamReader(tiingo.getInputStream()));
				String line = in.readLine();
				// System.out.println(line);
				String isLoggedIn = request.getParameter("isLoggedIn");
				if (!isLoggedIn.equals("false")) {
					// If the user is logged in, snip from D3 the information needed in the header
					URLConnection tiingo1 = new URL(tiingoLink2).openConnection();
					BufferedReader in1 = new BufferedReader(new InputStreamReader(tiingo1.getInputStream()));
					String line2 = in1.readLine();
					int ind = line2.indexOf("last\":");
					// System.out.println(ind);
					if (ind != -1) {
						line = line.substring(0, line.length() - 1);
						line += ", \"last\":\"" + line2.substring(ind + 6, line2.indexOf(",", ind)) + "\"";
						// System.out.println(line2);

						ind = line2.indexOf("prevClose");
						line += ", \"prevClose\":\"" + line2.substring(ind + 11, line2.indexOf(",", ind)) + "\"}";
					}
				}

				out.println(line);

			}
			if (field != null && field.equals("advInfo")) {
				// Getting the prices of the stock
				String isLoggedIn = request.getParameter("isLoggedIn");
				if (isLoggedIn.equals("false")) {
					// If the user is not logged in use tiingoLink1, which is D2 in the assignment
					// page

					URLConnection tiingo1 = new URL(tiingoLink1).openConnection();
					BufferedReader in1 = new BufferedReader(new InputStreamReader(tiingo1.getInputStream()));
					String line = in1.readLine();
					out.println(line);

				} else {
					// If the user is logged in use tiingoLink2, which is D3 in the assignment page
					URLConnection tiingo2 = new URL(tiingoLink2).openConnection();
					BufferedReader in1 = new BufferedReader(new InputStreamReader(tiingo2.getInputStream()));
					String line = in1.readLine();
					out.println(line);

				}

			}
		} catch (FileNotFoundException e) {

			out.println("{\"result\":\"Fail\"}");
		}

		if (field != null && field.equals("favorite")) {
			String ticker = request.getParameter("ticker");
			String tickerName = request.getParameter("tickerName");
			String UID = request.getParameter("UID");

			String result = db.addFavoriteStock(new Stock(ticker, tickerName), UID);
			if (result == null) {
				out.println("{\"result\":\"Error\"}");
			} else if (result.equals("Added")) {
				out.println("{\"result\":\"Added\"}");
			} else {
				out.println("{\"result\":\"Deleted\"}");
			}

		}

		if (field != null && field.equals("portfolio")) {

			String ticker = request.getParameter("ticker");
			String tickerName = request.getParameter("tickerName");
			String quantity = request.getParameter("quantity");
			String cost = request.getParameter("cost");
			String UID = request.getParameter("UID");

			String result = db.addPortfolio(new Stock(ticker, tickerName, Integer.parseInt(quantity),
					Double.parseDouble(cost) * Integer.parseInt(quantity)), UID);
			if (result == null) {
				out.println("{\"result\":\"Error\"}");
			} else if (result.equals("Added")) {
				out.println("{\"result\":\"Added\"}");
			} else {
				out.println("{\"result\":\"CannotAfford\"}");
			}

		}

		/*
		 * if (field != null && field.equals("checkIfFav")) { String ticker =
		 * request.getParameter("ticker"); Boolean isFav = db.checkIfFavorite(ticker);
		 * if (isFav) { out.println("{\"result\":\"Favorite\"}"); } else {
		 * out.println("{\"result\":\"NotFavorite\"}"); } }
		 */

	}

}
