package com.focus3d.game.card;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
/**
 * 扑克牌
 * *
 * @author lihaijun
 *
 */
public class CardManager {
	//定义HashMap变量用于存储每张排的编号以及牌型  
	private  HashMap<Integer,String> cardMap = new HashMap<Integer,String>();   
	//定义ArrayList变量存储牌的编号  
	private  ArrayList<Integer> cardIndexList = new ArrayList<Integer>();  
	//定义数组存储牌的花色  
	private static String[] colors = {"1","2","3","4"};//1-红桃,2-黑桃,3-方块,4-棉花
	//定义数组存储牌的号
	private static String[] numbers = {"3","4","5","6","7","8","9","10","11","12","13","14","15"};  
	public static String PLAYER_KEY_PREFIX = "ply_";
	public static String PLAYER_KEY_BOTTOM_CARD = "dp";
	public static void main(String[] args) {  
		CardManager poker = new CardManager();
		Map<String, Card> shuffleCards = poker.ShuffleCards();
		for(Map.Entry<String, Card> c : shuffleCards.entrySet()){
			System.out.println(c.getValue());
		}
	}

	/**
	 * 洗牌
	 * *
	 * @return
	 */
	public Map<String, Card>  ShuffleCards() {  
        //定义数组存储牌值  
        int index = 0;    
        //定义编号  
        for(String number : numbers){    
            //遍历排值数组  
            for(String color : colors){   
                //遍历花色  
            	//将花色与牌值拼接，并将编号与拼接后的结果存储到cardMap中  
                cardMap.put(index, color.concat("_" + number));  
                //将编号存储到cardIndexList中  
                cardIndexList.add(index);   
                index++;
            }  
        }
        //将小王和大王存储到hm中
        cardMap.put(index, "0_16");//小王
        cardIndexList.add(index);  
        index++;  
        cardMap.put(index, "0_17");//大王  
        cardIndexList.add(index);  
        //打乱顺序  
        Collections.shuffle(cardIndexList);    
        //定义四个TreeSet集合的变量用于存储底牌编号以及三个玩家的牌的编号 ,采用TreeSet集合是因为TreeSet集合可以实现自然排序
        TreeSet<Integer> ply_1 = new TreeSet<Integer>();  
        TreeSet<Integer> ply_2 = new TreeSet<Integer>();  
        TreeSet<Integer> ply_3 = new TreeSet<Integer>();   
        TreeSet<Integer> dp = new TreeSet<Integer>();  
        //遍历编号的集合，实现发牌  
        for(int x = 0; x < cardIndexList.size(); x++){  
            if(x >= cardIndexList.size() - 3){  
                dp.add(cardIndexList.get(x));  
            } else if( x % 3 == 0){ 
            	ply_1.add(cardIndexList.get(x));  
            } else if(x % 3 == 1){  
            	ply_2.add(cardIndexList.get(x));  
            } else if(x % 3 == 2){  
            	ply_3.add(cardIndexList.get(x));
            }  
        }  
        Map<String, Card> result = new HashMap<String, Card>();
        result.put(PLAYER_KEY_BOTTOM_CARD, getCardGroup(dp,cardMap));
        result.put(PLAYER_KEY_PREFIX + "1", getCardGroup(ply_1, cardMap));
        result.put(PLAYER_KEY_PREFIX + "2", getCardGroup(ply_2, cardMap));
        result.put(PLAYER_KEY_PREFIX + "3", getCardGroup(ply_3, cardMap));
        return result;
	}  
	/**
	 * 获取一组牌
	 * *
	 * @param cardIndex
	 * @param card
	 * @return
	 */
    private Card getCardGroup(TreeSet<Integer> cardIndex, HashMap<Integer,String> card){  
    	List<String> orderDesc = new ArrayList<String>();
		StringBuffer cardSb = new StringBuffer();
		for (Integer key : cardIndex) {
			// 遍历玩家TreeSet集合，获得玩家的牌的编号
			String value = card.get(key);
			orderDesc.add(value);
		}
		for(int i = orderDesc.size() - 1; i >= 0; i --){
			// 根据玩家牌编号获取具体的牌值
			cardSb.append(orderDesc.get(i));
			if(i > 0){
				cardSb.append(",");
			}
		}
		return new Card(cardSb.toString());
	}
}
