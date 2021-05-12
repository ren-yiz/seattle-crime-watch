package model;

public class Crime {
  // crimeName can be anything in offense category, group or offenseName
  protected String crimeName;
  // crimeTime will be a String represent day, month or year used to aggregate the crime
  protected String crimeTime;
  protected int count;

  public Crime(String crimeName, String crimeTime, int count) {
    this.crimeName = crimeName;
    this.crimeTime = crimeTime;
    this.count = count;
  }

  public String getCrimeName() {
    return crimeName;
  }

  public void setCrimeName(String crimeName) {
    this.crimeName = crimeName;
  }

  public String getCrimeTime() {
    return crimeTime;
  }

  public void setCrimeTime(String crimeTime) {
    this.crimeTime = crimeTime;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  @Override
  public String toString() {
    return "Crime{" +
        "crimeName='" + crimeName + '\'' +
        ", crimeTime='" + crimeTime + '\'' +
        ", count=" + count +
        '}';
  }
}
