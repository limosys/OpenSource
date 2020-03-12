package com.borland.dx.dataset;

import java.awt.Color;
import java.awt.Font;

public class CustomPaintSiteLabel {
	
	private static Font dfltLblFont = new Font("Tahoma", Font.BOLD, 9);
	
	private String text;
	private Font font;
	private Color background;
	private Color foreground;
	private Color borderColor;
	private int width = 0;
	private int marginLeft = 2;
	private int marginRight = 2;
	private int marginTop = 1;
	private int marginBottom = 1;
	private int paddingLeft = 3;
	private int paddingRight = 3;

	public CustomPaintSiteLabel(String text, Color background, Color foreground) {
		this.text = text;
		this.background = background;
		this.foreground = foreground;
		this.font = dfltLblFont;
	}

	public CustomPaintSiteLabel(String text, Color background, Color foreground, Color borderColor) {
		this.text = text;
		this.background = background;
		this.foreground = foreground;
		this.borderColor = borderColor;
		this.font = dfltLblFont;
	}
	
	public CustomPaintSiteLabel(String text, Color background, Color foreground, Color borderColor, Font font) {
		this.text = text;
		this.background = background;
		this.foreground = foreground;
		this.borderColor = borderColor;
		this.font = (font==null ? dfltLblFont : font);
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public Color getBackground() {
		return background;
	}

	public void setBackground(Color background) {
		this.background = background;
	}

	public Color getForeground() {
		return foreground;
	}

	public void setForeground(Color foreground) {
		this.foreground = foreground;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getMarginLeft() {
		return marginLeft;
	}

	public void setMarginLeft(int marginLeft) {
		this.marginLeft = marginLeft;
	}

	public int getMarginRight() {
		return marginRight;
	}

	public void setMarginRight(int marginRight) {
		this.marginRight = marginRight;
	}

	public int getMarginTop() {
		return marginTop;
	}

	public void setMarginTop(int marginTop) {
		this.marginTop = marginTop;
	}

	public int getMarginBottom() {
		return marginBottom;
	}

	public void setMarginBottom(int marginBottom) {
		this.marginBottom = marginBottom;
	}

	public int getPaddingLeft() {
		return paddingLeft;
	}

	public void setPaddingLeft(int paddingLeft) {
		this.paddingLeft = paddingLeft;
	}

	public int getPaddingRight() {
		return paddingRight;
	}

	public void setPaddingRight(int paddingRight) {
		this.paddingRight = paddingRight;
	}

	public boolean isBlankText() {
		return text == null || text.trim().isEmpty();
	}

	public void setMargins(int marginLeft, int marginTop, int marginRight, int marginBottom) {
		this.marginLeft = marginLeft;
		this.marginTop = marginTop;
		this.marginRight = marginRight;
		this.marginBottom = marginBottom;
	}

	public Color getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(Color borderColor) {
		this.borderColor = borderColor;
	}
}
