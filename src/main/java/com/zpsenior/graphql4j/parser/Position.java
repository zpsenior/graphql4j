package com.zpsenior.graphql4j.parser;

public class Position {
	
	private int pos = 0;
	private int col = 0;
	private int row = 0;
	
	public Position(){}
	
	public Position(int pos, int row, int col){
		this.pos = pos;
		this.row = row;
		this.col = col;
	}
	
	public int getPos() {
		return pos;
	}
	public int getCol() {
		return col;
	}
	public int getRow() {
		return row;
	}
	
	public void reset() {
		col = 0;
		pos = 0;
		row = 0;
	}
	
	public void inc() {
		col++;
		pos++;
	}
	
	public void newLine() {
		pos++;
		col = 0;
		row++;
	}

}
