package com.scortelemed.entidades;

public class Cobertura {
	
	private String name;
	private String code;
	private String result;
	private String prima;
	private String capital;
	private String exclusiones;
	
	public Cobertura(String code, String name) {
		this.name = name;
		this.code = code.replace(".","").toUpperCase();
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getPrima() {
		return prima;
	}
	public void setPrima(String prima) {
		this.prima = prima;
	}
	public String getCapital() {
		return capital;
	}
	public void setCapital(String capital) {
		this.capital = capital;
	}
	public String getExclusiones() {
		return exclusiones;
	}
	public void setExclusiones(String exclusiones) {
		this.exclusiones = exclusiones;
	}
	
	

}
