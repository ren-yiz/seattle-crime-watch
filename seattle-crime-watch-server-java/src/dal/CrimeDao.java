package dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import model.Crime;

public class CrimeDao {

  protected ConnectionManager connectionManager;
  private static CrimeDao instance = null;

  protected CrimeDao() {
    this.connectionManager = new ConnectionManager();
  }

  public static CrimeDao getInstance() {
    if (instance == null) {
      instance = new CrimeDao();
    }
    return instance;
  }

  //get a list of list of crimes by offenseName, start, end, and zipCode
  public List<ArrayList<Crime>> getCrimeByOffenseNameAndZipCode(List<Object> names, LocalDate start,
      LocalDate end, String zipCode)
      throws SQLException {
    long year = start.until(end, ChronoUnit.YEARS);
    long month = start.until(end, ChronoUnit.MONTHS);
    long day = start.until(end, ChronoUnit.DAYS);
    List<ArrayList<Crime>> res = new ArrayList<ArrayList<Crime>>();

    if (year > 0) {
      for (Object name : names) {
        ArrayList<Crime> listOfCrimes = new ArrayList<>();
        LocalDate temp = start;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
        while (temp.isBefore(end) || temp.isEqual(end)) {
          Crime crime = new Crime(String.valueOf(name), formatter.format(temp), 0);
          temp = temp.plusYears(1);
          listOfCrimes.add(crime);
        }
        res.add(listOfCrimes);
      }
      for (int i = 0; i < names.size(); i++) {
        String selectCrimes = "select *,count(*) as Count from\n"
            + "(select OffenseName, Date, ZipCode from\n"
            + "(select A.OffenseName, Date, LocationId\n"
            + "from\n"
            + "(select OffenseId,OffenseName,DATE_FORMAT(OffenseStartDateTime, \"%Y\") as Date\n"
            + "from OffenseTime\n"
            + "where OffenseStartDateTime > ? and OffenseStartDateTime < ? and OffenseName = ?) as A\n"
            + "inner join OffensePlace\n"
            + "on A.OffenseId = OffensePlace.OffenseId) as B\n"
            + "inner join Coordinate\n"
            + "on B.LocationId = Coordinate.LocationId) as C\n"
            + "where ZipCode = ?\n"
            + "group by Date";
        Connection connection = null;
        PreparedStatement selectStmt = null;
        ResultSet results = null;
        try {
          connection = connectionManager.getConnection();
          selectStmt = connection.prepareStatement(selectCrimes);
          selectStmt.setString(1, start.toString());
          selectStmt.setString(2, end.toString());
          selectStmt.setString(3, String.valueOf(names.get(i)));
          selectStmt.setString(4, zipCode);
          results = selectStmt.executeQuery();
          while (results.next()) {
            String resultYear = results.getString("Date");
            int count = results.getInt("Count");
            for (int j = 0; j < res.get(i).size(); j++) {
              if (res.get(i).get(j).getCrimeTime().equals(resultYear) && (
                  res.get(i).get(j).getCount() == 0)) {
                res.get(i).get(j).setCount(count);
                break;
              }
            }
          }
        } catch (SQLException throwables) {
          throwables.printStackTrace();
        } finally {
          if (connection != null) {
            connection.close();
          }
          if (selectStmt != null) {
            selectStmt.close();
          }
          if (results != null) {
            results.close();
          }
        }
      }
      return res;
    } else if (month > 0) {
      for (Object name : names) {
        ArrayList<Crime> listOfCrimes = new ArrayList<>();
        LocalDate temp = start;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        while (temp.isBefore(end) || temp.isEqual(end)) {
          Crime crime = new Crime(String.valueOf(name), formatter.format(temp), 0);
          temp = temp.plusMonths(1);
          listOfCrimes.add(crime);
        }
        res.add(listOfCrimes);
      }
      for (int i = 0; i < names.size(); i++) {
        String selectCrimes = "select *,count(*) as Count from\n"
            + "(select OffenseName, Date, ZipCode from\n"
            + "(select A.OffenseName, Date, LocationId\n"
            + "from\n"
            + "(select OffenseId,OffenseName,DATE_FORMAT(OffenseStartDateTime, \"%Y-%m\") as Date\n"
            + "from OffenseTime\n"
            + "where OffenseStartDateTime > ? and OffenseStartDateTime < ? and OffenseName = ?) as A\n"
            + "inner join OffensePlace\n"
            + "on A.OffenseId = OffensePlace.OffenseId) as B\n"
            + "inner join Coordinate\n"
            + "on B.LocationId = Coordinate.LocationId) as C\n"
            + "where ZipCode = ?\n"
            + "group by Date";
        Connection connection = null;
        PreparedStatement selectStmt = null;
        ResultSet results = null;
        try {
          connection = connectionManager.getConnection();
          selectStmt = connection.prepareStatement(selectCrimes);
          selectStmt.setString(1, start.toString());
          selectStmt.setString(2, end.toString());
          selectStmt.setString(3, String.valueOf(names.get(i)));
          selectStmt.setString(4, zipCode);
          results = selectStmt.executeQuery();
          while (results.next()) {
            String resultMonth = results.getString("Date");
            int count = results.getInt("Count");
            for (int j = 0; j < res.get(i).size(); j++) {
              if (res.get(i).get(j).getCrimeTime().equals(resultMonth) && (
                  res.get(i).get(j).getCount() == 0)) {
                res.get(i).get(j).setCount(count);
                break;
              }
            }
          }
        } catch (SQLException throwables) {
          throwables.printStackTrace();
        } finally {
          if (connection != null) {
            connection.close();
          }
          if (selectStmt != null) {
            selectStmt.close();
          }
          if (results != null) {
            results.close();
          }
        }
      }
      return res;
    } else {
      for (Object name : names) {
        ArrayList<Crime> listOfCrimes = new ArrayList<>();
        LocalDate temp = start;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        while (temp.isBefore(end) || temp.isEqual(end)) {
          Crime crime = new Crime(String.valueOf(name), formatter.format(temp), 0);
          temp = temp.plusDays(1);
          listOfCrimes.add(crime);
        }
        res.add(listOfCrimes);
      }
      for (int i = 0; i < names.size(); i++) {
        String selectCrimes = "select *,count(*) as Count from\n"
            + "(select OffenseName, Date, ZipCode from\n"
            + "(select A.OffenseName, Date, LocationId\n"
            + "from\n"
            + "(select OffenseId,OffenseName,DATE_FORMAT(OffenseStartDateTime, \"%Y-%m-%d\") as Date\n"
            + "from OffenseTime\n"
            + "where OffenseStartDateTime > ? and OffenseStartDateTime < ? and OffenseName = ?) as A\n"
            + "inner join OffensePlace\n"
            + "on A.OffenseId = OffensePlace.OffenseId) as B\n"
            + "inner join Coordinate\n"
            + "on B.LocationId = Coordinate.LocationId) as C\n"
            + "where ZipCode = ?\n"
            + "group by Date";
        Connection connection = null;
        PreparedStatement selectStmt = null;
        ResultSet results = null;
        try {
          connection = connectionManager.getConnection();
          selectStmt = connection.prepareStatement(selectCrimes);
          selectStmt.setString(1, start.toString());
          selectStmt.setString(2, end.toString());
          selectStmt.setString(3, String.valueOf(names.get(i)));
          selectStmt.setString(4, zipCode);
          results = selectStmt.executeQuery();
          while (results.next()) {
            String resultDay = results.getString("Date");
            int count = results.getInt("Count");
            for (int j = 0; j < res.get(i).size(); j++) {
              if (res.get(i).get(j).getCrimeTime().equals(resultDay) && (
                  res.get(i).get(j).getCount() == 0)) {
                res.get(i).get(j).setCount(count);
                break;
              }
            }
          }
        } catch (SQLException throwables) {
          throwables.printStackTrace();
        } finally {
          if (connection != null) {
            connection.close();
          }
          if (selectStmt != null) {
            selectStmt.close();
          }
          if (results != null) {
            results.close();
          }
        }
      }
      return res;
    }
  }

