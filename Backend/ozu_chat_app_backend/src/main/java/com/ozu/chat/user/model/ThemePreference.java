package com.ozu.chat.user.model;

public class ThemePreference {

	private ThemeMode mode = ThemeMode.SYSTEM;
	private String primaryColor = "#5b7cfa";
	private String secondaryColor = "#00b8a9";
	private String accentColor = "#ffb703";
	private int fontSize = 15;
	private int borderRadius = 16;

	public ThemeMode getMode() {
		return mode;
	}

	public void setMode(ThemeMode mode) {
		this.mode = mode;
	}

	public String getPrimaryColor() {
		return primaryColor;
	}

	public void setPrimaryColor(String primaryColor) {
		this.primaryColor = primaryColor;
	}

	public String getSecondaryColor() {
		return secondaryColor;
	}

	public void setSecondaryColor(String secondaryColor) {
		this.secondaryColor = secondaryColor;
	}

	public String getAccentColor() {
		return accentColor;
	}

	public void setAccentColor(String accentColor) {
		this.accentColor = accentColor;
	}

	public int getFontSize() {
		return fontSize;
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	public int getBorderRadius() {
		return borderRadius;
	}

	public void setBorderRadius(int borderRadius) {
		this.borderRadius = borderRadius;
	}
}
