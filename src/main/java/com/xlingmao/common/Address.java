package com.xlingmao.common;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Address {
	
	private  StringProperty number;
	private  StringProperty phone;
	private  StringProperty name;
	
	public Address(){}
	
	public Address(String number,String phone,String name){
		this.number = new SimpleStringProperty(number);
		this.phone = new SimpleStringProperty(phone);
		this.name = new SimpleStringProperty(name);
	}

	public StringProperty numberProperty(){
		return this.number;
	}
	
	public String getNumber() {
		return number.get();
	}

	public void setNumber(String number) {
		this.number.set(number);
	}

	public StringProperty phoneProperty(){
		return this.phone;
	}
	
	public String getPhone() {
		return phone.get();
	}

	public void setPhone(String phone) {
		this.phone.set(phone);
	}

	public StringProperty nameProperty(){
		return this.name;
	}
	
	public String getName() {
		return name.get();
	}

	public void setName(String name) {
		this.name.set(name);
	}
	
}