  //get a list of list of crimes by offenseParentName, start, end, and ZipCode
  public List<ArrayList<Crime>> getCrimeByOffenseParentNameAndZipCode(
      List<Object> names, LocalDate start, LocalDate end, String zipCode)
      throws SQLException {
    long year = start.until(end, ChronoUnit.YEARS);
    long month = start.until(end, ChronoUnit.MONTHS);
    long day = start.until(end, ChronoUnit.DAYS);
    //build and populate the list of list with all count = 0
    List<ArrayList<Crime>> res = new ArrayList<ArrayList<Crime>>();

    if (year > 0) {
      for (Object name : names) {
        ArrayList<Crime> listOfCrimes = new ArrayList<>();
        LocalDate temp = start;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
        while (temp.isBefore(end) || temp.isEqual(end)) {
          Crime crime = new Crime(String.valueOf(name), formatter.format(temp), 0);
          temp = temp.plusYears(1);
          listOfCrimes.add(crime);
        }
        res.add(listOfCrimes);
      }
      for (int i = 0; i < names.size(); i++) {
        String selectCrimes = "select *,count(*) as Count from\n"
            + "(select OffenseParentName, Date, ZipCode from\n"
            + "(select A.OffenseParentName, Date, LocationId\n"
            + "from\n"
            + "(select OffenseId,OffenseParentName,DATE_FORMAT(OffenseStartDateTime, \"%Y\") as Date\n"
            + "from OffenseTime inner join Offense\n"
            + "on OffenseTime.OffenseName = Offense.OffenseName\n"
            + "where OffenseStartDateTime > ? and OffenseStartDateTime < ? and OffenseParentName = ?) as A\n"
            + "inner join OffensePlace\n"
            + "on A.OffenseId = OffensePlace.OffenseId) as B\n"
            + "inner join Coordinate\n"
            + "on B.LocationId = Coordinate.LocationId) as C\n"
            + "where ZipCode = ?\n"
            + "group by Date";
        Connection connection = null;
        PreparedStatement selectStmt = null;
        ResultSet results = null;
        try {
          connection = connectionManager.getConnection();
          selectStmt = connection.prepareStatement(selectCrimes);
          selectStmt.setString(1, start.toString());
          selectStmt.setString(2, end.toString());
          selectStmt.setString(3, String.valueOf(names.get(i)));
          selectStmt.setString(4, zipCode);
          results = selectStmt.executeQuery();
          while (results.next()) {
            String resultYear = results.getString("Date");
            int count = results.getInt("Count");
            for (int j = 0; j < res.get(i).size(); j++) {
              if (res.get(i).get(j).getCrimeTime().contentEquals(resultYear) && (
                  res.get(i).get(j).getCount() == 0)) {
                res.get(i).get(j).setCount(count);
                break;
              }
            }
          }
        } catch (SQLException throwables) {
          throwables.printStackTrace();
        } finally {
          if (connection != null) {
            connection.close();
          }
          if (selectStmt != null) {
            selectStmt.close();
          }
          if (results != null) {
            results.close();
          }
        }
      }
      return res;
    } else if (month > 0) {
      for (Object name : names) {
        ArrayList<Crime> listOfCrimes = new ArrayList<>();
        LocalDate temp = start;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        while (temp.isBefore(end) || temp.isEqual(end)) {
          Crime crime = new Crime(String.valueOf(name), formatter.format(temp), 0);
          temp = temp.plusMonths(1);
          listOfCrimes.add(crime);
        }
        res.add(listOfCrimes);
      }
      for (int i = 0; i < names.size(); i++) {
        String selectCrimes = "select *,count(*) as Count from\n"
            + "(select OffenseParentName, Date, ZipCode from\n"
            + "(select A.OffenseParentName, Date, LocationId\n"
            + "from\n"
            + "(select OffenseId,OffenseParentName,DATE_FORMAT(OffenseStartDateTime, \"%Y-%m\") as Date\n"
            + "from OffenseTime inner join Offense\n"
            + "on OffenseTime.OffenseName = Offense.OffenseName\n"
            + "where OffenseStartDateTime > ? and OffenseStartDateTime < ? and OffenseParentName = ?) as A\n"
            + "inner join OffensePlace\n"
            + "on A.OffenseId = OffensePlace.OffenseId) as B\n"
            + "inner join Coordinate\n"
            + "on B.LocationId = Coordinate.LocationId) as C\n"
            + "where ZipCode = ?\n"
            + "group by Date";
        Connection connection = null;
        PreparedStatement selectStmt = null;
        ResultSet results = null;
        try {
          connection = connectionManager.getConnection();
          selectStmt = connection.prepareStatement(selectCrimes);
          selectStmt.setString(1, start.toString());
          selectStmt.setString(2, end.toString());
          selectStmt.setString(3, String.valueOf(names.get(i)));
          selectStmt.setString(4, zipCode);
          results = selectStmt.executeQuery();
          while (results.next()) {
            String resultMonth = results.getString("Date");
            int count = results.getInt("Count");
            for (int j = 0; j < res.get(i).size(); j++) {
              if (res.get(i).get(j).getCrimeTime().equals(resultMonth) && (
                  res.get(i).get(j).getCount() == 0)) {
                res.get(i).get(j).setCount(count);
                break;
              }
            }
          }
        } catch (SQLException throwables) {
          throwables.printStackTrace();
        } finally {
          if (connection != null) {
            connection.close();
          }
          if (selectStmt != null) {
            selectStmt.close();
          }
          if (results != null) {
            results.close();
          }
        }
      }
      return res;
    } else {
      for (Object name : names) {
        ArrayList<Crime> listOfCrimes = new ArrayList<>();
        LocalDate temp = start;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        while (temp.isBefore(end) || temp.isEqual(end)) {
          Crime crime = new Crime(String.valueOf(name), formatter.format(temp), 0);
          temp = temp.plusDays(1);
          listOfCrimes.add(crime);
        }
        res.add(listOfCrimes);
      }
      for (int i = 0; i < names.size(); i++) {
        String selectCrimes = "select *,count(*) as Count from\n"
            + "(select OffenseParentName, Date, ZipCode from\n"
            + "(select A.OffenseParentName, Date, LocationId\n"
            + "from\n"
            + "(select OffenseId,OffenseParentName,DATE_FORMAT(OffenseStartDateTime, \"%Y-%m-%d\") as Date\n"
            + "from OffenseTime inner join Offense\n"
            + "on OffenseTime.OffenseName = Offense.OffenseName\n"
            + "where OffenseStartDateTime > ? and OffenseStartDateTime < ? and OffenseParentName = ?) as A\n"
            + "inner join OffensePlace\n"
            + "on A.OffenseId = OffensePlace.OffenseId) as B\n"
            + "inner join Coordinate\n"
            + "on B.LocationId = Coordinate.LocationId) as C\n"
            + "where ZipCode = ?\n"
            + "group by Date";
        Connection connection = null;
        PreparedStatement selectStmt = null;
        ResultSet results = null;
        try {
          connection = connectionManager.getConnection();
          selectStmt = connection.prepareStatement(selectCrimes);
          selectStmt.setString(1, start.toString());
          selectStmt.setString(2, end.toString());
          selectStmt.setString(3, String.valueOf(names.get(i)));
          selectStmt.setString(4, zipCode);
          results = selectStmt.executeQuery();
          while (results.next()) {
            String resultDay = results.getString("Date");
            int count = results.getInt("Count");
            for (int j = 0; j < res.get(i).size(); j++) {
              if (res.get(i).get(j).getCrimeTime().equals(resultDay) && (
                  res.get(i).get(j).getCount() == 0)) {
                res.get(i).get(j).setCount(count);
                break;
              }
            }
          }
        } catch (SQLException throwables) {
          throwables.printStackTrace();
        } finally {
          if (connection != null) {
            connection.close();
          }
          if (selectStmt != null) {
            selectStmt.close();
          }
          if (results != null) {
            results.close();
          }
        }
      }
      return res;
    }
  }


