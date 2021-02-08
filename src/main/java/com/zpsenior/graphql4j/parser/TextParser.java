package com.zpsenior.graphql4j.parser;

import java.io.Reader;

public abstract class TextParser {
	
	public class Word {
		
		private String content;
		private Position pos;

		public Word(String content, int pos, int row, int col) {
			this.content = content;
			this.pos = new Position(pos, row, col);
		}

		public String getContent() {
			return content;
		}

		public Position getPos() {
			return pos;
		}
	}
	
	enum Status{
		WORD,
		NUMBER,
		DELIM,
		QUOTE,
		COMMENT,
		PUNCTUATOR
	}
	
	private boolean eof = false;
	
	StringBuffer sb = new StringBuffer();

	private Position pos;
	
	private char save = 0XFF;
	
	protected abstract void conversion(Word word);
	
	
	public final void parser(Reader reader)throws Exception{
		eof = false;
		save = 0XFF;
		pos.reset();
		while(!eof) {
			sb.setLength(0);
			Word word = readWord(reader);
			conversion(word);
		}
	}

	private Word readWord(Reader reader)throws Exception{
		Status status = null;
		int cpos = pos.getPos();
		while(true) {
			int v = save != 0XFF ? save : reader.read();
			if(v == -1) {
				eof = true;
				break;
			}
			char c = (char)v;
			if(c == '\n') {
				pos.newLine();
				append(c);
				break;
			}
			Status s = getStatus(c);
			if(status == null) {
				append(c);
				status = s;
			}else if(status == s){
				if(s == Status.PUNCTUATOR) {
					save = c;
					break;
				}
				append(c);
				if(s == Status.QUOTE) {
					break;
				}
			}else {
				if(status == Status.WORD && s == Status.NUMBER) {
					append(c);
				}else if(status == Status.NUMBER && c == '.') {
					append(c);
				}else if(status == Status.QUOTE) {
					append(c);
				}else if(status == Status.COMMENT) {
					append(c);
				}else {
					save = c;
					break;
				}
			}
		}
		String content = sb.toString();
		return new Word(content, cpos, pos.getRow(), pos.getCol());
	}
	
	private void append(char ch) {
		sb.append(ch);
		pos.inc();
		save = 0XFF;
	}
	
	private Status getStatus(char c) {
		if(Character.isLetter(c)) {
			return Status.WORD;
		}else if(Character.isDigit(c)) {
			return Status.NUMBER;
		}else if(c == '"') {
			return Status.QUOTE;
		}else if(c == '#') {
			return Status.COMMENT;
		}else if(isSymbol(c)) {
			return Status.PUNCTUATOR;
		}
		return Status.DELIM;
	}
	
	private static boolean isSymbol(int c){
		if(c >= 32 && c <= 47){
			return true;
		}
		if(c >= 58 && c <= 64){
			return true;
		}
		if(c >= 91 && c <= 96){
			return true;
		}
		if(c >= 123 && c <= 126){
			return true;
		}
		return false;
	}
}
