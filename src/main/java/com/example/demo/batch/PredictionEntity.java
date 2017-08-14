package com.example.demo.batch;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "prediction")
public class PredictionEntity {

  private int id;
  private Date validFromDate;
  private Date validToDate;
  private String nan;
  private int quantity;


  //Note: Identity + MySQL Dialect == AUTOINCREMENT column
  @Id //ID
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  //valid from
  @Column(name="valid_from")
  @Temporal(TemporalType.DATE)
  public Date getValidFromAsDate() {
    return validFromDate;
  }

  public void setValidFromAsDate(Date validFrom) {
    this.validFromDate = validFrom;
  }

  //valid to
  @Column(name="valid_to")
  @Temporal(TemporalType.DATE)
  public Date getValidToAsDate() {
    return validToDate;
  }

  public void setValidToAsDate(Date validTo) {
    this.validToDate = validTo;
  }

  public String getNan() {
    return nan;
  }

  public void setNan(String nan) {
    this.nan = nan;
  }

  public int getQuantity() {
    return quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

}
