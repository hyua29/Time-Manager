package com.app.cooper.time_manager.enums;

public enum NotificationType {
    ONCE,
    DAILY_NOTIFICATION,
    WEEKLY_NOTIFICATION,
    MONTHLY_NOTIFICATION,
    YEARLY_NOTIFICATION,
    NO_NOTIFICATION;

    public String getType() {
        switch (this) {
            case ONCE:
                return "Only Once";
            case DAILY_NOTIFICATION:
                return "Daily";
            case WEEKLY_NOTIFICATION:
                return "Weekly";
            case MONTHLY_NOTIFICATION:
                return "Monthly";
            case YEARLY_NOTIFICATION:
                return "Yearly";
            case NO_NOTIFICATION:
                return "Never";
            default:
                return "WTF";
        }
    }
}