  //get a list of list of crimes by offenseName, start, end, and MCPP
  public List<ArrayList<Crime>> getCrimeByOffenseNameAndMCPP(List<Object> names,
      LocalDate start, LocalDate end, String MCPP)
      throws SQLException {
    long year = start.until(end, ChronoUnit.YEARS);
    long month = start.until(end, ChronoUnit.MONTHS);
    long day = start.until(end, ChronoUnit.DAYS);
    //build and populate the list of list with all count = 0
    List<ArrayList<Crime>> res = new ArrayList<ArrayList<Crime>>();

    if (year > 0) {
      for (Object name : names) {
        ArrayList<Crime> listOfCrimes = new ArrayList<>();
        LocalDate temp = start;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
        while (temp.isBefore(end) || temp.isEqual(end)) {
          Crime crime = new Crime(String.valueOf(name), formatter.format(temp), 0);
          temp = temp.plusYears(1);
          listOfCrimes.add(crime);
        }
        res.add(listOfCrimes);
      }
      for (int i = 0; i < names.size(); i++) {
        String selectCrimes = "select *,count(*) as Count from\n"
            + "(select A.OffenseName as OffenseName, Date, MCPPName from\n"
            + "(select OffenseId,OffenseName,DATE_FORMAT(OffenseStartDateTime, \"%Y\") as Date\n"
            + "from OffenseTime\n"
            + "where OffenseStartDateTime > ? and OffenseStartDateTime < ? and OffenseName = ?) as A\n"
            + "inner join OffensePlace\n"
            + "on A.OffenseId = OffensePlace.OffenseId) as B\n"
            + "where MCPPName = ?\n"
            + "group by Date";
        Connection connection = null;
        PreparedStatement selectStmt = null;
        ResultSet results = null;
        try {
          connection = connectionManager.getConnection();
          selectStmt = connection.prepareStatement(selectCrimes);
          selectStmt.setString(1, start.toString());
          selectStmt.setString(2, end.toString());
          selectStmt.setString(3, String.valueOf(names.get(i)));
          selectStmt.setString(4, MCPP);
          results = selectStmt.executeQuery();
          while (results.next()) {
            String resultYear = results.getString("Date");
            int count = results.getInt("Count");
            for (int j = 0; j < res.get(i).size(); j++) {
              if (res.get(i).get(j).getCrimeTime().equals(resultYear) && (
                  res.get(i).get(j).getCount() == 0)) {
                res.get(i).get(j).setCount(count);
                break;
              }
            }
          }
        } catch (SQLException throwables) {
          throwables.printStackTrace();
        } finally {
          if (connection != null) {
            connection.close();
          }
          if (selectStmt != null) {
            selectStmt.close();
          }
          if (results != null) {
            results.close();
          }
        }
      }
      return res;
    } else if (month > 0) {
      for (Object name : names) {
        ArrayList<Crime> listOfCrimes = new ArrayList<>();
        LocalDate temp = start;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        while (temp.isBefore(end) || temp.isEqual(end)) {
          Crime crime = new Crime(String.valueOf(name), formatter.format(temp), 0);
          temp = temp.plusMonths(1);
          listOfCrimes.add(crime);
        }
        res.add(listOfCrimes);
      }
      for (int i = 0; i < names.size(); i++) {
        String selectCrimes = "select *,count(*) as Count from\n"
            + "(select A.OffenseName as OffenseName, Date, MCPPName from\n"
            + "(select OffenseId,OffenseName,DATE_FORMAT(OffenseStartDateTime, \"%Y-%m\") as Date\n"
            + "from OffenseTime\n"
            + "where OffenseStartDateTime > ? and OffenseStartDateTime < ? and OffenseName = ?) as A\n"
            + "inner join OffensePlace\n"
            + "on A.OffenseId = OffensePlace.OffenseId) as B\n"
            + "where MCPPName = ?\n"
            + "group by Date";
        Connection connection = null;
        PreparedStatement selectStmt = null;
        ResultSet results = null;
        try {
          connection = connectionManager.getConnection();
          selectStmt = connection.prepareStatement(selectCrimes);
          selectStmt.setString(1, start.toString());
          selectStmt.setString(2, end.toString());
          selectStmt.setString(3, String.valueOf(names.get(i)));
          selectStmt.setString(4, MCPP);
          results = selectStmt.executeQuery();
          while (results.next()) {
            String resultMonth = results.getString("Date");
            int count = results.getInt("Count");
            for (int j = 0; j < res.get(i).size(); j++) {
              if (res.get(i).get(j).getCrimeTime().equals(resultMonth) && (
                  res.get(i).get(j).getCount() == 0)) {
                res.get(i).get(j).setCount(count);
                break;
              }
            }
          }
        } catch (SQLException throwables) {
          throwables.printStackTrace();
        } finally {
          if (connection != null) {
            connection.close();
          }
          if (selectStmt != null) {
            selectStmt.close();
          }
          if (results != null) {
            results.close();
          }
        }
      }
      return res;
    } else {
      for (Object name : names) {
        ArrayList<Crime> listOfCrimes = new ArrayList<>();
        LocalDate temp = start;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        while (temp.isBefore(end) || temp.isEqual(end)) {
          Crime crime = new Crime(String.valueOf(name), formatter.format(temp), 0);
          temp = temp.plusDays(1);
          listOfCrimes.add(crime);
        }
        res.add(listOfCrimes);
      }
      for (int i = 0; i < names.size(); i++) {
        String selectCrimes = "select *,count(*) as Count from\n"
            + "(select A.OffenseName as OffenseName, Date, MCPPName from\n"
            + "(select OffenseId,OffenseName,DATE_FORMAT(OffenseStartDateTime, \"%Y-%m-%d\") as Date\n"
            + "from OffenseTime\n"
            + "where OffenseStartDateTime > ? and OffenseStartDateTime < ? and OffenseName = ?) as A\n"
            + "inner join OffensePlace\n"
            + "on A.OffenseId = OffensePlace.OffenseId) as B\n"
            + "where MCPPName = ?\n"
            + "group by Date";
        Connection connection = null;
        PreparedStatement selectStmt = null;
        ResultSet results = null;
        try {
          connection = connectionManager.getConnection();
          selectStmt = connection.prepareStatement(selectCrimes);
          selectStmt.setString(1, start.toString());
          selectStmt.setString(2, end.toString());
          selectStmt.setString(3, String.valueOf(names.get(i)));
          selectStmt.setString(4, MCPP);
          results = selectStmt.executeQuery();
          while (results.next()) {
            String resultDay = results.getString("Date");
            int count = results.getInt("Count");
            for (int j = 0; j < res.get(i).size(); j++) {
              if (res.get(i).get(j).getCrimeTime().equals(resultDay) && (
                  res.get(i).get(j).getCount() == 0)) {
                res.get(i).get(j).setCount(count);
                break;
              }
            }
          }
        } catch (SQLException throwables) {
          throwables.printStackTrace();
        } finally {
          if (connection != null) {
            connection.close();
          }
          if (selectStmt != null) {
            selectStmt.close();
          }
          if (results != null) {
            results.close();
          }
        }
      }
      return res;
    }
  }

