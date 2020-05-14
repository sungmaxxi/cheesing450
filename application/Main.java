package application;
	
import java.io.IOException;
import java.net.URL;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.StringTokenizer;

public class Main extends Application {
	@FXML
	private TextField inputSSN;
	@FXML
	private Button confirmButton;
	@FXML
	private Text statusText;
	@FXML
	private Text welcomeScreen;
	private static Stage pStage;
	@FXML
	private TextField fnameText;
	@FXML
	private TextField minitText;
	@FXML
	private TextField lnameText;
	@FXML
	private TextField ssnText;
	@FXML
	private TextField bdateText;
	@FXML
	private TextField addressText;
	@FXML
	private TextField sexText;
	@FXML
	private TextField salaryText;
	@FXML
	private TextField superssnText;
	@FXML
	private TextField dnoText;
	@FXML
	private TextField emailText;
	@FXML
	private Button goToProjectButton;
	private static Connection conn;
	
	@FXML
	private CheckBox yesCheckBox;
	@FXML
	private CheckBox noCheckBox;
	private boolean askDependent;
	@FXML
	private Button FinalizeButton;
	@FXML
	private TextField pNoView;
	@FXML
	private TextField hoursWorkedView;
	@FXML
	private TextField dESSN;
	@FXML
	private TextField dName;
	@FXML
	private TextField dSex;
	@FXML
	private TextField dBdate;
	@FXML
	private TextField dRel;
	@FXML
	private Button almost;
	@FXML
	private TextArea reportView;
	
