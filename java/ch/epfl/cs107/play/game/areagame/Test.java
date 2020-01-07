package ch.epfl.cs107.play.game.areagame;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Test {
    Scanner scanner = new Scanner(System.in);
    private double j;
    private int n;
    private double k;

    public Test() {
        System.out.println("Enter : init value of J, incrementation value K and Stop condition N (int)");
        this.j = scanner.nextDouble();
        this.k = scanner.nextDouble();
        this.n = scanner.nextInt();

        System.out.println(j + ", " + k + ", " + n);
        f();
    }

    private void f(){
        double i = 0;
        double x = 0;
        while(i < n){
            x += k;
            i+= x;
            j +=1;
            System.out.println( x  + " <x, j> " + j + " i = " + i +" < " + n + "= n");
        }
        System.out.println("x = " + x + " Sqrt[n] = " + Math.sqrt(n) );

        for(int w = 0; w < Math.sqrt(4*n); w++){
            System.out.println(w + " <K, J> " + j + " TRUE K = " + Math.sqrt(4*n - j));
        }
    }


    public static void main(String[] args) {
        Test chibre = new Test();
    }
}