  //get a list of list of crimes by offenseParentNames, start, end, and MCPP
  public List<ArrayList<Crime>> getCrimeByOffenseParentNameAndMCPP(List<Object> names,
      LocalDate start, LocalDate end, String MCPP)
      throws SQLException {
    long year = start.until(end, ChronoUnit.YEARS);
    long month = start.until(end, ChronoUnit.MONTHS);
    long day = start.until(end, ChronoUnit.DAYS);
    //build and populate the list of list with all count = 0
    List<ArrayList<Crime>> res = new ArrayList<ArrayList<Crime>>();

    if (year > 0) {
      for (Object name : names) {
        ArrayList<Crime> listOfCrimes = new ArrayList<>();
        LocalDate temp = start;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
        while (temp.isBefore(end) || temp.isEqual(end)) {
          Crime crime = new Crime(String.valueOf(name), formatter.format(temp), 0);
          temp = temp.plusYears(1);
          listOfCrimes.add(crime);
        }
        res.add(listOfCrimes);
      }
      for (int i = 0; i < names.size(); i++) {
        String selectCrimes = "select *,count(*) as Count from\n"
            + "(select A.offenseParentName as offenseParentName, Date, MCPPName from\n"
            + "(select OffenseId,OffenseParentName,DATE_FORMAT(OffenseStartDateTime, \"%Y\") as Date\n"
            + "from OffenseTime inner join Offense\n"
            + "on OffenseTime.OffenseName = Offense.OffenseName\n"
            + "where OffenseStartDateTime > ? and OffenseStartDateTime < ? and OffenseParentName = ?) as A\n"
            + "inner join OffensePlace\n"
            + "on A.OffenseId = OffensePlace.OffenseId) as B\n"
            + "where MCPPName = ?\n"
            + "group by Date";
        Connection connection = null;
        PreparedStatement selectStmt = null;
        ResultSet results = null;
        try {
          connection = connectionManager.getConnection();
          selectStmt = connection.prepareStatement(selectCrimes);
          selectStmt.setString(1, start.toString());
          selectStmt.setString(2, end.toString());
          selectStmt.setString(3, String.valueOf(names.get(i)));
          selectStmt.setString(4, MCPP);
          results = selectStmt.executeQuery();
          while (results.next()) {
            String resultYear = results.getString("Date");
            int count = results.getInt("Count");
            for (int j = 0; j < res.get(i).size(); j++) {
              if (res.get(i).get(j).getCrimeTime().equals(resultYear) && (
                  res.get(i).get(j).getCount() == 0)) {
                res.get(i).get(j).setCount(count);
                break;
              }
            }
          }
        } catch (SQLException throwables) {
          throwables.printStackTrace();
        } finally {
          if (connection != null) {
            connection.close();
          }
          if (selectStmt != null) {
            selectStmt.close();
          }
          if (results != null) {
            results.close();
          }
        }
      }
      return res;
    } else if (month > 0) {
      for (Object name : names) {
        ArrayList<Crime> listOfCrimes = new ArrayList<>();
        LocalDate temp = start;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        while (temp.isBefore(end) || temp.isEqual(end)) {
          Crime crime = new Crime(String.valueOf(name), formatter.format(temp), 0);
          temp = temp.plusMonths(1);
          listOfCrimes.add(crime);
        }
        res.add(listOfCrimes);
      }
      for (int i = 0; i < names.size(); i++) {
        String selectCrimes = "select *,count(*) as Count from\n"
            + "(select A.offenseParentName as offenseParentName, Date, MCPPName from\n"
            + "(select OffenseId,OffenseParentName,DATE_FORMAT(OffenseStartDateTime, \"%Y-%m\") as Date\n"
            + "from OffenseTime inner join Offense\n"
            + "on OffenseTime.OffenseName = Offense.OffenseName\n"
            + "where OffenseStartDateTime > ? and OffenseStartDateTime < ? and OffenseParentName = ?) as A\n"
            + "inner join OffensePlace\n"
            + "on A.OffenseId = OffensePlace.OffenseId) as B\n"
            + "where MCPPName = ?\n"
            + "group by Date";
        Connection connection = null;
        PreparedStatement selectStmt = null;
        ResultSet results = null;
        try {
          connection = connectionManager.getConnection();
          selectStmt = connection.prepareStatement(selectCrimes);
          selectStmt.setString(1, start.toString());
          selectStmt.setString(2, end.toString());
          selectStmt.setString(3, String.valueOf(names.get(i)));
          selectStmt.setString(4, MCPP);
          results = selectStmt.executeQuery();
          while (results.next()) {
            String resultMonth = results.getString("Date");
            int count = results.getInt("Count");
            for (int j = 0; j < res.get(i).size(); j++) {
              if (res.get(i).get(j).getCrimeTime().equals(resultMonth) && (
                  res.get(i).get(j).getCount() == 0)) {
                res.get(i).get(j).setCount(count);
                break;
              }
            }
          }
        } catch (SQLException throwables) {
          throwables.printStackTrace();
        } finally {
          if (connection != null) {
            connection.close();
          }
          if (selectStmt != null) {
            selectStmt.close();
          }
          if (results != null) {
            results.close();
          }
        }
      }
      return res;
    } else {
      for (Object name : names) {
        ArrayList<Crime> listOfCrimes = new ArrayList<>();
        LocalDate temp = start;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        while (temp.isBefore(end) || temp.isEqual(end)) {
          Crime crime = new Crime(String.valueOf(name), formatter.format(temp), 0);
          temp = temp.plusDays(1);
          listOfCrimes.add(crime);
        }
        res.add(listOfCrimes);
      }
      for (int i = 0; i < names.size(); i++) {
        String selectCrimes = "select *,count(*) as Count from\n"
            + "(select A.offenseParentName as offenseParentName, Date, MCPPName from\n"
            + "(select OffenseId,OffenseParentName,DATE_FORMAT(OffenseStartDateTime, \"%Y-%m-%d\") as Date\n"
            + "from OffenseTime inner join Offense\n"
            + "on OffenseTime.OffenseName = Offense.OffenseName\n"
            + "where OffenseStartDateTime > ? and OffenseStartDateTime < ? and OffenseParentName = ?) as A\n"
            + "inner join OffensePlace\n"
            + "on A.OffenseId = OffensePlace.OffenseId) as B\n"
            + "where MCPPName = ?\n"
            + "group by Date";
        Connection connection = null;
        PreparedStatement selectStmt = null;
        ResultSet results = null;
        try {
          connection = connectionManager.getConnection();
          selectStmt = connection.prepareStatement(selectCrimes);
          selectStmt.setString(1, start.toString());
          selectStmt.setString(2, end.toString());
          selectStmt.setString(3, String.valueOf(names.get(i)));
          selectStmt.setString(4, MCPP);
          results = selectStmt.executeQuery();
          while (results.next()) {
            String resultDay = results.getString("Date");
            int count = results.getInt("Count");
            for (int j = 0; j < res.get(i).size(); j++) {
              if (res.get(i).get(j).getCrimeTime().equals(resultDay) && (
                  res.get(i).get(j).getCount() == 0)) {
                res.get(i).get(j).setCount(count);
                break;
              }
            }
          }
        } catch (SQLException throwables) {
          throwables.printStackTrace();
        } finally {
          if (connection != null) {
            connection.close();
          }
          if (selectStmt != null) {
            selectStmt.close();
          }
          if (results != null) {
            results.close();
          }
        }
      }
      return res;
    }
  }

