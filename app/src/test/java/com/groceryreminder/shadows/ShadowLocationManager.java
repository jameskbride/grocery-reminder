package com.groceryreminder.shadows;

import android.app.PendingIntent;
import android.location.LocationManager;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.RealObject;
import org.robolectric.internal.Shadow;

import java.util.ArrayList;
import java.util.List;

@Implements(LocationManager.class)
public class ShadowLocationManager extends org.robolectric.shadows.ShadowLocationManager {

    @RealObject
    private LocationManager locationManager;

    List<ProximityAlert> proximityAlerts = new ArrayList<ProximityAlert>();

    @Implementation
    public void addProximityAlert(double latitude, double longitude,
                                  float radius, long expiration, PendingIntent intent) {
        proximityAlerts.add(new ProximityAlert(latitude, longitude, radius, expiration, intent));
        Shadow.directlyOn(locationManager, LocationManager.class).addProximityAlert(latitude, longitude, radius, expiration, intent);
    }

    @Implementation
    public void removeProximityAlert(PendingIntent intent) {
        for (ProximityAlert proximityAlert : proximityAlerts) {
            if (proximityAlert.pendingIntent.equals(intent)) {
                proximityAlerts.remove(proximityAlert);
            }
        }

        Shadow.directlyOn(locationManager, LocationManager.class).removeProximityAlert(intent);
    }

    public List<ProximityAlert> getProximityAlerts() {
        return proximityAlerts;
    }

    public class ProximityAlert {

        private double latitude;
        private double longitude;
        private float radius;
        private long expiration;
        private PendingIntent pendingIntent;

        public ProximityAlert(double latitude, double longitude,
                              float radius, long expiration, PendingIntent intent) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.radius = radius;
            this.expiration = expiration;
            this.pendingIntent = intent;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public float getRadius() {
            return radius;
        }

        public void setRadius(float radius) {
            this.radius = radius;
        }

        public long getExpiration() {
            return expiration;
        }

        public void setExpiration(long expiration) {
            this.expiration = expiration;
        }

        public PendingIntent getPendingIntent() {
            return pendingIntent;
        }

        public void setPendingIntent(PendingIntent pendingIntent) {
            this.pendingIntent = pendingIntent;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ProximityAlert that = (ProximityAlert) o;

            if (expiration != that.expiration) return false;
            if (Double.compare(that.latitude, latitude) != 0) return false;
            if (Double.compare(that.longitude, longitude) != 0) return false;
            if (Float.compare(that.radius, radius) != 0) return false;
            if (pendingIntent != null ? !pendingIntent.equals(that.pendingIntent) : that.pendingIntent != null)
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            temp = Double.doubleToLongBits(latitude);
            result = (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(longitude);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            result = 31 * result + (radius != +0.0f ? Float.floatToIntBits(radius) : 0);
            result = 31 * result + (int) (expiration ^ (expiration >>> 32));
            result = 31 * result + (pendingIntent != null ? pendingIntent.hashCode() : 0);
            return result;
        }
    }
}
