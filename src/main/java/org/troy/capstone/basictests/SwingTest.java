/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package org.troy.capstone.basictests;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.troy.capstone.utils.TableUtils;

import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
/**
 *
 * @author thkle
 */
public class SwingTest {

    static {
        // Suppress SLF4J warnings
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "off");
    }

    public static void main(String[] args) {
        String[] animals = {"bear", "cat", "giraffe"};
        double[] cuteness = {90.1, 84.3, 99.7};

        Table cuteAnimals =
            Table.create("Cute Animals")
                .addColumns(
                    StringColumn.create("Animal types", animals),
                    DoubleColumn.create("rating", cuteness));

        JFrame frame = new JFrame("Cute Animals Table");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        
        // Create and add the main table
        JTable jTable = TableUtils.toJTable(cuteAnimals);
        JScrollPane mainScrollPane = new JScrollPane(jTable);
        mainScrollPane.setPreferredSize(new Dimension(600, 200));
        
        // Create button panel
        JPanel buttonPanel = new JPanel();
        JButton button = new JButton("Show NFL Teams Data");
        buttonPanel.add(button);
        
        // Add components to frame with proper positioning
        frame.add(mainScrollPane, BorderLayout.NORTH);
        frame.add(buttonPanel, BorderLayout.SOUTH);
        frame.add(new JLabel(new ImageIcon("data/apple.jpg")), BorderLayout.EAST);
        frame.pack();
        frame.setVisible(true);
        
        button.addActionListener(e -> {
            // Create a new window for NFL data instead of adding to existing frame
            Table nflData = Table.read().csv("data/teams.csv");
            JTable nflTable = TableUtils.toJTable(nflData);
            frame.add(new JScrollPane(nflTable), BorderLayout.CENTER);
            frame.pack();
            frame.revalidate();
        });

        
    }
}