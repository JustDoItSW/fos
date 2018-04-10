package com.fos.entity;


public class Infomation {

	/**
	 * 空气温度
	 */
	private String temperature;

	/**
	 * 空气湿度
	 */
	private String humidity;

	/**
	 * 光照强度
	 */
	private String lux;

	/**
	 * 土壤湿度
	 */
	private String soilHumidity;

	/**
	 * 水位高度
	 */
	private String waterHigh;

	/**
	 * 日期
	 */
	private String date;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTemperature() {
		return temperature;
	}

	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}

	public String getHumidity() {
		return humidity;
	}

	public void setHumidity(String humidity) {
		this.humidity = humidity;
	}

	public String getLux() {
		return lux;
	}

	public void setLux(String lux) {
		this.lux = lux;
	}

	public String getSoilHumidity() {
		return soilHumidity;
	}

	public void setSoilHumidity(String soilHumidity) {
		this.soilHumidity = soilHumidity;
	}

	public String getWaterHigh() {
		return waterHigh;
	}

	public void setWaterHigh(String waterHigh) {
		this.waterHigh = waterHigh;
	}
}
