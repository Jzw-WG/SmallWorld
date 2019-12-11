package app;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

public class SmallWorld{
    
    public static final int RADIUS = 300;

    public int worldSize;
    public int K;
    public double randomRate;
    public int[][] image;
    public LinkedList<Point> pointList;
    public LinkedList<Point[]> linkedList;

    public class Point{
        double X;
        double Y;
        int index;
        public LinkedList<Point> connectList;
        public LinkedList<Integer> distanceList;

        public Point() {
            this.index = 0;
            this.connectList = new LinkedList<Point>();
            this.distanceList = new LinkedList<Integer>();
            this.X = 0;
            this.Y = 0;
        }

        public Point(int index) {
            this.index = index;
            this.X = 320 + RADIUS*Math.cos(2*Math.PI/worldSize*index);
            this.Y = 320 + RADIUS*Math.sin(2*Math.PI/worldSize*index);
            this.connectList = new LinkedList<Point>();
            this.distanceList = new LinkedList<Integer>();
            for (int i = 0; i < worldSize; i++) {
                this.distanceList.add(Integer.MAX_VALUE);
            }
        }

    }

    public SmallWorld() {
        worldSize = 10;
        K = 4;
        image = new int[640][640];
        pointList = new LinkedList<Point>();
        for (int i = 0; i < worldSize; i++) {
            Point point = new Point(i);
            pointList.add(point);
        }
    }

    public SmallWorld(int worldSize, int K, double randomRate) {
        this.worldSize = worldSize;
        this.K = K;
        this.randomRate = randomRate;
        this.image = new int[640][640];
        pointList = new LinkedList<Point>();
        linkedList = new LinkedList<Point[]>();
        for (int i = 0; i < worldSize; i++) {
            Point point = new Point(i);
            pointList.add(point);
        }
        for (int i = 0; i < worldSize; i++) {
            for (int j = 0; j < K; j++) {
                int lindex = j - (K + 1)/2;
                if (lindex >= 0) {
                   lindex++;
                }
                int vindex = validIndex(i + lindex);
                connectPoint(pointList.get(i), pointList.get(vindex));
            }
            
        }
    }

    public int validIndex(int index) {
        if (index < 0) {
            index = validIndex(worldSize + index);
        }
        if (index > worldSize - 1) {
            index = validIndex(index - worldSize);
        }
        return index;
    }

    public Point[] findPair(Point point1, Point point2) {
        for(int i = 0; i < linkedList.size()/2; i++) {
            if (linkedList.get(i)[0].index == point1.index && linkedList.get(i)[1].index == point2.index) {
                return linkedList.get(i);
            }
        }
        return null;
    }

    public boolean connectPoint(Point point1, Point point2) {
        boolean result = true;
        if (point1.index == point2.index) {
            result = false;
        }
        if (!point1.connectList.contains(point2) && !point2.connectList.contains(point1)) {
            point1.connectList.add(point2);
            point2.connectList.add(point1);
            Point[] pair = new Point[2];
            pair[0] = point1;
            pair[1] = point2;
            Point[] pair2 = new Point[2];        
            pair2[0] = point2;
            pair2[1] = point1;
            if (!linkedList.contains(pair) && !linkedList.contains(pair2)) {
                linkedList.add(pair);
                linkedList.add(pair2);
            }  
        } else {
            result = false;
        }
        return result;
    }

    public boolean disconnectPoint(Point point1, Point point2) {
        boolean result = true;
        if (point1.connectList.contains(point2) && point2.connectList.contains(point1)) {
            point1.connectList.remove(point2);
            point2.connectList.remove(point1);
            Point[] pair = findPair(point1, point2);
            Point[] pair2 = findPair(point2, point1);

            if (linkedList.contains(pair) && linkedList.contains(pair2)) {
                linkedList.remove(pair);
                linkedList.remove(pair2);
            }
        } else {
            result = false;
        }
        return result;
    }

