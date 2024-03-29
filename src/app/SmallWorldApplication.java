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
    public static SmallWorld smallWorld;
    public static final int worldSize = 1000;
    public static final int K = 10;
    public static final double rate = 0.001;

    JFrame jFrame;
    JButton jButton;
    JButton jButton2;
    JButton jButton3;
    JPanel jPanel;

    

    public static void main(String[] args) throws Exception {
        SmallWorldApplication app = new SmallWorldApplication();
        smallWorld = new SmallWorld(SmallWorldApplication.worldSize, SmallWorldApplication.K, SmallWorldApplication.rate);
        app.go();
    }
    
    public void go() {
        jFrame = new JFrame();
        jButton = new JButton("ReWire");
        jButton2 = new JButton("Cal");
        jButton3 = new JButton("Clear");
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
        jButton.addActionListener(new reWire());
        jButton2.addActionListener(new cal());
        jButton3.addActionListener(new clear());
        jFrame.setSize(640, 720); 
        jFrame.setVisible(true);
    }

    public class MyDrawPanel extends JPanel{
        public void paintComponent(Graphics g) {
            
            for (int i = 0; i < smallWorld.worldSize; i++) {
                g.setColor(Color.red);
                g.fillOval((int) smallWorld.pointList.get(i).X - pointSize/2, (int) smallWorld.pointList.get(i).Y - pointSize/2, pointSize, pointSize);
                for (int j = 0; j < smallWorld.pointList.get(i).connectList.size(); j++) {
                    if (smallWorld.pointList.get(i).index < smallWorld.pointList.get(i).connectList.get(j).index)
                    g.drawLine((int) smallWorld.pointList.get(i).X, (int) smallWorld.pointList.get(i).Y, (int) smallWorld.pointList.get(i).connectList.get(j).X, (int) smallWorld.pointList.get(i).connectList.get(j).Y);
                }
            }
            
        } 
    }

    class reWire implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            // for (int i = 0; i < SmallWorld.worldSize * SmallWorld.K/2; i++) {
            //     if (Math.random() < SmallWorld.randomRate) {
            //         SmallWorld.randomReconnect();
            //     }
            // }
            smallWorld.reconnect();
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
            smallWorld = new SmallWorld(SmallWorldApplication.worldSize, SmallWorldApplication.K, SmallWorldApplication.rate);
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
            smallWorld.clearDistance();
            double LSum = 0;
            double psum = 0;
            for (int i = 0; i < smallWorld.worldSize; i++) {
                SmallWorld.getMinLength(smallWorld.pointList.get(i));
                for (int j = 0; j < smallWorld.pointList.get(i).distanceList.size(); j++) {
                    psum = psum + smallWorld.pointList.get(i).distanceList.get(j);
                }
                psum = psum/ smallWorld.pointList.get(i).distanceList.size();
                LSum = LSum + psum;
            }
            LSum = LSum/smallWorld.worldSize;
            System.out.println("L: " + LSum);

            double CSum = 0;
            int NaNCount = 0;
            for (int i = 0; i < smallWorld.worldSize; i++) {
                if (smallWorld.getCustering(smallWorld.pointList.get(i)) <= 1) {
                    CSum = CSum + smallWorld.getCustering(smallWorld.pointList.get(i));
                } else {
                    NaNCount++;
                }
                 
            }
            CSum = CSum/(smallWorld.worldSize - NaNCount);
            System.out.println("C: " + CSum);
        }
    }
}

//test data
//1000, 10, 0      L: 50.450399949499634 C: 0.6666666666666636
//1000, 10, 0.0001 L: 39.964044610164734 C: 0.6664232323232293
//1000, 10, 0.0004 L: 34.76905654498596  C: 0.6658131313131287
//1000, 10, 0.0009 L: 27.382330460855265 C: 0.6639686868686836
//1000, 10, 0.002  L: 18.90538525039252  C: 0.6625444444444414
//1000, 10, 0.004  L: 15.77070597524555  C: 0.6608353535353503
//1000, 10, 0.008  L: 10.144693811292774 C: 0.6512979797979761
//1000, 10, 0.02   L: 6.630581227962533  C: 0.6233782828282806
//1000, 10, 0.04   L: 5.602303145018002  C: 0.5950287212787196
//1000, 10, 0.08   L: 4.639818979188489  C: 0.519838677988678 
//1000, 10, 0.1    L: 4.391595275063611  C: 0.4844389887889894
//1000, 10, 0.2    L: 3.8496901293443613 C: 0.3454002886002886
//1000, 10, 0.5    L: 3.37170012670518   C: 0.08588990600902394
//1000, 10, 1      L: 3.2644426285352526 C: 0.009350326118669776