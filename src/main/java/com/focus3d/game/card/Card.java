package com.focus3d.game.card;
/**
 * 牌
 * *
 * @author lihaijun
 *
 */
public class Card {

	private String data;
	
	public Card(String data){
		this.data = data;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return getData();
	}
	
	
}
