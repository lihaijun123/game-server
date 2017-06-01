package com.focus3d.game.card;
/**
 * 牌
 * *
 * @author lihaijun
 *
 */
public class Card {

	private String data;
	private boolean isCaller = false;//是否分配叫地主的玩家
	private boolean isSecondClick = false;//如果是叫地主玩家，是否第二次点击
	private boolean isRobHost = false;//是否抢得了地主
	private Integer robHostClickCount;//点击是否抢地主，数值在上家基础上+1
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

	public boolean isCaller() {
		return isCaller;
	}

	public void setCaller(boolean isCaller) {
		this.isCaller = isCaller;
	}
	public boolean isSecondClick() {
		return isSecondClick;
	}

	public void setSecondClick(boolean isSecondClick) {
		this.isSecondClick = isSecondClick;
	}

	public Integer getRobHostClickCount() {
		return robHostClickCount;
	}

	public void setRobHostClickCount(Integer robHostClickCount) {
		this.robHostClickCount = robHostClickCount;
	}

	public boolean isRobHost() {
		return isRobHost;
	}

	public void setRobHost(boolean isRobHost) {
		this.isRobHost = isRobHost;
	}

	
}
