package com.news.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MapSortUtil {
	
	/**
	 * 按照value排序
	 * @param map
	 * @param newsNum
	 * @return
	 */
	public static List<Integer> mapSortByValue(Map<Integer, Integer> map, int newsNum){
		List<Integer> res = new ArrayList<Integer>();
		List<Entry<Integer, Integer>> list = new ArrayList<Entry<Integer,Integer>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<Integer, Integer>>() {
			@Override
		    public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
		        return (o2.getValue() - o1.getValue());
		    }
		});
		for(int i = 0;i < newsNum;i++) {
			if (i < list.size()) {
				res.add(list.get(i).getKey());
			}
		}
		return res;
	}
	
	public static void main(String[] args) {
		Map<Integer, Integer> map = new HashMap<Integer, Integer>();
		map.put(1, 5);
		map.put(2, 2);
		map.put(3, 9);
		map.put(4, 1);
		map.put(5, 9);
		map.put(6, 4);
		map.put(7, 6);
		map.put(8, 7);
		map.put(9, 8);
		map.put(10, 0);
		map.put(11, 3);
		List<Integer> res = mapSortByValue(map, 3);
		for (Integer integer : res) {
			System.out.println(integer);
		}
	}
	
}