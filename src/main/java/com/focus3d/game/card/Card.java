package com.focus3d.game.card;
/**
 * 牌
 * *
 * @author lihaijun
 *
 */
public class Card {

	private String data;
	private boolean isReady = false;//是否准备开始
	private boolean isCaller = false;//是否分配叫地主的玩家
	//private boolean isSecondClick = false;//如果是叫地主玩家，是否第二次点击
	private boolean isRobHost = false;//是否抢得了地主
	private Integer robHostClick;//点击是否抢地主，0-不抢 1-抢

	private Card bootomCard;//底牌
	private Integer remainCard;//还剩几张牌
	private boolean isSend = true;//是否出牌
	
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

	public Integer getRobHostClick() {
		return robHostClick;
	}

	public void setRobHostClick(Integer robHostClick) {
		this.robHostClick = robHostClick;
	}

	public boolean isRobHost() {
		return isRobHost;
	}

	public void setRobHost(boolean isRobHost) {
		this.isRobHost = isRobHost;
	}

	public Card getBootomCard() {
		return bootomCard;
	}

	public void setBootomCard(Card bootomCard) {
		this.bootomCard = bootomCard;
	}

	public Integer getRemainCard() {
		return remainCard;
	}

	public void setRemainCard(Integer remainCard) {
		this.remainCard = remainCard;
	}

	public boolean isSend() {
		return isSend;
	}

	public void setSend(boolean isSend) {
		this.isSend = isSend;
	}

	public boolean isReady() {
		return isReady;
	}

	public void setReady(boolean isReady) {
		this.isReady = isReady;
	}
}
