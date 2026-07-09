package com.ayush;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AlarmClock extends JFrame {

    private JLabel timeLabel;
    private JSpinner hourSpinner, minuteSpinner, secondSpinner;
    private JComboBox<String> amPmBox;
    private JButton setAlarmButton, cancelAlarmButton;
    private JLabel statusLabel;

    private Timer clockTimer;
    private Timer alarmCheckTimer;

    private int alarmHour = -1;
    private int alarmMinute = -1;
    private int alarmSecond = -1;
    private boolean alarmSet = false;

    public AlarmClock() {
        setTitle("Alarm Clock");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 300);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        timeLabel = new JLabel("00:00:00", SwingConstants.CENTER);
        timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        mainPanel.add(timeLabel, BorderLayout.NORTH);

        JPanel setPanel = new JPanel();
        setPanel.setLayout(new FlowLayout());

        hourSpinner = new JSpinner(new SpinnerNumberModel(12, 1, 12, 1));
        minuteSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
        secondSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
        amPmBox = new JComboBox<>(new String[]{"AM", "PM"});

        hourSpinner.setEditor(new JSpinner.NumberEditor(hourSpinner, "00"));
        minuteSpinner.setEditor(new JSpinner.NumberEditor(minuteSpinner, "00"));
        secondSpinner.setEditor(new JSpinner.NumberEditor(secondSpinner, "00"));

        setPanel.add(new JLabel("Set Alarm:"));
        setPanel.add(hourSpinner);
        setPanel.add(new JLabel(":"));
        setPanel.add(minuteSpinner);
        setPanel.add(new JLabel(":"));
        setPanel.add(secondSpinner);
        setPanel.add(amPmBox);

        mainPanel.add(setPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));

        JPanel buttonPanel = new JPanel();
        setAlarmButton = new JButton("Set Alarm");
        cancelAlarmButton = new JButton("Cancel Alarm");
        cancelAlarmButton.setEnabled(false);
        buttonPanel.add(setAlarmButton);
        buttonPanel.add(cancelAlarmButton);

        statusLabel = new JLabel("No alarm set", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        bottomPanel.add(buttonPanel);
        bottomPanel.add(statusLabel);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        setAlarmButton.addActionListener(e -> setAlarm());
        cancelAlarmButton.addActionListener(e -> cancelAlarm());

        startClock();
        startAlarmChecker();
    }

    private void startClock() {
        clockTimer = new Timer(1000, e -> updateClock());
        clockTimer.start();
        updateClock();
    }

    private void updateClock() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss a");
        timeLabel.setText(sdf.format(new Date()));
    }

    private void startAlarmChecker() {
        alarmCheckTimer = new Timer(1000, e -> checkAlarm());
        alarmCheckTimer.start();
    }

    private void checkAlarm() {
        if (!alarmSet) return;

        java.util.Calendar cal = java.util.Calendar.getInstance();
        int currentHour = cal.get(java.util.Calendar.HOUR);
        currentHour = currentHour == 0 ? 12 : currentHour;
        int currentMinute = cal.get(java.util.Calendar.MINUTE);
        int currentSecond = cal.get(java.util.Calendar.SECOND);
        String currentAmPm = cal.get(java.util.Calendar.AM_PM) == java.util.Calendar.AM ? "AM" : "PM";

        if (currentHour == alarmHour && currentMinute == alarmMinute
                && currentSecond == alarmSecond
                && currentAmPm.equals(amPmBox.getSelectedItem())) {
            triggerAlarm();
        }
    }

    private void setAlarm() {
        alarmHour = (Integer) hourSpinner.getValue();
        alarmMinute = (Integer) minuteSpinner.getValue();
        alarmSecond = (Integer) secondSpinner.getValue();
        alarmSet = true;

        setAlarmButton.setEnabled(false);
        cancelAlarmButton.setEnabled(true);

        String ampm = (String) amPmBox.getSelectedItem();
        statusLabel.setText(String.format("Alarm set for %02d:%02d:%02d %s",
                alarmHour, alarmMinute, alarmSecond, ampm));
    }

    private void cancelAlarm() {
        alarmSet = false;
        setAlarmButton.setEnabled(true);
        cancelAlarmButton.setEnabled(false);
        statusLabel.setText("No alarm set");
    }

    private void triggerAlarm() {
        alarmSet = false;
        setAlarmButton.setEnabled(true);
        cancelAlarmButton.setEnabled(false);
        statusLabel.setText("Alarm ringing!");

        Toolkit.getDefaultToolkit().beep();

        Timer beepTimer = new Timer(500, null);
        int[] count = {0};
        beepTimer.addActionListener(e -> {
            Toolkit.getDefaultToolkit().beep();
            count[0]++;
            if (count[0] >= 5) {
                ((Timer) e.getSource()).stop();
            }
        });
        beepTimer.start();

        JOptionPane.showMessageDialog(this, "Time's up! Alarm ringing.", "Alarm",
                JOptionPane.INFORMATION_MESSAGE);

        statusLabel.setText("No alarm set");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AlarmClock clock = new AlarmClock();
            clock.setVisible(true);
        });
    }
}
