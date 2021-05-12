package servlet;

import dal.CrimeDao;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Crime;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/api/searchCrimes")
public class SearchCrimes extends HttpServlet {

  protected CrimeDao crimeDao;

  @Override
  public void init() throws ServletException {
    crimeDao = CrimeDao.getInstance();
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    String requestData = req.getReader().lines().collect(Collectors.joining());
    System.out.println(requestData);
    JSONObject jsonObject = new JSONObject(requestData);
    String offenseType = jsonObject.getString("offenseType");
    List<Object> offenseNames = jsonObject.getJSONArray("offenseName").toList();
    String startDate = jsonObject.getString("startDate");
    LocalDate start = LocalDate.parse(startDate);
    String endDate = jsonObject.getString("endDate");
    LocalDate end = LocalDate.parse(endDate);
    String locationType = jsonObject.getString("locationType");
    String locationData = jsonObject.getString("locationData");

    List<ArrayList<Crime>> res;   //res need to be returned to front-end yet

    // offenseName + zipCode
    if (offenseType.contentEquals("offenseName") && locationType.contentEquals("zipcode")) {
      try {
        res = crimeDao.getCrimeByOffenseNameAndZipCode(offenseNames, start, end, locationData);
        resp.getWriter().print(new JSONArray(res));
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }

    // offenseParentName + zipCode
    if (offenseType.contentEquals("offenseParent") && locationType.contentEquals("zipcode")) {
      try {
        res = crimeDao
            .getCrimeByOffenseParentNameAndZipCode(offenseNames, start, end, locationData);
        resp.getWriter().print(new JSONArray(res));
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }

    // offenseName + MCPP
    if (offenseType.contentEquals("offenseName") && locationType.contentEquals("MCPP")) {
      try {
        res = crimeDao.getCrimeByOffenseNameAndMCPP(offenseNames, start, end, locationData);
        resp.getWriter().print(new JSONArray(res));
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }

    // offenseParentName + MCPP
    if (offenseType.contentEquals("offenseParent") && locationType.contentEquals("MCPP")) {
      try {
        res = crimeDao.getCrimeByOffenseParentNameAndMCPP(offenseNames, start, end, locationData);
        resp.getWriter().print(new JSONArray(res));
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }

    // offenseCategory + zipcode
    if (offenseType.contentEquals("offenseCategory") && locationType.contentEquals("zipcode")) {
      try {
        res = crimeDao.getCrimeByCategoryAndZipcode(offenseNames, start, end, locationData);
        resp.getWriter().print(new JSONArray(res));
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }

    // offenseCategory + MCPP
    if (offenseType.contentEquals("offenseCategory") && locationType.contentEquals("MCPP")) {
      try {
        res = crimeDao.getCrimeByCategoryAndMCPP(offenseNames, start, end, locationData);
        resp.getWriter().print(new JSONArray(res));
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }
}
