package ScheduleClient.client.GUIObjects;

import ScheduleClient.client.DataAdapters.ClassTimeAdapter;
import ScheduleClient.client.DataAdapters.SectionAdapter;
import ScheduleClient.client.ScheduleClientServiceAsync;
import com.google.gwt.dev.jjs.ast.JLabel;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class ScheduleGrid extends VerticalPanel {

    public static enum GridType {
        WeekdaysOnly, AllDays, WeekdaysAndSat, WeekdaysAndSun
    };

    // Base class for all panels on the grid
    private abstract class SectionPanel extends VerticalPanel
        implements MouseOverHandler, MouseOutHandler{

        protected SectionAdapter sa;
        protected Button actionButton;
        protected SectionPopup popup;

        public SectionPanel(SectionAdapter sa, ClassTimeAdapter cta) {
            this.sa = sa;
            this.popup = null;

            this.setVerticalAlignment(VerticalPanel.ALIGN_TOP);
            this.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
            this.setWidth("128");

            this.sa = sa;
        }

        public abstract void onMouseOver(MouseOverEvent moe);

        public abstract void onMouseOut(MouseOutEvent moe);

        public void hidePopup() {
            if(popup != null) {
                popup.hide();
            }
        }

        public SectionAdapter getSectionAdapter() {
            return sa;
        }
    }

    // Panel for classes that have been added to the grid
    private class AddedClassPanel extends SectionPanel {
        public AddedClassPanel(final SectionAdapter sa, ClassTimeAdapter cta) {
            super(sa, cta);
            this.setStyleName("schedule-class added-class");

            this.actionButton = new Button(sa.getSectionNum() + ((cta == null) ? "": ("<br>"
                    + cta.toString() + "<br>"
                    + cta.getRoom())) + "<br>(Click to Remove)");
            this.actionButton.setSize("100%", "100%");

            this.actionButton.setStyleName("section-select-button added-class");

            this.actionButton.addMouseOverHandler(this);
            this.actionButton.addMouseOutHandler(this);

            this.add(actionButton);

            this.actionButton.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent ce) {
                    popup.hide();
                    final AsyncCallback<Boolean> removeSectionCallback = new AsyncCallback<Boolean>() {

                        // On Success remove the section
                        public void onSuccess(Boolean canRemove) {
                            if(canRemove) {
                                removeSection(sa);
                            }
                        }

                        // Display a warning on failure
                        public void onFailure(Throwable caught) {
                            Window.alert("Warning: Error removing section...");
                        }
                    };

                    service.removeSection(sessionKey, sa, removeSectionCallback);
                }
            });
        }

        public void onMouseOver(MouseOverEvent moe) {
            if(popup == null){
                popup = new SectionPopup(sa);
            }

            popup.setPopupPosition(this.getAbsoluteLeft() + 64, this.getAbsoluteTop() + 64);
            popup.show();
        }

        public void onMouseOut(MouseOutEvent moe) {
            popup.hide();
        }
    }

    // Panel for a class that can be added to a panel
    private class SelectedClassPanel extends SectionPanel {
        public SelectedClassPanel(final SectionAdapter sa, ClassTimeAdapter cta) {
            super(sa, cta);
            this.setStyleName("schedule-class not-added-class");

            this.actionButton = new Button(sa.getSectionNum() + ((cta == null) ? "": ("<br>"
                    + cta.toString() + "<br>"
                    + cta.getRoom())) + "<br>(Click to Add)");
            this.actionButton.setSize("100%", "100%");
            
            this.actionButton.setStyleName("section-select-button not-added-class");

            this.actionButton.addMouseOverHandler(this);
            this.actionButton.addMouseOutHandler(this);

            this.add(actionButton);

            this.actionButton.addClickHandler(new ClickHandler() {

                public void onClick(ClickEvent ce) {

                    popup.hide();
                    final AsyncCallback<Boolean> addSectionCallback = new AsyncCallback<Boolean>() {

                        // On Success remove the section
                        public void onSuccess(Boolean canAdd) {
                            if (canAdd) {
                                addSection(sa);
                            }
                        }

                        // Display a warning on failure
                        public void onFailure(Throwable caught) {
                            Window.alert("Warning: Error adding section...");
                        }
                    };

                    service.addSection(sessionKey, sa, addSectionCallback);
                }
            });
        }

        public void onMouseOver(MouseOverEvent moe) {
            if(popup == null){
                popup = new SectionPopup(sa);
            }

            ArrayList<SectionPanel> sectionPanels = getSectionPanels(sa);

            for(SectionPanel panel : sectionPanels) {
                if(panel instanceof SelectedClassPanel) {
                    ((SelectedClassPanel)panel).doRolloverStyle(true);
                }
            }

            popup.setPopupPosition(this.getAbsoluteLeft() + 64, this.getAbsoluteTop() + 64);
            popup.show();
        }

        public void onMouseOut(MouseOutEvent moe) {
            ArrayList<SectionPanel> sectionPanels = getSectionPanels(sa);

            for(SectionPanel panel : sectionPanels) {
                if(panel instanceof SelectedClassPanel) {
                    ((SelectedClassPanel)panel).doRolloverStyle(false);
                }
            }
            popup.hide();
        }

        private void doRolloverStyle(boolean value) {
            if(value) {
                this.setStyleName("schedule-class hovered-class");
                this.actionButton.setStyleName("section-select-button hovered-class");
            } else {
                this.setStyleName("schedule-class not-added-class");
                this.actionButton.setStyleName("section-select-button not-added-class");
            }
        }
    }

    // Panel for when two classes overlap
    private class ConflictPanel extends SectionPanel {
        private SectionAdapter conflictSection;
        public ConflictPanel(final SectionAdapter sa, final SectionAdapter conflictSection, ClassTimeAdapter cta) {
            super(sa, cta);
            this.conflictSection = conflictSection;
            this.setStyleName("schedule-class conflict-class");

            this.actionButton = new Button(sa.getSectionNum() + ((cta == null) ? "": ("<br>"
                    + cta.toString() + "<br>"
                    + cta.getRoom())) + "<br>(Click to Replace)");
            this.actionButton.setSize("100%", "100%");

            this.actionButton.setStyleName("section-select-button conflict-class");

            this.actionButton.addMouseOverHandler(this);
            this.actionButton.addMouseOutHandler(this);

            this.add(actionButton);
            
            this.actionButton.addClickHandler(new ClickHandler() {

                public void onClick(ClickEvent ce) {
                    popup.hide();
                    final AsyncCallback<Boolean> removeSectionCallback = new AsyncCallback<Boolean>() {
                        // On Success remove the section

                        public void onSuccess(Boolean canAdd) {

                            removeSection(conflictSection);
                            
                            final AsyncCallback<Boolean> addSectionCallback = new AsyncCallback<Boolean>() {
                                public void onSuccess(Boolean canAdd) {
                                    addSection(sa);
                                }

                                // Display a warning on failure
                                public void onFailure(Throwable caught) {
                                    Window.alert("Warning: Error replacing section...");
                                }
                            };

                            service.addSection(sessionKey, sa, addSectionCallback);
                        }

                        // Display a warning on failure
                        public void onFailure(Throwable caught) {
                            Window.alert("Warning: Error replacing section...");
                        }
                    };

                    service.removeSection(sessionKey, conflictSection, removeSectionCallback);
                }
            });
        }

        public void onMouseOver(MouseOverEvent moe) {
            if (popup == null) {
                popup = new SectionPopup(sa);
            }

            popup.setPopupPosition(this.getAbsoluteLeft() + 64, this.getAbsoluteTop() + 64);
            popup.show();
        }

        public void onMouseOut(MouseOutEvent moe) {
            popup.hide();
        }
    }

    private GridType type;
    private int startHour;
    private int endHour;
    private VerticalPanel [] schedulePanels;

    private DisclosurePanel onlineClassPanel;
    private HorizontalPanel onlineSectionsPanel;

    private ListBox historyBox;
    private ArrayList<SectionAdapter> history;

    private ArrayList<SectionAdapter> confirmedSections;

    private ArrayList<SectionAdapter> timedSections;
    private ArrayList<SectionAdapter> onlineSections;

    private HashMap<ClassTimeAdapter, SectionAdapter> timeSectionMap;

    private ScheduleClientServiceAsync service;
    private String sessionKey;
    private Label creditLabel = new Label("0");
   // JLabel title = new JLabel("Want a Raise?", JLabel.CENTER);
 
    // Key was null, needed to alter after constructor//
    public void setKey( String s ) {
        sessionKey = s;
    }
    
    private void updateCredits()
    {
      /*
         * if(totalCredits > 19) label.setStyleName("name you give style") else { }
         */

       int currentCredits = 0;
       for(SectionAdapter sa : confirmedSections)
       {
       currentCredits += sa.getCredits();
       }
      
  if (currentCredits > 19) 
      creditLabel.setStyleName("over19");
  else
      creditLabel.setStyleName("under19");
      creditLabel.setText("" + currentCredits);
  
    }


        
    public ScheduleGrid(ScheduleClientServiceAsync service, String sessionKey, GridType type,
            int startHour, int endHour, ListBox historyBox) {

        this.service = service;
        this.sessionKey = sessionKey;
        
        this.type = type;
        this.startHour = startHour;
        this.endHour = endHour;

        this.historyBox = historyBox;
        this.history = new ArrayList<SectionAdapter>();

        this.timeSectionMap = new HashMap<ClassTimeAdapter, SectionAdapter>();
        this.confirmedSections = new ArrayList<SectionAdapter>();

        this.timedSections = new ArrayList<SectionAdapter>();
        this.onlineSections = new ArrayList<SectionAdapter>();

        this.setVerticalAlignment(VerticalPanel.ALIGN_TOP);

        generateOptionsPanel();
        generateOnlinePanel();
        generateGrid(type, startHour, endHour);

        //testGrid();
    }

    private void generateOptionsPanel() {
        DockPanel panel = new DockPanel();
        Button gridButton = new Button("Simple Schedule View");
        gridButton.setWidth("200");
        gridButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent e) {
                new GridPopout(confirmedSections);
            }
        });

        panel.setWidth("100%");
        panel.add(gridButton, DockPanel.WEST);

        HorizontalPanel creditPanel = new HorizontalPanel();
        creditPanel.add(new Label("Credits Scheduled:"));
        creditPanel.add(new Label("     "));
        creditPanel.add(creditLabel);

        panel.add(creditPanel, DockPanel.EAST);

        this.add(panel);
    }

    private void generateOnlinePanel() {
        onlineClassPanel = new DisclosurePanel("Online & Other Courses (0)");
        onlineSectionsPanel = new HorizontalPanel();
        onlineSectionsPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);
        onlineSectionsPanel.setHeight("60");
        //onlineSectionsPanel
        onlineClassPanel.setAnimationEnabled(true);
        onlineClassPanel.setContent(onlineSectionsPanel);
        onlineClassPanel.setStyleName("online-panel");
        onlineClassPanel.setWidth("850");
        this.add(onlineClassPanel);
    }

    // Creates the grid that can have classes added to it
    private void generateGrid(GridType type, int startHour, int endHour) {
        String[] days = getDays(type);

        Grid grid = new Grid(2, days.length + 1);
        
        for(int c = 0; c < days.length; c++){
            DecoratorPanel dayPanel = new DecoratorPanel();
            dayPanel.setWidth("128");
            dayPanel.add(new HTML(days[c]));
            grid.setWidget(0, c+1, dayPanel);
        }
        
        VerticalPanel timePanel = new VerticalPanel();
        timePanel.setWidth("64");
        
        for(int c = 0; c < (endHour - startHour) + 1; c++){
            VerticalPanel hourPanel = new VerticalPanel();
            hourPanel.setVerticalAlignment(VerticalPanel.ALIGN_TOP);
            hourPanel.setHeight("60");

            Label time;

            if(startHour + c > 11) {
                if(startHour + c == 12) {
                    time = new Label("12:00PM");
                } else {
                    time = new Label((startHour + c) % 12 + ":00PM");
                }
            }
            else {
                time = new Label((startHour + c) + ":00AM");
            }

            hourPanel.add(time);
            hourPanel.setStyleName("schedule-timebox");
            timePanel.add(hourPanel);
        }

        grid.getCellFormatter().setVerticalAlignment(1, 0, HasVerticalAlignment.ALIGN_TOP);
        grid.setWidget(1, 0, timePanel);

        schedulePanels = new VerticalPanel[days.length];

        for(int c = 0; c < schedulePanels.length; c++){
            schedulePanels[c] = new VerticalPanel();
            //grid.getCellFormatter().setStyleName(1, c+1, "schedule-column");
            grid.getCellFormatter().setVerticalAlignment(1, c+1, HasVerticalAlignment.ALIGN_TOP);
            grid.setWidget(1, c+1, schedulePanels[c]);
        }
        
        this.add(grid);
    }

    // Clears the classes on the grid, closes any open popups
    private void clearGrid(){
        for(VerticalPanel panel : schedulePanels){

            for(int c = 0; c < panel.getWidgetCount(); c++) {
                Widget widget = panel.getWidget(c);

                if(widget instanceof SectionPanel) {
                    ((SectionPanel)widget).hidePopup();
                }
            }

            panel.clear();
        }
    }

    public void addSections(ArrayList<SectionAdapter> sections) {
        for (SectionAdapter section : sections) {
            if (!confirmedSections.contains(section)) {

                // Remove all unadded sections...
                for (int c = timedSections.size() - 1; c >= 0; c--) {
                    if (!confirmedSections.contains(timedSections.get(c))) {

                        for (ClassTimeAdapter cta : timedSections.get(c).getClassTimes()) {
                            timeSectionMap.remove(cta);
                        }

                        timedSections.remove(c);
                    }
                }

                // Remove any online sections of this class
                for (int c = onlineSections.size() - 1; c >= 0; c--) {
                    if (!confirmedSections.contains(onlineSections.get(c))) {
                        onlineSections.remove(c);
                    }
                }

                SectionAdapter existingSection = null;

                // If the course already exists on the schedule, remove it
                for (SectionAdapter currentSection : confirmedSections) {
                    if (section.getCourse().equals(currentSection.getCourse())) {
                        existingSection = currentSection;
                        break;
                    }
                }

                // Remove any sections of this course that were already added to the grid
                if (existingSection != null) {
                    confirmedSections.remove(existingSection);
                    onlineSections.remove(existingSection);
                    timedSections.remove(existingSection);

                    for (ClassTimeAdapter cta : existingSection.getClassTimes()) {
                        timeSectionMap.remove(cta);
                    }
                }

                // Add the new sections
                confirmedSections.add(section);

                // Online class or non-class time class
                if (section.getClassTimes().size() == 0) {
                    if (!onlineClassPanel.isOpen()) {
                        onlineClassPanel.setOpen(true);
                    }

                    onlineSections.add(section);
                } else {
                    for (ClassTimeAdapter cta : section.getClassTimes()) {
                        timeSectionMap.put(cta, section);
                    }
                    timedSections.add(section);
                }

                // Add it to history if it hasn't been added
                if (!history.contains(section)) {
                    history.add(section);
                    historyBox.addItem(section.getSectionNum(), section.getSectionNum());
                }
            }
        }

        updateCredits();
        clearGrid();
        redrawGrid();
        clearOnlineGrid();
        redrawOnlineGrid();
    }

    // Adds a section to the schedule grid
    public void addSection(SectionAdapter section) {
        ArrayList<SectionAdapter> sections = new ArrayList<SectionAdapter>();
        sections.add(section);
        addSections(sections);
    }

    private void clearOnlineGrid() {
        onlineSectionsPanel.clear();
    }

    private void redrawOnlineGrid() {

        int addedSections = 0;

        for(SectionAdapter sa : onlineSections) {
            SectionPanel sectionPanel;

            if(confirmedSections.contains(sa)) {
                sectionPanel = new AddedClassPanel(sa, null);
                addedSections++;
            } else {
                sectionPanel = new SelectedClassPanel(sa, null);
            }

            sectionPanel.setHeight("60");

            onlineSectionsPanel.add(sectionPanel);
        }

        onlineClassPanel.getHeaderTextAccessor().setText(
                "Online & Other Classes (" + addedSections + ")");
    }

    // Used to navigate from a section in the history of
    // visited sections
    public void navigateHistory(String sectionNum) {
        for(SectionAdapter sa : history) {
            if( sa.getSectionNum().equals(sectionNum) ) {
                displaySection(sa);
                return;
            }
        }
    }

    // Removes a section from the grid
    public void removeSection(SectionAdapter sa) {
        if (confirmedSections.contains(sa)) {
         
            confirmedSections.remove(sa);
            if (onlineSections.contains(sa)) {
                onlineSections.remove(sa);
                updateCredits();
                clearOnlineGrid();
                redrawOnlineGrid();
            } else {
                for (ClassTimeAdapter cta : sa.getClassTimes()) {
                    timeSectionMap.remove(cta);
                }
                updateCredits();
                timedSections.remove(sa);
                clearGrid();
                redrawGrid();
                
            }
        }
    }

    // Clears everything out of the schedule grid, resets it to default
    public void clearSchedule() {
        timeSectionMap.clear();
        history.clear();
        confirmedSections.clear();
        timedSections.clear();
        onlineSections.clear();

        clearGrid();
        redrawGrid();

        clearOnlineGrid();
        redrawOnlineGrid();
    }

    // Redraws the grid based on the currently added sections and the
    // selected section
    private void redrawGrid() {
        ArrayList<ClassTimeAdapter> classTimes = getAllClassTimes();

        ClassTimeAdapter lastAdapter = null;
        SectionPanel lastPanel = null;

        // Keeps track of times that have overlapped
        ArrayList<ClassTimeAdapter> overlappedTimes = new ArrayList<ClassTimeAdapter>();

        for(ClassTimeAdapter classTime : classTimes) {
            if( !validDay(classTime.getDay()) ) {
                continue;
            }
            
            int panelIndex = mapClassDaytoGrid(classTime.getDay());

            // This classtime's panel
            int sectionSize = classTime.getEnd() - classTime.getStart();
            SectionPanel sectionPanel;

            // Colliding class time
            if (lastAdapter != null && classTime.equals(lastAdapter)) {

                // If this is the first conflict, then the last time isn't
                // in the list
                if(!overlappedTimes.contains(lastAdapter)) {
                    overlappedTimes.add(lastAdapter);
                    schedulePanels[mapClassDaytoGrid(lastAdapter.getDay())].remove(lastPanel);
                }

                overlappedTimes.add(classTime);
                lastAdapter = classTime;

                continue;
            }

            if(!overlappedTimes.isEmpty()) {

                ClassTimeAdapter time = overlappedTimes.get(0);
                ClassTimeAdapter conflictingTime = null;

                for(ClassTimeAdapter cta : overlappedTimes) {
                    if(confirmedSections.contains(timeSectionMap.get(cta))) {
                        conflictingTime = cta;
                        break;
                    }
                }

                String panelSize = Integer.toString(
                        (time.getEnd() - time.getStart()) / overlappedTimes.size());

                int day = mapClassDaytoGrid(time.getDay());

                if(conflictingTime != null) {
                    SectionPanel addedPanel = new AddedClassPanel(
                            timeSectionMap.get(conflictingTime), conflictingTime);
                    addedPanel.setHeight(panelSize);
                    
                    schedulePanels[day].add(addedPanel);

                    for(ClassTimeAdapter cta : overlappedTimes) {
                        if(cta != conflictingTime) {
                            SectionPanel panel = new ConflictPanel(
                                    timeSectionMap.get(cta),
                                    timeSectionMap.get(conflictingTime),
                                    cta);

                            panel.setHeight(panelSize);

                            lastPanel = panel;
                            schedulePanels[day].add(panel);
                        }
                    }
                } else {
                    for (ClassTimeAdapter cta : overlappedTimes) {
                        SectionPanel panel = new SelectedClassPanel(
                                timeSectionMap.get(cta),
                                cta);

                        panel.setHeight(panelSize);

                        lastPanel = panel;
                        schedulePanels[day].add(panel);
                    }
                }

                overlappedTimes.clear();
            }

            if(!confirmedSections.contains(timeSectionMap.get(classTime))) {
                sectionPanel = new SelectedClassPanel(timeSectionMap.get(classTime), classTime);
            }
            else{
                sectionPanel = new AddedClassPanel(timeSectionMap.get(classTime), classTime);
            }

            sectionPanel.setHeight(Integer.toString(sectionSize));

            int bumpSize = 0;

            if (lastAdapter != null && classTime.getDay() == lastAdapter.getDay()) {
                bumpSize = classTime.getStart() - lastAdapter.getEnd();
            } else {
                bumpSize = classTime.getStart() - this.startHour * 60;
            }

            if(bumpSize < 0) {
                if(classTime.getEnd() < lastAdapter.getEnd()) {
                    sectionPanel =
                            new ConflictPanel(timeSectionMap.get(classTime),
                            timeSectionMap.get(lastAdapter), classTime);

                    sectionPanel.setHeight(Integer.toString(classTime.getEnd() - classTime.getStart()));
                    schedulePanels[panelIndex].remove(lastPanel);
                    schedulePanels[panelIndex].add(sectionPanel);

                    lastPanel.setHeight(Integer.toString(lastAdapter.getEnd() - classTime.getEnd()));
                    schedulePanels[panelIndex].add(lastPanel);
                } else {
                    int newStart = lastAdapter.getEnd() - classTime.getStart();

                    if(lastPanel instanceof SelectedClassPanel) {
                        schedulePanels[panelIndex].remove(lastPanel);

                        if(sectionPanel instanceof SelectedClassPanel) {
                            lastPanel = new SelectedClassPanel(timeSectionMap.get(lastAdapter),
                                    lastAdapter);
                        } else {
                            lastPanel = new ConflictPanel(timeSectionMap.get(lastAdapter),
                                    timeSectionMap.get(classTime), classTime);
                        }

                        schedulePanels[panelIndex].add(lastPanel);
                    } else {
                        sectionPanel = new ConflictPanel(timeSectionMap.get(classTime),
                            timeSectionMap.get(lastAdapter), classTime);
                    }

                    sectionPanel.setHeight(Integer.toString(newStart));

                    schedulePanels[panelIndex].add(sectionPanel);
                }

            } else {
                DecoratorPanel bump = new DecoratorPanel();
                bump.setHeight(Integer.toString(bumpSize));
                schedulePanels[panelIndex].add(bump);
                schedulePanels[panelIndex].add(sectionPanel);
            }

            lastAdapter = classTime;
            lastPanel = sectionPanel;
        }
    }

    // Returns all class times of selected/added sections in order
    private ArrayList<ClassTimeAdapter> getAllClassTimes() {
        ArrayList<ClassTimeAdapter> allClassTimes = new ArrayList<ClassTimeAdapter>();

        for(SectionAdapter section : timedSections) {
            allClassTimes.addAll(section.getClassTimes());
        }

        Collections.sort(allClassTimes);

        return allClassTimes;
    }

    public void displaySection(SectionAdapter section) {

        if(confirmedSections.contains(section)) { return; }
        
        ArrayList<SectionAdapter> sections = new ArrayList<SectionAdapter>();
        sections.add(section);
        displaySections(sections);
    }

    // Displayed a section that has been selected but not added
    public void displaySections(ArrayList<SectionAdapter> sections){

        // Remove all currently displayed sections
        for(int c = timedSections.size() - 1; c >= 0; c--) {
            if (!confirmedSections.contains(timedSections.get(c))) {
                for (ClassTimeAdapter classTime : timedSections.get(c).getClassTimes()) {
                    timeSectionMap.remove(classTime);
                }

                timedSections.remove(c);
            }
        }

        // Remove online sections
        for(int c = onlineSections.size() - 1; c >= 0; c--) {
            if(!confirmedSections.contains(onlineSections.get(c))) {
                onlineSections.remove(c);
            }
        }

        // For all of the sections we're displaying
        for(SectionAdapter section : sections) {

            if(confirmedSections.contains(section)) {
                continue;
            }

            // If its an untimed course
            if (section.getClassTimes().size() == 0) {
                onlineSections.add(section);

                if (!onlineClassPanel.isOpen()) {
                    onlineClassPanel.setOpen(true);
                }
            } else {
                timedSections.add(section);
                for (ClassTimeAdapter classTime : section.getClassTimes()) {
                    timeSectionMap.put(classTime, section);
                }
            }
        }
        
        clearGrid();
        redrawGrid();
        clearOnlineGrid();
        redrawOnlineGrid();
    }

    private ArrayList<SectionPanel> getSectionPanels(SectionAdapter sa) {
        ArrayList<SectionPanel> sectionPanels = new ArrayList<SectionPanel>();

        for(VerticalPanel dayPanel : schedulePanels) {
            for(int c = 0; c < dayPanel.getWidgetCount(); c++) {
                if(dayPanel.getWidget(c) instanceof SectionPanel) {
                    SectionPanel panel = (SectionPanel)dayPanel.getWidget(c);

                    if(panel.getSectionAdapter() == sa) {
                        sectionPanels.add(panel);
                    }
                }
            }
        }

        return sectionPanels;
    }

    public ArrayList<SectionAdapter> getSelectedSections() {
        return confirmedSections;
    }

    // Determines if the given time falls within the range of the grid
    private boolean validTime(ClassTimeAdapter classTime){
        return classTime.getEnd()  <= (this.endHour * 60) &&
                classTime.getStart() >= (this.startHour * 60);
    }

    // Determines if the given day is displayed on the grid
    private boolean validDay(int classDay){
        switch(type){
            case WeekdaysOnly:
                return (classDay > 0 && classDay < 6);
            case AllDays:
                return (classDay >= 0 && classDay <=6);
            case WeekdaysAndSat:
                return (classDay > 0 && classDay <= 6);
            case WeekdaysAndSun:
                return (classDay >=0 && classDay < 6);
            default:
                return false;
        }
    }

    // Maps a day stored in a class time object to its index in the grid
    private int mapClassDaytoGrid(int classDay){
        switch(type){
            case WeekdaysOnly: // Fall Through
            case WeekdaysAndSat:
                return classDay - 1;
            case AllDays: // Fall through
            case WeekdaysAndSun: // Fall through
            default:
                return classDay;
        }
    }

    // Returns the days displayed for the selected grid type
    private String[] getDays(GridType type) {

        switch (type) {
            case WeekdaysOnly:
                return new String[]{"Mon", "Tues", "Weds", "Thurs", "Fri"};
            case AllDays:
                return new String[]{"Sun", "Mon", "Tues", "Weds", "Thurs", "Fri", "Sat"};
            case WeekdaysAndSat:
                return new String[]{"Mon", "Tues", "Weds", "Thurs", "Fri", "Sat"};
            case WeekdaysAndSun:
                return new String[]{"Sun", "Mon", "Tues", "Weds", "Thurs", "Fri"};
            default:
                return null;
        }
    }

    private void testGrid(){
        //ClassTimeAdapter time = new ClassTimeAdapter(2, 10*60, 12*60);

        //this.displayClassTime(time);
    }
}
