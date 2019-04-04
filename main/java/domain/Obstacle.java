package domain;

import util.RandomUtil;
import com.google.common.collect.Lists;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 应该单例
 */
@Data
public class Obstacle {


    private List<Point> obstacleList = new ArrayList();


    public Obstacle(Point maxPoint, int obstacleNum) {

        List<Point> obstacleList = Lists.newArrayList();

        for (int i = 0; i < obstacleNum; i++) {
            obstacleList.add(RandomUtil.initEndPoint(maxPoint.getX(), maxPoint.getY()));
        }
        obstacleList.add(new Point(10,0));


        obstacleList.add(new Point(9,7));

        obstacleList.add(new Point(8,6));

        obstacleList.add(new Point(10,6));

        obstacleList.add(new Point(0,18));

        obstacleList.add(new Point(2,17));

        obstacleList.add(new Point(1,18));

        obstacleList.add(new Point(0,1));

        obstacleList.add(new Point(1,0));

        this.obstacleList = obstacleList;
    }


    public boolean isAllow(Point checkPonit){

        List<Point> result =  this.obstacleList.stream().filter(
                o -> checkPonit.getX() == o.getX()).filter(
                        o -> o.getY() == checkPonit.getY()).collect(Collectors.toList());
        return result.isEmpty()? true :false;

    }


}
