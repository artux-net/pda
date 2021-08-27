package net.artux.pdalib;

public class QueryPage {
  private int number = 1;

  private int size = 15;

  private String sortDirection = "ASC";

  private String sortBy = "xp";

  public QueryPage(int number, int size, String sortDirection, String sortBy) {
    this.number = number;
    this.size = size;
    this.sortDirection = sortDirection;
    this.sortBy = sortBy;
  }

  public QueryPage(int number) {
    this.number = number;
  }

  public int getNumber() {
    return number;
  }

  public void setNumber(int number) {
    this.number = number;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public String getSortDirection() {
    return sortDirection;
  }

  public void setSortDirection(String sortDirection) {
    this.sortDirection = sortDirection;
  }

  public String getSortBy() {
    return sortBy;
  }

  public void setSortBy(String sortBy) {
    this.sortBy = sortBy;
  }
}