  // get a list of crimes by category, start, end and zipcode
  public List<ArrayList<Crime>> getCrimeByCategoryAndZipcode(List<Object> names,
      LocalDate start, LocalDate end, String zipCode) //object?
      throws SQLException {
    long year = start.until(end, ChronoUnit.YEARS);
    long month = start.until(end, ChronoUnit.MONTHS);
    long day = start.until(end, ChronoUnit.DAYS);
    List<ArrayList<Crime>> res = new ArrayList<ArrayList<Crime>>();

    if (year > 0) {
      for (Object name : names) {
        ArrayList<Crime> listOfCrimes = new ArrayList<>();
        LocalDate temp = start;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
        while (temp.isBefore(end) || temp.isEqual(end)) {
          Crime crime = new Crime(String.valueOf(name), formatter.format(temp), 0);
          temp = temp.plusYears(1);
          listOfCrimes.add(crime);
        }
        res.add(listOfCrimes);
      }

      for (int i = 0; i < names.size(); i++) {
        String selectCrimes = "select *,count(*) as Count from\n"
            + "(select Category, Date, ZipCode from\n"
            + "(select A.CrimeAgainstCategory as Category, Date, LocationId\n"
            + "from\n"
            + "(select OffenseId,CrimeAgainstCategory,DATE_FORMAT(OffenseStartDateTime, \"%Y\") as Date\n"
            + "from OffenseTime inner join Offense\n"
            + "on OffenseTime.OffenseName = Offense.OffenseName\n"
            + "where OffenseStartDateTime > ? and OffenseStartDateTime < ? and CrimeAgainstCategory = ?) as A\n"
            + "inner join OffensePlace\n"
            + "on A.OffenseId = OffensePlace.OffenseId) as B\n"
            + "inner join Coordinate\n"
            + "on B.LocationId = Coordinate.LocationId) as C\n"
            + "where ZipCode = ?\n"
            + "group by Date";
        Connection connection = null;
        PreparedStatement selectStmt = null;
        ResultSet results = null;
        try {
          connection = connectionManager.getConnection();
          selectStmt = connection.prepareStatement(selectCrimes);
          selectStmt.setString(1, start.toString());
          selectStmt.setString(2, end.toString());
          selectStmt.setString(3, String.valueOf(names.get(i)));
          selectStmt.setString(4, zipCode);
          results = selectStmt.executeQuery();
          while (results.next()) {
            String resultYear = results.getString("Date");
            int count = results.getInt("Count");
            for (int j = 0; j < res.get(i).size(); j++) {
              if (res.get(i).get(j).getCrimeTime().equals(resultYear) && (
                  res.get(i).get(j).getCount() == 0)) {
                res.get(i).get(j).setCount(count);
                break;
              }
            }
          }
        } catch (SQLException throwables) {
          throwables.printStackTrace();
        } finally {
          if (connection != null) {
            connection.close();
          }
          if (selectStmt != null) {
            selectStmt.close();
          }
          if (results != null) {
            results.close();
          }
        }
      }
      return res;
    } else if (month > 0) {
      for (Object name : names) {
        ArrayList<Crime> listOfCrimes = new ArrayList<>();
        LocalDate temp = start;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        while (temp.isBefore(end) || temp.isEqual(end)) {
          Crime crime = new Crime(String.valueOf(name), formatter.format(temp), 0);
          temp = temp.plusMonths(1);
          listOfCrimes.add(crime);
        }
        res.add(listOfCrimes);
      }
      for (int i = 0; i < names.size(); i++) {
        String selectCrimes = "select *,count(*) as Count from\n"
            + "(select Category, Date, ZipCode from\n"
            + "(select A.CrimeAgainstCategory as Category, Date, LocationId\n"
            + "from\n"
            + "(select OffenseId,CrimeAgainstCategory,DATE_FORMAT(OffenseStartDateTime, \"%Y-%m\") as Date\n"
            + "from OffenseTime inner join Offense\n"
            + "on OffenseTime.OffenseName = Offense.OffenseName\n"
            + "where OffenseStartDateTime > ? and OffenseStartDateTime < ? and CrimeAgainstCategory = ?) as A\n"
            + "inner join OffensePlace\n"
            + "on A.OffenseId = OffensePlace.OffenseId) as B\n"
            + "inner join Coordinate\n"
            + "on B.LocationId = Coordinate.LocationId) as C\n"
            + "where ZipCode = ?\n"
            + "group by Date";
        Connection connection = null;
        PreparedStatement selectStmt = null;
        ResultSet results = null;
        try {
          connection = connectionManager.getConnection();
          selectStmt = connection.prepareStatement(selectCrimes);
          selectStmt.setString(1, start.toString());
          selectStmt.setString(2, end.toString());
          selectStmt.setString(3, String.valueOf(names.get(i)));
          selectStmt.setString(4, zipCode);
          results = selectStmt.executeQuery();
          while (results.next()) {
            String resultMonth = results.getString("Date");
            int count = results.getInt("Count");
            for (int j = 0; j < res.get(i).size(); j++) {
              if (res.get(i).get(j).getCrimeTime().equals(resultMonth) && (
                  res.get(i).get(j).getCount() == 0)) {
                res.get(i).get(j).setCount(count);
                break;
              }
            }
          }
        } catch (SQLException throwables) {
          throwables.printStackTrace();
        } finally {
          if (connection != null) {
            connection.close();
          }
          if (selectStmt != null) {
            selectStmt.close();
          }
          if (results != null) {
            results.close();
          }
        }
      }
      return res;

    } else {
      for (Object name : names) {
        ArrayList<Crime> listOfCrimes = new ArrayList<>();
        LocalDate temp = start;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        while (temp.isBefore(end) || temp.isEqual(end)) {
          Crime crime = new Crime(String.valueOf(name), formatter.format(temp), 0);
          temp = temp.plusDays(1);
          listOfCrimes.add(crime);
        }
        res.add(listOfCrimes);
      }
      for (int i = 0; i < names.size(); i++) {
        String selectCrimes = "select *,count(*) as Count from\n"
            + "(select Category, Date, ZipCode from\n"
            + "(select A.CrimeAgainstCategory as Category, Date, LocationId\n"
            + "from\n"
            + "(select OffenseId,CrimeAgainstCategory,DATE_FORMAT(OffenseStartDateTime, \"%Y-%m-%d\") as Date\n"
            + "from OffenseTime inner join Offense\n"
            + "on OffenseTime.OffenseName = Offense.OffenseName\n"
            + "where OffenseStartDateTime > ? and OffenseStartDateTime < ? and CrimeAgainstCategory = ?) as A\n"
            + "inner join OffensePlace\n"
            + "on A.OffenseId = OffensePlace.OffenseId) as B\n"
            + "inner join Coordinate\n"
            + "on B.LocationId = Coordinate.LocationId) as C\n"
            + "where ZipCode = ?\n"
            + "group by Date";
        Connection connection = null;
        PreparedStatement selectStmt = null;
        ResultSet results = null;
        try {
          connection = connectionManager.getConnection();
          selectStmt = connection.prepareStatement(selectCrimes);
          selectStmt.setString(1, start.toString());
          selectStmt.setString(2, end.toString());
          selectStmt.setString(3, String.valueOf(names.get(i)));
          selectStmt.setString(4, zipCode);
          results = selectStmt.executeQuery();
          while (results.next()) {
            String resultDay = results.getString("Date");
            int count = results.getInt("Count");
            for (int j = 0; j < res.get(i).size(); j++) {
              if (res.get(i).get(j).getCrimeTime().equals(resultDay) && (
                  res.get(i).get(j).getCount() == 0)) {
                res.get(i).get(j).setCount(count);
                break;
              }
            }
          }
        } catch (SQLException throwables) {
          throwables.printStackTrace();
        } finally {
          if (connection != null) {
            connection.close();
          }
          if (selectStmt != null) {
            selectStmt.close();
          }
          if (results != null) {
            results.close();
          }
        }
      }
      return res;
    }
  }

