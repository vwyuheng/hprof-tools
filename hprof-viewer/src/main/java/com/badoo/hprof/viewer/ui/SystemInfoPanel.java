package com.badoo.hprof.viewer.ui;

import com.badoo.hprof.viewer.android.AndroidSocket;
import com.badoo.hprof.viewer.android.Location;
import com.badoo.hprof.viewer.factory.SystemInfo;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Window containing additional system information, e.g socket connections, location, etc.
 * <p/>
 * Created by Erik Andre on 05/12/15.
 */
public class SystemInfoPanel extends JPanel implements ItemListener {

    private static final String LOCATION = "Location";
    private static final String SOCKET_CONNECTIONS = "Socket connections";
    private static final String[] SOCKET_HEADER = new String[]{"IP", "Hostname", "Port", "Connected", "Closed"};
    private static final String[] LOCATION_HEADER = new String[]{"Latitude", "Longitude", "Accuracy", "Time", "Provider"};

    private final JTable details;
    private final SystemInfo sysInfo;

    public SystemInfoPanel(SystemInfo sysInfo) {
        super(new BorderLayout());
        this.sysInfo = sysInfo;
        // Picker
        Vector<String> items = new Vector<String>();
        items.add(LOCATION);
        items.add(SOCKET_CONNECTIONS);
        JComboBox picker = new JComboBox(items);
        picker.addItemListener(this);

        // Details view
        details = new JTable();
        JScrollPane detailsContainer = new JScrollPane(details);

        add(picker, BorderLayout.NORTH);
        add(detailsContainer, BorderLayout.CENTER);
        update(LOCATION);
    }

    private void update(String selected) {
        if (LOCATION.equals(selected)) {
            showLocationInfo();
        }
        else if (SOCKET_CONNECTIONS.equals(selected)) {
            showSocketInfo();
        }
    }

    private void showSocketInfo() {
        List<AndroidSocket> sockets = sysInfo.getSockets();
        Object[][] cells = new Object[sockets.size()][];
        for (int i = 0; i < sockets.size(); i++) {
            AndroidSocket socket = sockets.get(i);
            cells[i] = new Object[] {socket.getHostIp(), socket.getHost(), socket.getPort(), socket.isConnected(), socket.isClosed()};
        }
        details.setModel(new DefaultTableModel(cells, SOCKET_HEADER));
    }

    private void showLocationInfo() {
        List<Location> locations = sysInfo.getLocations();
        Object[][] cells = new Object[locations.size()][];
        for (int i = 0; i < locations.size(); i++) {
            Location loc = locations.get(i);
            String timestamp = DateFormat.getDateTimeInstance().format(new Date(loc.getTime()));
            cells[i] = new Object[] {loc.getLatitude(), loc.getLongitude(), loc.getAccuracy(), timestamp, loc.getProvider()};
        }
        details.setModel(new DefaultTableModel(cells, LOCATION_HEADER));
    }


    @Override
    public void itemStateChanged(ItemEvent itemEvent) {
        update((String) itemEvent.getItem());
    }
}
