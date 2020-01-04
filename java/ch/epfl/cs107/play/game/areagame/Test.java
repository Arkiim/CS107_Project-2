package ch.epfl.cs107.play.game.areagame;

import java.util.ArrayList;
import java.util.List;

public class Test {

    List<String> test;
    List<String> test2;

    public void add(String t){
        test.add(t);
    }

    public Test(){
        test = new ArrayList<String>();
        test2 = new ArrayList<String>();
        add("1");
        add("2");
        System.out.println(test);

      /*  System.out.println(test2.get(0));
        test2.remove(1);
        System.out.println(test);*/
        for(String i : test){
           // System.out.println(test.get(1));
            test2.add(i);
        }

       System.out.println(test2);
        test2.remove(1);
        System.out.println(test2 +"," + test);
    }

    @Override
    public String toString(){
        return test.get(0) +"," + test.get(1);
    }

    public static void main(String[] args) {
         Test a =  new Test();
         //System.out.println(test1);
    }
}