  // get a list of crimes by category, start, end and MCPP
  public List<ArrayList<Crime>> getCrimeByCategoryAndMCPP(List<Object> names, LocalDate start,
      LocalDate end, String MCPP)
      throws SQLException {

    long year = start.until(end, ChronoUnit.YEARS);
    long month = start.until(end, ChronoUnit.MONTHS);
    long day = start.until(end, ChronoUnit.DAYS);
    List<ArrayList<Crime>> res = new ArrayList<ArrayList<Crime>>();

    if (year > 0) {
      for (Object name : names) {
        ArrayList<Crime> listOfCrimes = new ArrayList<>();
        LocalDate temp = start;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
        while (temp.isBefore(end) || temp.isEqual(end)) {
          Crime crime = new Crime(String.valueOf(name), formatter.format(temp), 0);
          temp = temp.plusYears(1);
          listOfCrimes.add(crime);
        }
        res.add(listOfCrimes);
      }
      for (int i = 0; i < names.size(); i++) {
        String selectCrimes = "select *,count(*) as Count from\n"
            + "(select A.CrimeAgainstCategory as Category, Date, MCPPName from\n"
            + "(select OffenseId,CrimeAgainstCategory,DATE_FORMAT(OffenseStartDateTime, \"%Y\") as Date\n"
            + "from OffenseTime inner join Offense\n"
            + "where OffenseStartDateTime > ? and OffenseStartDateTime < ? and CrimeAgainstCategory = ?) as A\n"
            + "inner join OffensePlace\n"
            + "on A.OffenseId = OffensePlace.OffenseId) as B\n"
            + "where MCPPName = ?\n"
            + "group by Date";
        Connection connection = null;
        PreparedStatement selectStmt = null;
        ResultSet results = null;
        try {
          connection = connectionManager.getConnection();
          selectStmt = connection.prepareStatement(selectCrimes);
          selectStmt.setString(1, start.toString());
          selectStmt.setString(2, end.toString());
          selectStmt.setString(3, String.valueOf(names.get(i)));
          selectStmt.setString(4, MCPP);
          results = selectStmt.executeQuery();
          while (results.next()) {
            String resultYear = results.getString("Date");
            int count = results.getInt("Count");
            for (int j = 0; j < res.get(i).size(); j++) {
              if (res.get(i).get(j).getCrimeTime().equals(resultYear) && (
                  res.get(i).get(j).getCount() == 0)) {
                res.get(i).get(j).setCount(count);
                break;
              }
            }
          }
        } catch (SQLException throwables) {
          throwables.printStackTrace();
        } finally {
          if (connection != null) {
            connection.close();
          }
          if (selectStmt != null) {
            selectStmt.close();
          }
          if (results != null) {
            results.close();
          }
        }
      }
      return res;
    } else if (month > 0) {
      for (Object name : names) {
        ArrayList<Crime> listOfCrimes = new ArrayList<>();
        LocalDate temp = start;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        while (temp.isBefore(end) || temp.isEqual(end)) {
          Crime crime = new Crime(String.valueOf(name), formatter.format(temp), 0);
          temp = temp.plusMonths(1);
          listOfCrimes.add(crime);
        }
        res.add(listOfCrimes);
      }
      for (int i = 0; i < names.size(); i++) {
        String selectCrimes = "select *,count(*) as Count from\n"
            + "(select A.CrimeAgainstCategory as Category, Date, MCPPName from\n"
            + "(select OffenseId,CrimeAgainstCategory,DATE_FORMAT(OffenseStartDateTime, \"%Y-%m\") as Date\n"
            + "from OffenseTime inner join Offense\n"
            + "where OffenseStartDateTime > ? and OffenseStartDateTime < ? and CrimeAgainstCategory = ?) as A\n"
            + "inner join OffensePlace\n"
            + "on A.OffenseId = OffensePlace.OffenseId) as B\n"
            + "where MCPPName = ?\n"
            + "group by Date";

        Connection connection = null;
        PreparedStatement selectStmt = null;
        ResultSet results = null;
        try {
          connection = connectionManager.getConnection();
          selectStmt = connection.prepareStatement(selectCrimes);
          selectStmt.setString(1, start.toString());
          selectStmt.setString(2, end.toString());
          selectStmt.setString(3, String.valueOf(names.get(i)));
          selectStmt.setString(4, MCPP);
          results = selectStmt.executeQuery();
          while (results.next()) {
            String resultMonth = results.getString("Date");
            int count = results.getInt("Count");
            for (int j = 0; j < res.get(i).size(); j++) {
              if (res.get(i).get(j).getCrimeTime().equals(resultMonth) && (
                  res.get(i).get(j).getCount() == 0)) {
                res.get(i).get(j).setCount(count);
                break;
              }
            }
          }
        } catch (SQLException throwables) {
          throwables.printStackTrace();
        } finally {
          if (connection != null) {
            connection.close();
          }
          if (selectStmt != null) {
            selectStmt.close();
          }
          if (results != null) {
            results.close();
          }
        }
      }
      return res;
    } else {
      for (Object name : names) {
        ArrayList<Crime> listOfCrimes = new ArrayList<>();
        LocalDate temp = start;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        while (temp.isBefore(end) || temp.isEqual(end)) {
          Crime crime = new Crime(String.valueOf(name), formatter.format(temp), 0);
          temp = temp.plusDays(1);
          listOfCrimes.add(crime);
        }
        res.add(listOfCrimes);
      }
      for (int i = 0; i < names.size(); i++) {
        String selectCrimes = "select *,count(*) as Count from\n"
            + "(select A.CrimeAgainstCategory as Category, Date, MCPPName from\n"
            + "(select OffenseId,CrimeAgainstCategory,DATE_FORMAT(OffenseStartDateTime, \"%Y-%m-%d\") as Date\n"
            + "from OffenseTime inner join Offense\n"
            + "where OffenseStartDateTime > ? and OffenseStartDateTime < ? and CrimeAgainstCategory = ?) as A\n"
            + "inner join OffensePlace\n"
            + "on A.OffenseId = OffensePlace.OffenseId) as B\n"
            + "where MCPPName = ?\n"
            + "group by Date";
        Connection connection = null;
        PreparedStatement selectStmt = null;
        ResultSet results = null;
        try {
          connection = connectionManager.getConnection();
          selectStmt = connection.prepareStatement(selectCrimes);
          selectStmt.setString(1, start.toString());
          selectStmt.setString(2, end.toString());
          selectStmt.setString(3, String.valueOf(names.get(i)));
          selectStmt.setString(4, MCPP);
          results = selectStmt.executeQuery();
          while (results.next()) {
            String resultDay = results.getString("Date");
            int count = results.getInt("Count");
            for (int j = 0; j < res.get(i).size(); j++) {
              if (res.get(i).get(j).getCrimeTime().equals(resultDay) && (
                  res.get(i).get(j).getCount() == 0)) {
                res.get(i).get(j).setCount(count);
                break;
              }
            }
          }
        } catch (SQLException throwables) {
          throwables.printStackTrace();
        } finally {
          if (connection != null) {
            connection.close();
          }
          if (selectStmt != null) {
            selectStmt.close();
          }
          if (results != null) {
            results.close();
          }
        }
      }
      return res;
    }
  }
}

