package database;

//import validation.CheckInput;
//import ErrorHandling.Validator;
import utilities.DateCalculations;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class DbHandler {

    private List<String> expiredPlates = new ArrayList<String>();
    // queries for owners table
    /* insert statement using prepared statements */

    public void insertVehicle(String plate, String activationDate, String ownerId)
    {
        MySqlConnect mySqlConnect = new MySqlConnect();
        String insertVehicleSql = "INSERT  INTO Vehicles (plate, activationDate, ownerId) VALUES (?,?,?) ";
        try {
            mySqlConnect.connect().setAutoCommit(false);
            PreparedStatement insertVehiclePrepared = mySqlConnect.connect().prepareStatement(insertVehicleSql);
            insertVehiclePrepared.setString(1, plate);
            insertVehiclePrepared.setString(2, activationDate);
            insertVehiclePrepared.setString(3, ownerId);
            insertVehiclePrepared.executeUpdate();
            mySqlConnect.connect().commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void insertOwner(String ownerId, String firstName, String lastName)
    {
        MySqlConnect mySqlConnect = new MySqlConnect();
        String insertOwnerSql = "INSERT  INTO Owners (ownerId, firstName, lastName) VALUES (?,?,?) ";
        try {
            mySqlConnect.connect().setAutoCommit(false);
            PreparedStatement insertOwnerPrepared = mySqlConnect.connect().prepareStatement(insertOwnerSql);
            insertOwnerPrepared.setString(1, ownerId);
            insertOwnerPrepared.setString(2, firstName);
            insertOwnerPrepared.setString(3, lastName);
            insertOwnerPrepared.executeUpdate();
            mySqlConnect.connect().commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public String getActivationDate(String plate)
    {
        MySqlConnect mySqlConnect = new MySqlConnect();
        String getActivationDateSql = "SELECT activationDate FROM vehicles WHERE plate = ?";
        String activationDate = "";
        String insuranceStatus = "";
        try {
            mySqlConnect.connect().setAutoCommit(false);
            PreparedStatement getActivationDatePrepared = mySqlConnect.connect().prepareStatement(getActivationDateSql);
            getActivationDatePrepared.setString(1, plate);
            ResultSet rs = getActivationDatePrepared.executeQuery();
            while(rs.next())
            {
                activationDate = rs.getString("activationDate");
            }
            DateCalculations dateCalculations = new DateCalculations();
            String expirationDate = dateCalculations.calculateExpirationDate(activationDate);
            insuranceStatus = dateCalculations.compareDates(expirationDate);
            mySqlConnect.connect().commit();
            return insuranceStatus;
        } catch (SQLException e) {
            e.printStackTrace();
            return "error - not found";
        }

    }

    public String getActivationDates(int timeFrame)
    {

        MySqlConnect mySqlConnect = new MySqlConnect();
        String getActivationDateSql = "SELECT plate, activationDate FROM vehicles";
        String activationDate = "";
        String insuranceStatus = "";
        String expiredPlate = "";
        try {
            mySqlConnect.connect().setAutoCommit(false);
            Statement stmt = mySqlConnect.connect().createStatement();
            ResultSet rs = stmt.executeQuery(getActivationDateSql);
            DateCalculations dateCalculations = new DateCalculations();
            while(rs.next())
            {
                activationDate = rs.getString("activationDate");
                expiredPlate = rs.getString("plate");
                String expirationDate = dateCalculations.calculateExpirationDate(activationDate);
                insuranceStatus = dateCalculations.compareDates(timeFrame, expirationDate);
                if (insuranceStatus.equals("expired")){
                    expiredPlates.add(expiredPlate);
                  //  System.out.println(expiredPlate);
                }
            }
           // System.out.println("\nNumber of expired plates found: " + expiredPlates.size());

            mySqlConnect.connect().commit();
            return insuranceStatus;
        } catch (SQLException e) {
            e.printStackTrace();
            return "error - not found";
        }
    }

    public List<String> getList(){
        return expiredPlates;
    }

}
