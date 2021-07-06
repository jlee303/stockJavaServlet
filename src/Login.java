
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jlee3900_CSCI201L_Assignment4_v2.User;

import jlee3900_CSCI201L_Assignment4_v2.Database;

/**
 * Servlet implementation class Login
 */
@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Database db = new Database();

	public Login() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.flush();
		String filepath = this.getServletContext().getRealPath("login.html");

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
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		String email = request.getParameter("email");

		if (field.equals("login")) {
			String UID = db.validateUser(username, password, false);
			if (UID != null) {
				out.println("{\"UID\":\"" + UID + "\", \"result\":\"success\"}");

			} else {
				out.println("{\"result\":\"Failure\"}");

			}
		}
		if (field.equals("loginGoogle")) {
			// google login should always return success
			String UID = db.validateUser(email, "", true);
			out.println("{\"UID\":\"" + UID + "\", \"result\":\"success\"}");

		}
		if (field.equals("signup")) {
			String UID = db.addUser(new User(username, email, password));
			if (UID != null) {
				if (UID.equals("AlreadyTaken")) {
					out.println("{\"UID\":\"" + UID + "\", \"result\":\"taken\"}");
				} else if (UID.equals("AccountReg")) {
					out.println("{\"UID\":\"" + UID + "\", \"result\":\"aregist\"}");
				} else {
					out.println("{\"UID\":\"" + UID + "\", \"result\":\"success\"}");
				}

			} else {
				out.println("{\"result\":\"Failure\"}");
			}
		}
		out.close();
	}

}
