package com.groceryreminder.domain;

import android.content.Intent;

public interface GroceryStoreNotificationManagerInterface {
    void saveNoticeDetails(String currentStoreName, long currentTime);

    void sendNotification(Intent intent);

    boolean noticeCanBeSent(String currentStoreName, long currentTime);
}
