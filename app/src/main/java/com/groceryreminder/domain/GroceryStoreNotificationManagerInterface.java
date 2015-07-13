package com.groceryreminder.domain;

import android.content.Intent;

/**
 * Created by jim on 7/12/15.
 */
public interface GroceryStoreNotificationManagerInterface {
    boolean remindersExist();

    void saveNoticeDetails(String currentStoreName, long currentTime);

    void sendNotification(Intent intent);

    boolean noticeCanBeSent(String currentStoreName, long currentTime);
}