	private String result = "";
	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader loader = new FXMLLoader();
		URL theGUI = getClass().getResource("GUI.fxml");
		loader.setLocation(theGUI);
		Parent root = loader.load();
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.show();
		setpStage(primaryStage);
	}
	
	public static void main(String[] args) throws Exception {
		try
		{			
			Class.forName("oracle.jdbc.driver.OracleDriver");
		}
		catch(ClassNotFoundException x)
		{
			System.out.println("Driver could not be loaded");
		}
		conn = DriverManager.getConnection(
				"jdbc:oracle:thin:@artemis.vsnet.gmu.edu:1521/vse18c.vsnet.gmu.edu",
				"syang27",
				"oamteefa"
				);
		launch(args);
		}
	@FXML
	public void authenticate(ActionEvent event) throws Exception {
		boolean isAuthenticated = false;
		PreparedStatement p;
		ResultSet managerSSNs;
		String query = "SELECT DISTINCT supervisor.ssn"
				+ " FROM EMPLOYEE e, EMPLOYEE supervisor"
				+ " WHERE e.superssn = supervisor.ssn";
		p = conn.prepareStatement(query);
		p.clearParameters();
		managerSSNs = p.executeQuery();
		System.out.println(inputSSN.getText());
		while(managerSSNs.next()) {
			if(inputSSN.getText().contentEquals(managerSSNs.getString("ssn"))) {
				isAuthenticated = true;
			}
		}
		if(isAuthenticated) {
			addEmployeeScreen();
		} else {
			this.statusText.setText("Please get a manager");
		}
		
	}
	
	public void addEmployeeScreen() throws Exception {
		pStage = this.getpStage();
		Parent pane = FXMLLoader.load(getClass().getResource("EmployeeAddingScreen.fxml"));
		pStage.getScene().setRoot(pane);
		Thread.sleep(4000);
		if(welcomeScreen != null) {
			welcomeScreen.setVisible(false);
		}
	}

	public static Stage getpStage() {
		return pStage;
	}

	public static void setpStage(Stage pStage) {
		Main.pStage = pStage;
	}
	
	@FXML
	public void assignProjects(ActionEvent event) throws Exception {
		pStage = Main.getpStage();
		Parent pane = FXMLLoader.load(getClass().getResource("ProjectAssignment.fxml"));
		pStage.getScene().setRoot(pane);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
		java.util.Date jDate = sdf.parse(this.bdateText.getText());
		java.sql.Date sdate = new java.sql.Date(jDate.getTime());
		PreparedStatement p;
		String query = "insert into EMPLOYEE values (?,?,?,?,?,?,?,?,?,?,?)";
		p = conn.prepareStatement(query);
		p.clearParameters();
		p.setString(1, this.fnameText.getText());
		p.setString(2, this.minitText.getText());
		p.setString(3, this.lnameText.getText());
		p.setString(4, this.ssnText.getText());
		p.setDate(5, sdate);
		p.setString(6, this.addressText.getText());
		p.setString(7, this.sexText.getText());
		p.setInt(8, Integer.parseInt(this.salaryText.getText()));
		p.setString(9, this.superssnText.getText());
		p.setInt(10, Integer.parseInt(this.dnoText.getText()));
		p.setString(11, this.emailText.getText());
		p.executeUpdate();
		result = result + "The New Employee Is: " + this.fnameText.getText() 
		+ " " + this.minitText.getText() + ". " + this.lnameText.getText() + "\n";
	}
	@FXML
	public void onYesChecked(ActionEvent event) {
		this.noCheckBox.setSelected(false);
		this.askDependent = true;
	}
	@FXML
	public void onNoChecked(ActionEvent event) {
		this.yesCheckBox.setSelected(false);
		this.askDependent = false;
	}
	@FXML
	public void finalizeButtonClicked(ActionEvent event) throws Exception {
		String[] projects = this.pNoView.getText().split(",");
		String[] hoursWorked = this.hoursWorkedView.getText().split(",");
		int overtime = 0;
		for(int i = 0; i < hoursWorked.length;i++) {
			overtime +=Integer.parseInt(hoursWorked[i]);
		}
		if(projects.length != hoursWorked.length || overtime > 40) {
			this.FinalizeButton.setText("please doublecheck the input!");
		} else {
			String query = "insert into WORKS_ON values (?,?,?)";
			PreparedStatement p = conn.prepareStatement(query);
			p.clearParameters();
			result+= "Working on these departments with these many hours - ";
			for(int i =0; i < projects.length;i++) {
				p.clearParameters();
				p.setString(1, "Sung");//this.ssnText.getText());
				p.setInt(2, Integer.parseInt(projects[i].replace(":", "")));
				p.setInt(3, Integer.parseInt(hoursWorked[i].replace(":", "")));
				result += projects[i];
				result += hoursWorked[i];
				//p.executeUpdate();
			}
		}
		result+= '\n';
		if(this.yesCheckBox.isSelected()) {
			pStage = Main.getpStage();
			Parent pane = FXMLLoader.load(getClass().getResource("dependent.fxml"));
			pStage.getScene().setRoot(pane);
		} else {
			gatherReport();
		}

	}
	
	@FXML
	public void actualFinalize(ActionEvent event) throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
		java.util.Date jDate = sdf.parse(this.dBdate.getText());
		java.sql.Date sdate = new java.sql.Date(jDate.getTime());
		String query = "insert into dependent(?,?,?,?,?)";
		PreparedStatement p = conn.prepareStatement(query);
		p.clearParameters();
		p.setInt(1, Integer.parseInt(this.dESSN.getText()));
		p.setString(2, this.dName.getText());
		p.setDate(3, sdate);
		p.setString(4, this.dRel.getText());
		p.executeUpdate();
		System.out.println(this.dRel.getText());
		gatherReport();
		
	}
	
	private void gatherReport() throws Exception {
		pStage = Main.getpStage();
		Parent pane = FXMLLoader.load(getClass().getResource("finalReport.fxml"));
		pStage.getScene().setRoot(pane);
		String[] projects = this.pNoView.getText().split(",");
		String[] hoursWorked = this.hoursWorkedView.getText().split(",");
		this.reportView.setText("The New Employee Is: " + this.fnameText.getText() 
		+ " " + this.minitText.getText() + ". " + this.lnameText.getText() + "\n"
		+ "Working in these departments: " + projects.toString() + "\n"
		+ "Working these many hours respectively" + hoursWorked.toString() + "\n"
		+ "With the dependent(s)" + this.dName.getText() + " born " + this.dBdate.getText() + " " + this.dRel.getText());
		
		
	}
}
