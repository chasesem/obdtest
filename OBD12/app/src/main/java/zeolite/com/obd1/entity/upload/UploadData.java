package zeolite.com.obd1.entity.upload;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zeolite on 16/2/29.
 */
public class UploadData {

    private Engine engines;
    private Oil oils;
    private Sensor sensor;

    public Engine getEngines() {
        return engines;
    }

    public void setEngines(Engine engines) {
        this.engines = engines;
    }

    public Oil getOils() {
        return oils;
    }

    public void setOils(Oil oils) {
        this.oils = oils;
    }

    public Sensor getSensor() {
        return sensor;
    }

    public void setSensor(Sensor sensor) {
        this.sensor = sensor;
    }
}