    public void randomReconnect() {
        int rdIndex = new Random().nextInt(worldSize);
        int rdReconnectIndex = new Random().nextInt(worldSize);

        while (pointList.get(rdIndex).connectList.size() == 0) {
            randomReconnect();
            return;
        }

        int rdConnectedIndex = pointList.get(rdIndex).connectList.get(new Random().nextInt(pointList.get(rdIndex).connectList.size())).index;

        while(rdIndex == rdReconnectIndex || pointList.get(rdIndex).connectList.contains(pointList.get(rdReconnectIndex))) {
            rdReconnectIndex = new Random().nextInt(worldSize);
        }

        if (disconnectPoint(pointList.get(rdIndex), pointList.get(rdConnectedIndex)) && connectPoint(pointList.get(rdIndex), pointList.get(rdReconnectIndex))) {
            return;
        } else {
            randomReconnect();
            return;
        }
    }

    public void reconnect() {
        
        List<Integer> hasReconnect = new ArrayList<>();
        for(int i = 0; i < linkedList.size()/2; i++) {
            int rdReconnectIndex = new Random().nextInt(worldSize);
            int rdDisconnectIndex = new Random().nextInt(2);
            if (linkedList.get(0)[0].index < linkedList.get(0)[1].index) {
                while(linkedList.get(0)[rdDisconnectIndex].index == rdReconnectIndex || pointList.get(linkedList.get(0)[rdDisconnectIndex].index).connectList.contains(pointList.get(rdReconnectIndex))) {
                    rdReconnectIndex = new Random().nextInt(worldSize);
                }

                if (Math.random() < randomRate) {
                    Point chosenPoint = linkedList.get(0)[rdDisconnectIndex];
                    Point chosenPoint2 = linkedList.get(0)[rdDisconnectIndex == 0?1:0];
                    disconnectPoint(chosenPoint, chosenPoint2);
                    connectPoint(chosenPoint, pointList.get(rdReconnectIndex));
                } else {
                    Point chosenPoint = linkedList.get(0)[rdDisconnectIndex];
                    Point chosenPoint2 = linkedList.get(0)[rdDisconnectIndex == 0?1:0];
                    disconnectPoint(chosenPoint, chosenPoint2);
                    connectPoint(chosenPoint, chosenPoint2);
                }
                
            }
        }
    }

    public static void getMinLength(Point point1) {
        Point tempStart = point1;
        Queue<Point> queue = new LinkedList<>();
        List<Integer> hasSearchList = new ArrayList<Integer>();
        queue.offer(point1);

        if (point1.connectList.size() == 0) {
            return;
        }

        while(!queue.isEmpty()){
            Point start = queue.poll();
            if (hasSearchList.contains(start.index)) {
                continue;
            }
            hasSearchList.add(start.index);
            start.distanceList.set(start.index, 0);

            for (Point childPoint : start.connectList) {//扫描v的邻接边(点)
                if (!hasSearchList.contains(childPoint.index)) {//如果这个顶点未被访问(每个顶点只会入队列一次)
                    // hasSearchList.add(childPoint.index);                   
                    if (point1.distanceList.get(start.index) + 1 < point1.distanceList.get(childPoint.index)) {
                        point1.distanceList.set(childPoint.index, 0);
                        point1.distanceList.set(childPoint.index, point1.distanceList.get(start.index) + 1);//更新该顶点到源点的距离
                    }    
                    queue.offer(childPoint);
                }//end if
            }//end for
        }//end while

        return;
    }

    public double getCustering(Point point) {
        int custering = 0;
        int k = point.connectList.size();
        int maxCustering = k*(k-1)/2;
        for (int i = 0; i < linkedList.size(); i++) {
            if (point.connectList.contains(linkedList.get(i)[0]) && point.connectList.contains(linkedList.get(i)[1])) {
                custering++;
            }
        }
        return ((double)custering)/2/maxCustering;
    }

    public void clearDistance() {
        for (int i = 0; i < pointList.size(); i++) {
            for (int j = 0; j < worldSize; j++) {
                pointList.get(i).distanceList.set(j, Integer.MAX_VALUE);
            }
        }
    }

    public List<Integer> alonePoint() {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < pointList.size(); i++) {
            if (pointList.get(i).connectList.size() == 0) {
                result.add(pointList.get(i).index);
            }
        }
        return result;
    }
}