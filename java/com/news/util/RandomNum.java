package com.news.util;

public class RandomNum {
	public static int[] generateRandomNumber(int num) {
		int[] all = new int[num];
		int i, q;
		for (i = 0; i < num; i++) {
			double m = Math.random() * num;
			int random = (int) m;
			all[i] = random;
			// 从第二个数开始判断
			if (i >= 1) {
				// 和之前的比较，重复则当前值重新random赋值
				for (q = 0; q < i; q++) {
					if (all[i] == all[q]) {
						i = i - 1;
					}
				}
			}
		}
		return all;
	}
	
	public static void main(String[] args) {
		int num = 10;
		int[] all = generateRandomNumber(num);
		for(int j = 0;j < num;j++) {
			System.out.print(all[j] + "\t");
		}
	}
	
}