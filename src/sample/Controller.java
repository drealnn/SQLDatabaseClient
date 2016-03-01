package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import javax.swing.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    public TextArea queryText;
    public Button submitBut;
    public SwingNode swingNode;

    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DATABASE_URL = "jdbc:mysql://localhost:3306/bikedb";
    static final String USERNAME= "root";
    static final String PASSWORD= "root";
    static final String DEFAULT_QUERY = "SELECT * FROM bikes\nwhere color = \"blue\"";
    public ChoiceBox driverBox;
    public ChoiceBox databaseBox;
    public TextField usernameBox;
    public TextField passwordBox;
    public Button ClearBt;
    public Button connectBt;
    public Label connectionLabel;

    public Connection connection;
    public boolean connectedToDatabase = false;

    String database;
    String username;
    String password;
    String driver;
    String query;

    private ResultSetTableModel tableModel;

    public void submitQuery(ActionEvent actionEvent) {
        try
        {
            // create TableModel for results of query SELECT * FROM bikes

            query = queryText.getText();
            tableModel = new ResultSetTableModel( connection, query, connectedToDatabase );

            // create JTable delegate for tableModel
            JTable resultTable = new JTable( tableModel );

            JScrollPane scroll = new JScrollPane(resultTable);

            swingNode.setContent(scroll);

            /*
            // perform a new query
            try
            {
                tableModel.setQuery( queryArea.getText() );
            } // end try
            catch ( SQLException sqlException )
            {
                JOptionPane.showMessageDialog( null,
                        sqlException.getMessage(), "Database error",
                        JOptionPane.ERROR_MESSAGE );

                // try to recover from invalid user query
                // by executing default query
                try
                {
                    tableModel.setQuery( DEFAULT_QUERY );
                    queryArea.setText( DEFAULT_QUERY );
                } // end try
                catch ( SQLException sqlException2 )
                {
                    JOptionPane.showMessageDialog( null,
                            sqlException2.getMessage(), "Database error",
                            JOptionPane.ERROR_MESSAGE );

                    // ensure database connection is closed
                    tableModel.disconnectFromDatabase();

                    System.exit( 1 ); // terminate application
                } // end inner catch
            } // end outer catch
            */

        } // end try
        catch ( ClassNotFoundException classNotFound )
        {
            JOptionPane.showMessageDialog( null,
                    "MySQL driver not found", "Driver not found",
                    JOptionPane.ERROR_MESSAGE );

            System.exit( 1 ); // terminate application
        } // end catch
        catch ( SQLException sqlException )
        {
            JOptionPane.showMessageDialog( null, sqlException.getMessage(),
                    "Database error", JOptionPane.ERROR_MESSAGE );

            // ensure database connection is closed
            tableModel.disconnectFromDatabase();

            System.exit( 1 );   // terminate application
        } // end catch
    }

    public void connectToDatabase(ActionEvent actionEvent) {

        try {
            username = usernameBox.getText();
            password = passwordBox.getText();

            Class.forName(driver);

            // connect to database
            connection = DriverManager.getConnection(database, username, password);

            connectionLabel.setText("Connected to " + database);

            // update database connectio
            // n status
            connectedToDatabase = true;
        }
        catch ( ClassNotFoundException classNotFound )
        {
            JOptionPane.showMessageDialog( null,
                    "MySQL driver not found", "Driver not found",
                    JOptionPane.ERROR_MESSAGE );

            connectedToDatabase = false;

            System.exit( 1 ); // terminate application
        } // end catch
        catch (SQLException sqlException)
        {
            JOptionPane.showMessageDialog( null, sqlException.getMessage(),
                    "Database error", JOptionPane.ERROR_MESSAGE );

            // ensure database connection is closed
            tableModel.disconnectFromDatabase();

            connectedToDatabase = false;
            System.exit( 1 );   // terminate application
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        databaseBox.setItems(FXCollections.observableArrayList(DATABASE_URL, "jdbc:mysql://localhost:3306/project3"));

        databaseBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                database = (String) newValue;

            }
        });

        driverBox.setItems(FXCollections.observableArrayList(JDBC_DRIVER));
        driverBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                driver = (String) newValue;

            }
        });

    }
}
