package action;

import com.google.common.collect.Lists;
import domain.Obstacle;
import domain.Point;
import enums.AxisEnum;
import enums.MathEnum;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Action {

    Obstacle obstacle;

    private Point maxPoint;

    private Point endPoint;

    private static final Point startPoint = new Point(0, 0);

    /**
     * 最大执行次数 = 最大横坐标 * 最大纵坐标 * MAX_EXECUTE_SIZE
     */
    private static final int MAX_EXECUTE_SIZE = 5;


    public Action(Point maxPonit, Point endPoint, int obstacleCount) {

        this.maxPoint = maxPonit;
        this.endPoint = endPoint;
        this.obstacle = new Obstacle(maxPonit, obstacleCount);
    }


    /**
     * 开始操作
     *
     * @param args
     */
    public static void main(String[] args) {
        //设置最大位置
        Point maxPoint = new Point(100, 100);
        //设置终点
        Point endPoint = new Point(10, 18);

        Action action = new Action(maxPoint, endPoint, 70);
        action.run();
    }

    /**
     * 开始执行
     */
    private void run() {
        List<Point> line = Lists.newLinkedList();
        line.add(startPoint);
        Point currentPoint = startPoint;
        for (int i = 0; i < (maxPoint.getX() * maxPoint.getY() * MAX_EXECUTE_SIZE); i++) {
            currentPoint = running(currentPoint, line);
            if (Objects.isNull(currentPoint)) {
                System.out.println("四周都是障碍物，结束！");
                break;
            }
            if (nearEnd(currentPoint)) {
                line.add(endPoint);
                break;
            }
        }
        System.out.println(line.toString());
    }

    /**
     * 用当前点，获取到下一步的点。
     * 前进一步
     *
     * @param currentPoint
     * @param line
     * @return
     */
    private Point running(Point currentPoint, List<Point> line) {
        Point next = choseNextStep(currentPoint, line);
        if (next == null) {
            return null;
        }
        line.add(next);
        return next;

    }

    /**
     * 选择下一步
     * 1.获取所有能去的位置，预计目标位
     * 2.预计目标位中去掉障碍点
     * 3.对预计目标位进行筛选，选出最优路径
     *
     * @param currentPoint 当前所在的点
     * @param line         走过的线
     */
    private Point choseNextStep(Point currentPoint, List<Point> line) {

        List<Point> purposeList = createPurposePoints(currentPoint);
        List<Point> allowPurposeList = allowPoint(purposeList);

        if (allowPurposeList.isEmpty()) {
            return null;
        }
        Point bestPoint = choseBestStep(allowPurposeList, line);

        return bestPoint;
    }

    /**
     * 生成 当前点所有能去的位置（去掉边界点）
     *
     * @param currentPoint
     * @return
     */
    private List<Point> createPurposePoints(Point currentPoint) {

        List<Point> purpoesedPoints = Lists.newArrayList();

        purpoesedPoints.add(getNearPoint(currentPoint, AxisEnum.AXIS_X, MathEnum.ADD));
        purpoesedPoints.add(getNearPoint(currentPoint, AxisEnum.AXIS_X, MathEnum.REDUCE));
        purpoesedPoints.add(getNearPoint(currentPoint, AxisEnum.AXIS_Y, MathEnum.ADD));
        purpoesedPoints.add(getNearPoint(currentPoint, AxisEnum.AXIS_Y, MathEnum.REDUCE));

        return purpoesedPoints.stream().filter(o -> o != null).collect(Collectors.toList());
    }

    /**
     * 根据X Y轴、加 减 情况，生成新的坐标点
     * 最后做了范围校验
     *
     * @param currentPoint 当前点
     * @param axisEnum     坐标轴
     * @param mathEnum     加减操作
     * @return
     */
    private Point getNearPoint(Point currentPoint, AxisEnum axisEnum, MathEnum mathEnum) {
        Point point;
        if (AxisEnum.AXIS_X == axisEnum) {
            point = new Point(currentPoint.getX() + mathEnum.getCode(), currentPoint.getY());
        } else if (AxisEnum.AXIS_Y == axisEnum) {
            point = new Point(currentPoint.getX(), currentPoint.getY() + mathEnum.getCode());
        } else {
            System.out.println("匹配类型错误");
            return null;
        }
        return checkBoundary(point);
    }

    /**
     * 校验边界值
     *
     * @param currentPoint 当前点
     * @return
     */
    private Point checkBoundary(Point currentPoint) {

        if (0 <= currentPoint.getX() && currentPoint.getX() <= maxPoint.getX()
                && 0 <= currentPoint.getY() && currentPoint.getY() <= maxPoint.getY()) {
            return currentPoint;
        }
        return null;
    }

    /**
     * 过滤掉障碍物点
     *
     * @param purpoestPoints
     * @return
     */
    private List<Point> allowPoint(List<Point> purpoestPoints) {
        return purpoestPoints.stream().filter(
                o -> obstacle.isAllow(o)).collect(Collectors.toList());
    }


    /**
     * 最优路线选择
     * <p>
     * 1。如果目标位只有一个，说明前面都堵了。
     * 2。去掉来时的路，有其他选择不会走来时的路，否则导致循环
     * 3。根据在历史路径中出现次数排序。
     * 4。选取第一第二进行比较。
     *
     * @param purposeList 预计目标位
     * @param line        走过的历史路径
     * @return
     */
    private Point choseBestStep(List<Point> purposeList, List<Point> line) {
        Point bestPoint;
        if (purposeList.size() == 1) {
            bestPoint = purposeList.get(0);
            return bestPoint;
        }
        List<Point> notHistorys = excludeHistory(purposeList, line);
        List<Point> orderList = notHistorys.stream().sorted(
                (o1, o2) -> appearCount(o1, line) - appearCount(o2, line)).collect(Collectors.toList());

        return orderList.size() == 1 ? orderList.get(0) : comparePoint(orderList.get(0), orderList.get(1), line);
    }

    /**
     * 过滤掉来时的路
     *
     * @param purposeList 预计目标位
     * @param line        历史路线
     * @return
     */
    private List<Point> excludeHistory(List<Point> purposeList, List<Point> line) {
        Point historyPoint = getHistoryStep(line);
        if (historyPoint != null) {
            List<Point> notHistoryPonintPurposeList = purposeList.stream().filter(o ->
                    o.getX() != historyPoint.getX() || o.getY() != historyPoint.getY()).collect(Collectors.toList());
            return notHistoryPonintPurposeList;
        }
        return purposeList;
    }

    /**
     * 得到来时的点
     *
     * @param line
     * @return
     */
    private Point getHistoryStep(List<Point> line) {

        int size = line.size();
        if (size >= 2) {
            return line.get(size - 2);
        }
        return null;
    }

    /**
     * 比较到终点距离,距离远的优先级高。
     *
     * @param first
     * @param second
     * @return 返回距离最远的
     */
    private Point compareDistance(Point first, Point second) {

        int firstDistance = Math.abs(first.getX() - endPoint.getX()) + Math.abs(first.getY() - endPoint.getY());
        int secondDistance = Math.abs(second.getX() - endPoint.getX()) + Math.abs(second.getY() - endPoint.getY());

        return firstDistance >= secondDistance ? second : first;

    }

    /**
     * 比较两个点，出现次数一致，返回距离远的
     * 出现次数不一致，返回出现最少的。
     *
     * @param first  比较点1
     * @param second 比较点2
     * @param lines  历史路线
     * @return
     */
    private Point comparePoint(Point first, Point second, List<Point> lines) {
        int firstCount = appearCount(first, lines);
        int secondCount = appearCount(second, lines);

        if (firstCount == firstCount) {
            return compareDistance(first, second);
        }
        return firstCount > secondCount ? second : first;

    }

    /**
     * 出现次数
     *
     * @param currentPoint 当前点
     * @param line         历史路线
     * @return
     */
    private static int appearCount(Point currentPoint, List<Point> line) {
        return (int) line.stream().filter(o -> o.getX() == currentPoint.getX() && o.getY() == currentPoint.getY()).count();
    }

    /**
     * 判断是否抵达终点
     *
     * @param point 当前点
     * @return
     */
    private boolean nearEnd(Point point) {

        if (Math.abs(endPoint.getX() - point.getX()) == 1 && point.getY() == endPoint.getY()) {
            return true;
        }
        if (Math.abs(endPoint.getY() - point.getY()) == 1 && point.getX() == endPoint.getX()) {
            return true;
        }

        return false;
    }

    //如果2个点，说明 要么2边是边界，要么1个边界，1个障碍物， 要么 2处是障碍物
    //2个都是障碍物，一条路肯定是来的路，来的路优先级低 < 全新的路。不需要考虑与终点的位置，
    // 因为来的路要么距离终点远，要么来的路进入死胡同。
    //2个是边界，说明是 最大边界点，或者0点，，取不到最新的，随机一个
    //1个是边界，1个是障碍物，看2条路 与终点的距离，如果在终点+2范围的时候，优先+。
    //一条是来的路，取 lines中最后一个 。优先考虑新的。什么情况特殊。

    //如果3个点，其中有一个是来时的路，
    // 说明，要么遇到1个障碍物 要么遇到1条边界。
    //首先把来时的路设置优先级最低，剩余2个进行比较，
    //如果一个是边界，那优先向来的路继续走，走一步不越过终点的情况。考虑超过的情况，走一步也是-。
    //如果一个是障碍物，
    // 看能不能翻过障碍物，并且跳过后的点与终点的距离/ 跳障碍物要考虑
    //跳先看目标点能到吗？ 在看障碍物四周，能得到几个可行动点，有3个可行的点才可以
    //若有一个可行点，在看该可行点与障碍物相交叉的两个点可以不可以
    //都可以就 直接 输入5个点到历史中，返回目标点

    //2个路，看在line中情况，一个出现一个没出现，优先没出现的。或少的。
    //出现次数一致，优先向终点靠拢。

    //如果4个点，说明在中间，那从lines中取上个路径，沿着直线走
    //都没有走过。那直线走
    //1个走过，2个没走过，那靠拢终点。
    //2个都走过，去新的。
    //都走过，选择靠拢终点
    //计算距离，
    //什么时候拐弯呢？沿着直线走也要拐弯。对于4个点来说，判断与终点方向进行拐弯
    //

}
