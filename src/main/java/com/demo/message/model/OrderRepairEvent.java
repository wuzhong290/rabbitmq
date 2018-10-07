package com.demo.message.model;

/**
 * 订单修复事件处理
 */
public class OrderRepairEvent {

  private int uid;

  private String udid;

  private long orderCode;

  private boolean repairAddress;

  private String addressId;

  public int getUid() {
    return uid;
  }

  public void setUid( int uid ) {
    this.uid = uid;
  }

  public String getUdid() {
    return udid;
  }

  public void setUdid( String udid ) {
    this.udid = udid;
  }

  public long getOrderCode() {
    return orderCode;
  }

  public void setOrderCode( long orderCode ) {
    this.orderCode = orderCode;
  }

  public boolean isRepairAddress() {
    return repairAddress;
  }

  public void setRepairAddress( boolean repairAddress ) {
    this.repairAddress = repairAddress;
  }

  public String getAddressId() {
    return addressId;
  }

  public void setAddressId( String addressId ) {
    this.addressId = addressId;
  }
}
