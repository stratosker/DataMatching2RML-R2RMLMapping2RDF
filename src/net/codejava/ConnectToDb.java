package net.codejava;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import java.sql.* ;  // for standard JDBC programs
import java.util.HashMap;


/**
 * Servlet implementation class ConnectToDb
 */
public class ConnectToDb extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ConnectToDb() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		System.out.println("CCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCc");
		
		HashMap<String,HashMap<String,String>> result=new HashMap<>();		
		
		try {
			
			
			if(request.getParameter("connectEngine").toLowerCase().equals("mysql")){
				Class.forName("com.mysql.jdbc.Driver");
			}
			else if(request.getParameter("connectEngine").toLowerCase().equals("postgresql")){
				Class.forName("org.postgresql.Driver");
			}
			else{
				System.out.println("only mysql and postgresql suitable!!");
			}
		
			
			//String connectionUrl = "jdbc:mysql://localhost:3306/db";
			String connectionUrl = "jdbc:"+request.getParameter("connectEngine").toLowerCase()+"://"+
					request.getParameter("connectHost")+":"+request.getParameter("connectPort")+
					"/"+request.getParameter("connectDbName");
				
			
			String connectionUser = request.getParameter("connectUsrname"); 
			String connectionPassword = request.getParameter("connectPsw"); 
			conn = DriverManager.getConnection(connectionUrl, connectionUser, connectionPassword);
			stmt = conn.createStatement();
		
			
			ResultSet rs2 = conn.getMetaData().getTables(null, null, "%", null);
			
			while (rs2.next()) {
			    
				 result.put(rs2.getString(3), new HashMap<String,String>());
				 
				 ResultSet rr = conn.getMetaData().getColumns(null, null, rs2.getString(3), null);
				 
				 HashMap<String, String> table = result.get(rs2.getString(3));
				 while(rr.next()){
					 table.put(rr.getString(4), rr.getString(6));
					 
				 }
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
		
			try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
			try { if (stmt != null) stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
			try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
		}
		
		
	      
		String json=new Gson().toJson(result);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		//System.out.println(json.toString());
		

		response.getWriter().write(json);

	}

}
