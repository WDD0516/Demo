package org.example;

public class Main {

    public static int findRotateSteps(String ring, String key) {
        char[] chars = ring.toCharArray();
        int res = 0;
        int cur_index = 0;
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            while(chars[cur_index]!=c){
                cur_index++;
                res++;
            }
            res+=1; //按下
//            System.out.println(res);
        }
        return res;
    }
    public static void main(String[] args) {
        System.out.println(findRotateSteps("godding","godding"));
    }
}