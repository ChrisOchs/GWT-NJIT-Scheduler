package ScheduleClient.client.GUIObjects;

import ScheduleClient.client.DataAdapters.ClassTimeAdapter;
import ScheduleClient.client.DataAdapters.SectionAdapter;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import java.util.ArrayList;

public class GridPopout {
    public GridPopout(ArrayList<SectionAdapter> sections) {
        DialogBox popupBox = createDialogBox(sections);

        popupBox.setAnimationEnabled(true);
        popupBox.center();
        popupBox.setWidth("1000");
        popupBox.setHeight("256");
        popupBox.setStyleName("nav-panel");
        popupBox.show();
    }

    private DialogBox createDialogBox(ArrayList<SectionAdapter> sections) {
        final DialogBox popupBox = new DialogBox();

        VerticalPanel panel = new VerticalPanel();
        panel.setVerticalAlignment(VerticalPanel.ALIGN_TOP);
        panel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);

        ScrollPanel scroller = new ScrollPanel();
        scroller.setWidth("100%");
        FlexTable layout = new FlexTable();
        scroller.add(layout);

        layout.setCellSpacing(6);
        layout.setCellPadding(4);

        String [] colTitles = {"Section", "Title", "Call Number", "Professor",
        "Day", "Time", "Room", "Credits"};

        for(int c = 0; c < colTitles.length; c++) {
            layout.setWidget(0, c, new Label(colTitles[c]));
        }

        for(int c = 0; c < sections.size(); c++) {
            SectionAdapter section = sections.get(c);

            layout.setWidget(c+1, 0, new Label(section.getCourse()));
            layout.setWidget(c+1, 1, new Label(section.getCourseName()));
            layout.setWidget(c+1, 2, new Label(section.getCallNum()));
            layout.setWidget(c+1, 3, new Label(section.getProfessor()));

            VerticalPanel dayPanel = new VerticalPanel();
            VerticalPanel timesPanel = new VerticalPanel();
            VerticalPanel roomsPanel = new VerticalPanel();

            if (section.getOnline()) {
                timesPanel.add(new Label("Online Section"));
            } else {
                for (ClassTimeAdapter cta : section.getClassTimes()) {
                    dayPanel.add(new Label(cta.getDayString()));
                    timesPanel.add(new Label(cta.toString()));
                    roomsPanel.add(new Label(cta.getRoom()));
                }
            }

            layout.setWidget(c+1, 4, dayPanel);
            layout.setWidget(c+1, 5, timesPanel);
            layout.setWidget(c+1, 6, roomsPanel);
            layout.setWidget(c+1, 7, new Label(Integer.toString(section.getCredits())));
        }

        panel.add(layout);

        Button closeButton = new Button("Close");
        closeButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent e) {
                popupBox.hide();
            }
        });

        panel.add(closeButton);

        popupBox.add(panel);

        return popupBox;
    }
}
