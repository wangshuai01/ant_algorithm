package util;

import domain.Point;

import java.util.Random;

public class RandomUtil {


    /**
     *  随机返回一个终点
     * @param maxX  最大区域
     * @param maxY  最大区域
     * @return
     */
    public static Point initEndPoint(int maxX, int maxY){

        Random random = new Random();

        return new Point(random.nextInt(maxX),random.nextInt(maxY));

    }
}
