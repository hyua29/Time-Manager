package com.app.cooper.time_manager.objects;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Parcel;
import android.os.Parcelable;

import com.android.databinding.library.baseAdapters.BR;
import com.app.cooper.time_manager.enums.NotificationType;

import java.util.Calendar;

public class Event extends BaseObservable implements Parcelable {

    private int id;
    private String eventName;
    private int startYear;
    private int startMonth;
    private int startDay;
    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;
    private String description;
    private String location;
    private String eventType;
    private String notificationType;

    public Event(int id) {
        this.id = id;
        this.eventName = "";
        Calendar c = Calendar.getInstance();
        this.setStartYear(c.get(Calendar.YEAR));
        this.setStartMonth(c.get(Calendar.MONTH));
        this.setStartDay(c.get(Calendar.DAY_OF_MONTH));
        this.setStartHour(c.get(Calendar.HOUR_OF_DAY));
        this.setStartMinute(c.get(Calendar.MINUTE));
        this.setEndHour(c.get(Calendar.HOUR_OF_DAY));
        this.setEndMinute(c.get(Calendar.MINUTE));
        this.setEventType("work");
        this.setNotificationType(NotificationType.ONCE.getType());
        this.setDescription("");
        this.setLocation("");
    }

    public Event() {

        this.eventName = "";
        Calendar c = Calendar.getInstance();
        this.setStartYear(c.get(Calendar.YEAR));
        this.setStartMonth(c.get(Calendar.MONTH));
        this.setStartDay(c.get(Calendar.DAY_OF_MONTH));
        this.setStartHour(c.get(Calendar.HOUR_OF_DAY));
        this.setStartMinute(c.get(Calendar.MINUTE));
        this.setEndHour(c.get(Calendar.HOUR_OF_DAY));
        this.setEndMinute(c.get(Calendar.MINUTE));
        this.setEventType("work");
        this.setNotificationType(NotificationType.ONCE.getType());
        this.setDescription("");
        this.setLocation("");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Bindable
    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
        //notifyPropertyChanged(BR.eventName);

        //notifyPropertyChanged(BR.eventName);
    }

    public int getStartYear() {
        return startYear;
    }

    public void setStartYear(int startYear) {
        this.startYear = startYear;
    }

    public int getStartMonth() {
        return startMonth;
    }

    public void setStartMonth(int startMonth) {
        this.startMonth = startMonth;
    }

    public int getStartDay() {
        return startDay;
    }

    public void setStartDay(int startDay) {
        this.startDay = startDay;
    }

    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(int startMinute) {
        this.startMinute = startMinute;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public void setEndMinute(int endMinute) {
        this.endMinute = endMinute;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMonthString() {
        return parseMonth(this.startMonth);
    }

    private String parseMonth(int monthInt) {
        String month;
        switch (monthInt+1) {
            case 1:
                month = "Jan";
                break;
            case 2:
                month = "Feb";
                break;
            case 3:
                month = "Mar";
                break;
            case 4:
                month = "Apr";
                break;
            case 5:
                month = "May";
                break;
            case 6:
                month = "Jun";
                break;
            case 7:
                month = "Jul";
                break;
            case 8:
                month = "Aug";
                break;
            case 9:
                month = "Sep";
                break;
            case 10:
                month = "Otc";
                break;
            case 11:
                month = "Nov";
                break;
            case 0:
                month = "Dec";
                break;
            default:
                month = "error";
                break;
        }
        return month;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventName='" + eventName + '\'' +
                ", startYear='" + startYear + '\'' +
                ", startMonth='" + startMonth + '\'' +
                ", startDay='" + startDay + '\'' +
                ", startHour='" + startHour + '\'' +
                ", startMinute='" + startMinute + '\'' +
                ", endHour='" + endHour + '\'' +
                ", endMinute='" + endMinute + '\'' +
                ", description='" + description + '\'' +
                ", location='" + location + '\'' +
                ", eventType='" + eventType + '\'' +
                ", notificationType='" + notificationType + '\'' +
                '}';
    }

    public String getDate(){
        return String.format("%s %s %s", this.getStartYear(), this.getMonthString(), this.getStartDay());
    }

    public String getStartTime() {
        return String.format("%s:%s", this.getStartHour(), this.getStartMinute());
    }

    public String getEndTime() {
        return String.format("%s:%s", this.getEndHour(), this.getEndMinute());
    }

    @Override
    public void notifyPropertyChanged(int fieldId) {
        super.notifyPropertyChanged(fieldId);
        System.out.println("notified");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(this.id);
        dest.writeString(this.eventName);
        dest.writeInt(this.startYear);
        dest.writeInt(this.startMonth);
        dest.writeInt(this.startDay);
        dest.writeInt(this.startHour);
        dest.writeInt(this.startMinute);
        dest.writeInt(this.endHour);
        dest.writeInt(this.endMinute);
        dest.writeString(this.description);
        dest.writeString(this.location);
        dest.writeString(this.eventType);
        dest.writeString(this.notificationType);

    }

    protected Event(Parcel in) {
        id = in.readInt();
        eventName = in.readString();
        startYear = in.readInt();
        startMonth = in.readInt();
        startDay = in.readInt();
        startHour = in.readInt();
        startMinute = in.readInt();
        endHour = in.readInt();
        endMinute = in.readInt();
        description = in.readString();
        location = in.readString();
        eventType = in.readString();
        notificationType = in.readString();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
}
