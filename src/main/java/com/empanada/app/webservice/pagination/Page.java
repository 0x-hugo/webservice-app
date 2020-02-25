package com.empanada.app.webservice.pagination;

public class Page {

  private Integer number;
  private Integer size;

  public static Page build(int number, int size) {
    return new Page(number, size);
  }

  private Page(int number, int size) {
    this.number = number;
    this.size = size;
  }

  public static Page build() {
    return new Page();
  }

  private Page() {
  }

  public int getNumber() {
    return number;
  }

  public int getSize() {
    return size;
  }

}
