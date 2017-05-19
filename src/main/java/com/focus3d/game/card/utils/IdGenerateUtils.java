package com.focus3d.game.card.utils;

import java.util.Random;

public class IdGenerateUtils {
	/**
	 * *
	 * @throws Exception
	 */
	public static Long getId() {
		long min = 1;
		long max = 100000;
		return min + (((long) (new Random().nextDouble() * (max - min))));
	}
	
	public static void main(String[] args){
		System.out.println(IdGenerateUtils.getId());
	}
}
