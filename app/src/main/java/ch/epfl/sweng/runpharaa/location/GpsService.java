package ch.epfl.sweng.runpharaa.location;

import android.app.Service;
import android.content.Context;
import android.location.Location;

public abstract class GpsService extends Service {

    private static GpsService instance;

    /**
     * Get the instance of GpsService
     *
     * @return the instance of GpsService
     */
    public static GpsService getInstance() {
        if (instance == null)
            instance = new RealGpsService();
        return instance;
    }

    @Override
    public void onDestroy() {
        instance = null;
        super.onDestroy();
    }

    /**
     * Init the FakeGpsService
     *
     * @param s the FakeGpsService to init
     */
    public static void initFakeGps(FakeGpsService s) {
        if (instance == null)
            instance = s;
    }

    static Location currentLocation;

    public abstract Location getCurrentLocation();

    public abstract void setNewLocation(Context context, Location location);

    protected abstract void updateAndSendNewLocation(Location location);

    public void setTimeInterval(int interval) {
    }

    public void setMinTimeInterval(int interval) {
    }

    public void setMinDistanceInterval(int interval) {
    }
}
