package app;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.*;
import java.nio.Buffer;
import java.util.Random;
import java.awt.Graphics;
import java.awt.color.*;

public class SmallWorldApplication{
    int pointSize = 8;
    public static final int worldSize = 30;
    public static final int K = 4;
    public static final double rate = 0.4;

    JFrame jFrame;
    JButton jButton;
    JButton jButton2;
    JButton jButton3;
    JPanel jPanel;

    public class MyDrawPanel extends JPanel{
        public void paintComponent(Graphics g) {
            for (int i = 0; i < SmallWorld.worldSize; i++) {
                g.setColor(Color.red);
                g.fillOval((int) SmallWorld.pointList.get(i).X - pointSize/2, (int) SmallWorld.pointList.get(i).Y - pointSize/2, pointSize, pointSize);
                for (int j = 0; j < SmallWorld.pointList.get(i).connectList.size(); j++) {
                    if (SmallWorld.pointList.get(i).index < SmallWorld.pointList.get(i).connectList.get(j).index)
                    g.drawLine((int) SmallWorld.pointList.get(i).X, (int) SmallWorld.pointList.get(i).Y, (int) SmallWorld.pointList.get(i).connectList.get(j).X, (int) SmallWorld.pointList.get(i).connectList.get(j).Y);
                }
            }
            
        } 
    }

    public static void main(String[] args) throws Exception {
        SmallWorldApplication app = new SmallWorldApplication();
        SmallWorld smallWorld = new SmallWorld(SmallWorldApplication.worldSize, SmallWorldApplication.K, SmallWorldApplication.rate);
        app.go();
    }
    
    public void go() {
        jFrame = new JFrame();
        jButton = new JButton("dian");
        jButton2 = new JButton("cal");
        jButton3 = new JButton("clear");
        jPanel = new JPanel();       
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.getContentPane().add(BorderLayout.SOUTH, jPanel);
        jPanel.add(jButton);
        jPanel.add(jButton2);
        jPanel.add(jButton3);
        // jFrame.getContentPane().add(BorderLayout.SOUTH, jButton);
        // jFrame.getContentPane().add(BorderLayout.SOUTH, jButton2);
        MyDrawPanel drawPanel = new MyDrawPanel();
        jFrame.getContentPane().add(BorderLayout.CENTER, drawPanel);
        jButton.addActionListener(new dian());
        jButton2.addActionListener(new cal());
        jButton3.addActionListener(new clear());
        jFrame.setSize(640, 720); 
        jFrame.setVisible(true);
    }

    class dian implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            // for (int i = 0; i < SmallWorld.worldSize * SmallWorld.K/2; i++) {
            //     if (Math.random() < SmallWorld.randomRate) {
            //         SmallWorld.randomReconnect();
            //     }
            // }
            SmallWorld.reconnect();
            jFrame.repaint();
        }
    }

    class clear implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            // for (int i = 0; i < SmallWorld.worldSize * SmallWorld.K/2; i++) {
            //     if (Math.random() < SmallWorld.randomRate) {
            //         SmallWorld.randomReconnect();
            //     }
            // }
            SmallWorld smallWorld = new SmallWorld(SmallWorldApplication.worldSize, SmallWorldApplication.K, SmallWorldApplication.rate);
            jFrame.repaint();
        }
    }

    class cal implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            // for (int i = 0; i < SmallWorld.worldSize * SmallWorld.K/2; i++) {
            //     if (Math.random() < SmallWorld.randomRate) {
            //         SmallWorld.randomReconnect();
            //     }
            // }
            SmallWorld.clearDistance();
            double sum = 0;
            double psum = 0;
            for (int i = 0; i < SmallWorld.worldSize; i++) {
                SmallWorld.getMinLength(SmallWorld.pointList.get(i));
                for (int j = 0; j < SmallWorld.pointList.get(i).distanceList.size(); j++) {
                    psum = psum + SmallWorld.pointList.get(i).distanceList.get(j);
                }
                psum = psum/ SmallWorld.pointList.get(i).distanceList.size();
                sum = sum + psum;
            }
            sum = sum/SmallWorld.worldSize;

            System.out.println(sum);
        }
    }
}