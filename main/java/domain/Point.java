package domain;

import lombok.Data;

/**
 * 坐标点
 */
@Data
public class Point {

    private int x;
    private int y;

    public Point(int x,int y){
        this.x = x;
        this.y = y;
    }

    public String toString(){
        return "( "+x + "," + y + ")" + "-->";
    }

}
