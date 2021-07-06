
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

@WebServlet("/Favorite")
public class Favorite extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Database db = new Database();

	public Favorite() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.flush();
		String filepath = this.getServletContext().getRealPath("favorite.html");

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
		if (field.equals("loadFavorites")) {
			Vector<Stock> vs = db.getFavoriteStock(UID);
			String vsJson = "[";

			for (int i = 0; i < vs.size(); i++) {

				double last = 0;
				double prevClose = 0;
				try {
					String tiingoLink = "https://api.tiingo.com/iex?tickers=" + vs.elementAt(i).getStockTicker()
							+ "&token=0f7a21272ea6b3f39f27174758c4022c80e141da";

					URLConnection tiingo = new URL(tiingoLink).openConnection();
					BufferedReader in = new BufferedReader(new InputStreamReader(tiingo.getInputStream()));
					String line = in.readLine();
					int ind = line.indexOf("last\":");

					if (ind != -1) {
						System.out.println(line);
						last = Double.parseDouble(line.substring(ind + 6, line.indexOf(",", ind)));
						ind = line.indexOf("prevClose");
						prevClose = Double.parseDouble(line.substring(ind + 11, line.indexOf(",", ind)));
					}

					String p = String.valueOf(last - prevClose);
					String p2 = String.valueOf(((last - prevClose) * 100) / prevClose);

					String changeP = "";
					if (p.indexOf(".") == -1 && p2.indexOf(".") == -1) {

						changeP = p + "(" + p2 + ")%";
					} else if (p.indexOf(".") == -1) {

						changeP = p + "(" + p2.substring(0, p2.indexOf(".") + 3) + ")%";
					} else if (p2.indexOf(".") == -1) {

						changeP = p.substring(0, p.indexOf(".") + 3) + "(" + p2 + ")%";
					} else {
						if (p.indexOf(".") + 3 >= p.length() || p2.indexOf(".") + 3 >= p2.length()) {
							changeP = p.substring(0, p.length()) + "(" + p2.substring(0, p2.length()) + ")%";
						} else {
							changeP = p.substring(0, p.indexOf(".") + 3) + "(" + p2.substring(0, p2.indexOf(".") + 3)
									+ ")%";
						}
					}

					vsJson += "{\"stockTicker\": \"" + vs.elementAt(i).getStockTicker() + "\", \"stockCompany\": \""
							+ vs.elementAt(i).getStockCompany() + "\", \"currentPrice\": \"" + last
							+ "\", \"changeP\": \"" + changeP + "\"},";

				} catch (Exception e) {
					System.out.println("An error has occured in Favoites: " + e.getMessage());
				}

			}
			vsJson = vsJson.substring(0, vsJson.length() - 1) + "]";
			if (vsJson.length() < 2) {
				vsJson = "null";
			}
			out.println(vsJson);

		}

		if (field.equals("deleteFavorite")) {
			String id = request.getParameter("id");
			UID = request.getParameter("UID");
			db.deleteFavoriteStock(new Stock(id, ""), UID);
		}
	}

}
