package ScheduleClient.client.GUIObjects;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class CoursePopup extends PopupPanel{
    private String courseName;
    private String courseDescription;
    
    public CoursePopup(String courseName, String courseDescription) {
        this.courseName = courseName;
        this.courseDescription = courseDescription;
        initializeWidget();
    }
    
    private void initializeWidget() {
        this.setStyleName("add-section-popup");
        this.setWidth("450");

        VerticalPanel panel = new VerticalPanel();
        panel.setVerticalAlignment(VerticalPanel.ALIGN_TOP);

        DecoratorPanel header = new DecoratorPanel();
        header.setStyleName("popup-header");

        Label label = new Label(courseName);
        label.setWidth("100%");
        header.setWidth("100%");

        header.add(label);
        panel.add(header);

        DecoratorPanel bodyPanel = new DecoratorPanel();

        bodyPanel.add(new HTML(courseDescription));

        panel.add(bodyPanel);

        this.add(panel);
    }
}
